
package edu.unl.hcc.service.rrd;

import edu.unl.hcc.service.common.Configuration;
import edu.unl.hcc.service.common.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * RrdService is a basic service that provide daemon for monitoring app to write to Round
 * Robin Database through shell
 *
 * Created by Chen He on May 26, 2016.
 *
 */


public class RrdService extends Service {

    private static final Log LOG = LogFactory.getLog(RrdService.class);
    public enum TASK_TYPE {
        CREATE_TASK,
        UPDATE_TASK,
        FETCH_TASK
    }

    private Configuration config;
    private long updatePeriod;
    private String rrdHome;
    ThreadPoolExecutor executor;

    /**
     * Construct the service.
     *
     * @param name service name
     */
    public RrdService(String name) {
        super(name);
    }

    /**
     * Construct service with given config and name
     * @param conf
     * @param name
     */
    public RrdService(Configuration conf, String name) {
        super(name);
        this.config = conf;
        this.rrdHome = this.config.get("rrd.home", "/tmp/localhost/rrd");
    }

    public void init(Configuration conf){

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
                this.config.getInt("rrd.concurrency",1));
        updatePeriod = conf.getInt("rrd.update.period",15);

        //change state to INITED
        this.stateModel.enterState(STATE.INITED);
        recordLifecycleEvent();
        LOG.debug("Lifecycle History: " + getLifecycleHistory().toString());
    }

    public void start(){

        //init startTime
        this.startTime = System.currentTimeMillis();

        //change state to STARTED
        this.stateModel.enterState(STATE.STARTED);
        recordLifecycleEvent();
        LOG.debug("Lifecycle History: " + getLifecycleHistory().toString());

        //create rrdPath
        LOG.info("New RrdTask to create rrdPath: " + rrdHome);
        RrdTask initTask = new RrdTask(TASK_TYPE.CREATE_TASK,
                RrdUtils.Metric_Type.METRIC_TPYE_BYTE_IN,
                rrdHome);
        initTask.setTaskId(startTime,increaseTaskCounter());
        executor.submit(initTask);

        String update;
        //start daemon
        while(this.getServiceState() == STATE.STARTED){
            //issue multiple threads to update rrd file
            //each thread will in charge of one server
            try {
                update = RrdUtils.getMetricValue();
                RrdTask updateTask = new RrdTask(TASK_TYPE.UPDATE_TASK,
                        RrdUtils.Metric_Type.METRIC_TPYE_BYTE_IN,
                        System.currentTimeMillis(),
                        update,
                        rrdHome);
                updateTask.setTaskId(startTime, increaseTaskCounter());
                //TODO handle return result and possible exception
                Future<Integer> result = executor.submit(updateTask);
                LOG.info("Successfully update " + rrdHome + " with value " + update);
                Thread.sleep(updatePeriod * 1000);
            } catch (InterruptedException e){
                LOG.warn("Encounter interruption when update " + rrdHome + " " + e.getMessage());
                e.printStackTrace();
            } catch (IOException ioe){
                LOG.warn("Encounter IOException when update " + rrdHome + " " + ioe.getMessage());
                ioe.printStackTrace();
            }
        }
    }

    public void stop(){
        //TODO join all running threads if multiple threads

        //log service stop
        int stopTimeout = config.getInt("rrd.stop.timeout", 60);
        LOG.info("RrdService will stop in " + stopTimeout + " seconds!");
        //change service state to STOPPED
        if (this.waitForServiceToStop(stopTimeout)) {
            this.stateModel.enterState(STATE.STOPPED);
        }
    }

    public static void main(String[] args){
        Configuration conf = new Configuration();
        RrdService service = new RrdService(conf, "RRDService");
        service.init(conf);
        try {
            service.start();
        } catch (Exception e){
            LOG.error("RrdService Encounter exception: ", e);
            LOG.debug("Lifecycle record history: " + service.getLifecycleHistory().toString());
            service.stop();
            e.printStackTrace();
        }
    }
}