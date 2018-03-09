package edu.unl.hcc.service.common;

import edu.unl.hcc.service.event.LifeCycleEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import edu.unl.hcc.service.exception.ServiceStateException;

/**
 *
 * This abstract class define the foundation of a service
 *
 * Created by Chen He on May 26, 2016.
 *
 */

public abstract class Service {

    private static final Log LOG = LogFactory.getLog(Service.class);

    /**
     * Service name.
     */
    protected final String name;

    /** service state */
    protected final ServiceStateModel stateModel;

    /**
     * Service start time. Will be zero until the service is started.
     */
    protected long startTime;

    /**
     * Incremental counter for task Id
     */
    protected AtomicInteger taskCounter = new AtomicInteger(0);

    /**
     * The cause of any failure -will be null.
     * if a service did not stop due to a failure.
     */
    protected Exception failureCause;

    /**
     * the state in which the service was when it failed.
     * Only valid when the service is stopped due to a failure
     */
    protected STATE failureState = null;

    /**
     * object used to co-ordinate {@link #waitForServiceToStop(long)}
     * across threads.
     */
    protected final AtomicBoolean terminationNotification =
            new AtomicBoolean(false);

    public enum STATE {
        /** Constructed but not initialized */
        NOTINITED(0, "NOTINITED"),

        /** Initialized but not started or stopped */
        INITED(1, "INITED"),

        /** started and not stopped */
        STARTED(2, "STARTED"),

        /** stopped. No further state transitions are permitted */
        STOPPED(3, "STOPPED");

        /**
         * An integer value for use in array lookup and JMX interfaces.
         * Although {@link Enum#ordinal()} could do this, explicitly
         * identify the numbers gives more stability guarantees over time.
         */
        private final int value;

        /**
         * A name of the state that can be used in messages
         */
        private final String statename;

        STATE(int value, String name) {
            this.value = value;
            this.statename = name;
        }

        /**
         * Get the integer value of a state
         * @return the numeric value of the state
         */
        public int getValue() {
            return value;
        }

        /**
         * Get the name of a state
         * @return the state's name
         */
        
        public String toString() {
            return statename;
        }
    }


    /**
     * The configuration. Will be null until the service is initialized.
     */
    private volatile Configuration config;

    /**
     * List of state change listeners; it is final to ensure
     * that it will never be null.
     */
    private final ServiceOperations.ServiceListeners listeners
            = new ServiceOperations.ServiceListeners();
    /**
     * Static listeners to all events across all services
     */
    private static ServiceOperations.ServiceListeners globalListeners
            = new ServiceOperations.ServiceListeners();



    /**
     * History of lifecycle transitions
     */
    protected final List<LifeCycleEvent> lifecycleHistory
            = new ArrayList<LifeCycleEvent>(5);

    /**
     * Map of blocking dependencies
     */
    private final Map<String,String> blockerMap = new HashMap<String, String>();

    private final Object stateChangeLock = new Object();

    /**
     * Construct the service.
     * @param name service name
     */
    public Service(String name) {
        this.name = name;
        stateModel = new ServiceStateModel(name);
    }

    
    public final STATE getServiceState() {
        return stateModel.getState();
    }

    
    public final synchronized Throwable getFailureCause() {
        return failureCause;
    }

    
    public synchronized STATE getFailureState() {
        return failureState;
    }

    /**
     * Set the configuration for this service.
     * This method is called during {@link #init(Configuration)}
     * and should only be needed if for some reason a service implementation
     * needs to override that initial setting -for example replacing
     * it with a new subclass of {@link Configuration}
     * @param conf new configuration.
     */
    protected void setConfig(Configuration conf) {
        this.config = conf;
    }

    /**
     * {@inheritDoc}
     * This invokes {@link #serviceInit}
     * @param conf the configuration of the service. This must not be null
     * @throws ServiceStateException if the configuration was null,
     * the state change not permitted, or something else went wrong
     */
    
    public void init(Configuration conf) {
        if (conf == null) {
            throw new ServiceStateException("Cannot initialize service "
                    + getName() + ": null configuration");
        }
        if (isInState(STATE.INITED)) {
            return;
        }
        synchronized (stateChangeLock) {
            if (enterState(STATE.INITED) != STATE.INITED) {
                setConfig(conf);
                try {
                    serviceInit(config);
                    if (isInState(STATE.INITED)) {
                        //if the service ended up here during init,
                        //notify the listeners
                        notifyListeners();
                    }
                } catch (Exception e) {
                    noteFailure(e);
                    ServiceOperations.stopQuietly(LOG, this);
                    throw ServiceStateException.convert(e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * @throws ServiceStateException if the current service state does not permit
     * this action
     */
    
    public void start() {
        if (isInState(STATE.STARTED)) {
            return;
        }
        //enter the started state
        synchronized (stateChangeLock) {
            if (stateModel.enterState(STATE.STARTED) != STATE.STARTED) {
                try {
                    startTime = System.currentTimeMillis();
                    serviceStart();
                    if (isInState(STATE.STARTED)) {
                        //if the service started (and isn't now in a later state), notify
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Service " + getName() + " is started");
                        }
                        notifyListeners();
                    }
                } catch (Exception e) {
                    noteFailure(e);
                    ServiceOperations.stopQuietly(LOG, this);
                    throw ServiceStateException.convert(e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    
    public void stop() {
        if (isInState(STATE.STOPPED)) {
            return;
        }
        synchronized (stateChangeLock) {
            if (enterState(STATE.STOPPED) != STATE.STOPPED) {
                try {
                    serviceStop();
                } catch (Exception e) {
                    //stop-time exceptions are logged if they are the first one,
                    noteFailure(e);
                    throw ServiceStateException.convert(e);
                } finally {
                    //report that the service has terminated
                    terminationNotification.set(true);
                    synchronized (terminationNotification) {
                        terminationNotification.notifyAll();
                    }
                    //notify anything listening for events
                    notifyListeners();
                }
            } else {
                //already stopped: note it
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ignoring re-entrant call to stop()");
                }
            }
        }
    }

    /**
     * Relay to {@link #stop()}
     * @throws IOException
     */
    
    public final void close() throws IOException {
        stop();
    }

    /**
     * Failure handling: record the exception
     * that triggered it -if there was not one already.
     * Services are free to call this themselves.
     * @param exception the exception
     */
    protected final void noteFailure(Exception exception) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("noteFailure " + exception, null);
        }
        if (exception == null) {
            //make sure failure logic doesn't itself cause problems
            return;
        }
        //record the failure details, and log it
        synchronized (this) {
            if (failureCause == null) {
                failureCause = exception;
                failureState = getServiceState();
                LOG.info("Service " + getName()
                                + " failed in state " + failureState
                                + "; cause: " + exception,
                        exception);
            }
        }
    }

    
    public final boolean waitForServiceToStop(long timeout) {
        boolean completed = terminationNotification.get();
        while (!completed) {
            try {
                synchronized(terminationNotification) {
                    terminationNotification.wait(timeout);
                }
                // here there has been a timeout, the object has terminated,
                // or there has been a spurious wakeup (which we ignore)
                completed = true;
            } catch (InterruptedException e) {
                // interrupted; have another look at the flag
                completed = terminationNotification.get();
            }
        }
        return terminationNotification.get();
    }

    /**
     * All initialization code needed by a service.
     *
     * This method will only ever be called once during the lifecycle of
     * a specific service instance.
     *
     * Implementations do not need to be synchronized as the logic
     * in {@link #init(Configuration)} prevents re-entrancy.
     *
     * The base implementation checks to see if the subclass has created
     * a new configuration instance, and if so, updates the base class value
     * @param conf configuration
     * @throws Exception on a failure -these will be caught,
     * possibly wrapped, and wil; trigger a service stop
     */
    protected void serviceInit(Configuration conf) throws Exception {
        if (conf != config) {
            LOG.debug("Config has been overridden during init");
            setConfig(conf);
        }
    }

    /**
     * Actions called during the INITED to STARTED transition.
     *
     * This method will only ever be called once during the lifecycle of
     * a specific service instance.
     *
     * Implementations do not need to be synchronized as the logic
     * in {@link #start()} prevents re-entrancy.
     *
     * @throws Exception if needed -these will be caught,
     * wrapped, and trigger a service stop
     */
    protected void serviceStart() throws Exception {

    }

    /**
     * Actions called during the transition to the STOPPED state.
     *
     * This method will only ever be called once during the lifecycle of
     * a specific service instance.
     *
     * Implementations do not need to be synchronized as the logic
     * in {@link #stop()} prevents re-entrancy.
     *
     * Implementations MUST write this to be robust against failures, including
     * checks for null references -and for the first failure to not stop other
     * attempts to shut down parts of the service.
     *
     * @throws Exception if needed -these will be caught and logged.
     */
    protected void serviceStop() throws Exception {

    }
    
    public String getName() {
        return name;
    }

    
    public synchronized Configuration getConfig() {
        return config;
    }

    
    public long getStartTime() {
        return startTime;
    }

    /**
     * Notify local and global listeners of state changes.
     * Exceptions raised by listeners are NOT passed up.
     */
    private void notifyListeners() {
        try {
            listeners.notifyListeners(this);
            globalListeners.notifyListeners(this);
        } catch (Throwable e) {
            LOG.warn("Exception while notifying listeners of " + this + ": " + e,
                    e);
        }
    }

    /**
     * Add a state change event to the lifecycle history
     */
    protected void recordLifecycleEvent() {
        LifeCycleEvent event = new LifeCycleEvent();
        event.time = System.currentTimeMillis();
        event.state = getServiceState();
        lifecycleHistory.add(event);
    }

    
    public synchronized List<LifeCycleEvent> getLifecycleHistory() {
        return new ArrayList<LifeCycleEvent>(lifecycleHistory);
    }

    /**
     * Enter a state; record this via {@link #recordLifecycleEvent}
     * and log at the info level.
     * @param newState the proposed new state
     * @return the original state
     * it wasn't already in that state, and the state model permits state re-entrancy.
     */
    private STATE enterState(STATE newState) {
        assert stateModel != null : "null state in " + name + " " + this.getClass();
        STATE oldState = stateModel.enterState(newState);
        if (oldState != newState) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(
                        "Service: " + getName() + " entered state " + getServiceState());
            }
            recordLifecycleEvent();
        }
        return oldState;
    }

    
    public final boolean isInState(STATE expected) {
        return stateModel.isInState(expected);
    }

    public AtomicInteger getTaskCounter(){
        return this.taskCounter;
    }

    public int increaseTaskCounter(){
        this.taskCounter.incrementAndGet();
        return this.taskCounter.get();
    }

    public String toString() {
        return "Service " + name + " in state " + stateModel;
    }
}