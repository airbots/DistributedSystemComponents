package edu.unl.hcc.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A base class for running a Unix command.
 *
 * <code>Shell</code> can be used to run unix commands like <code>du</code> or
 * <code>df</code>. It also offers facilities to gate commands by 
 * time-intervals.
 *
 * Created by Chen He 5/30/2017 refer to Apache Hadoop code about running Shell command
 */
abstract public class ShellUtils {

    public static final Log LOG = LogFactory.getLog(ShellUtils.class);

    /**Time after which the executing script would be timedout*/
    protected long timeOutInterval = 0L;
    /** If or not script timed out*/
    private AtomicBoolean timedOut;

    private long    interval;   // refresh interval in msec
    private long    lastTime;   // last time the command was performed
    final private boolean redirectErrorStream; // merge stdout and stderr
    private Map<String, String> environment; // env for the command execution
    private File dir;
    private Process process; // sub process used to execute the command
    private int exitCode;

    /**If or not script finished executing*/
    private volatile AtomicBoolean completed;

    public ShellUtils() {
        this(0L);
    }

    public ShellUtils(long interval) {
        this(interval, false);
    }

    /**
     * @param interval the minimum duration to wait before re-executing the
     *        command.
     */
    public ShellUtils(long interval, boolean redirectErrorStream) {
        this.interval = interval;
        this.lastTime = (interval<0) ? 0 : -interval;
        this.redirectErrorStream = redirectErrorStream;
    }

    /** set the environment for the command
     * @param env Mapping of environment variables
     */
    protected void setEnvironment(Map<String, String> env) {
        this.environment = env;
    }

    /** set the working directory
     * @param dir The directory where the command would be executed
     */
    protected void setWorkingDirectory(File dir) {
        this.dir = dir;
    }

    /** check to see if a command needs to be executed and execute if needed */
    protected void run() throws IOException {
        if (lastTime + interval > System.currentTimeMillis())
            return;
        exitCode = 0; // reset for next run
        runCommand();
    }

    /** Run a command */
    private void runCommand() throws IOException {
        ProcessBuilder builder = new ProcessBuilder(getExecString());
        Timer timeOutTimer = null;
        ShellTimeoutTimerTask timeoutTimerTask;
        timedOut = new AtomicBoolean(false);
        completed = new AtomicBoolean(false);

        if (environment != null) {
            builder.environment().putAll(this.environment);
        }
        if (dir != null) {
            builder.directory(this.dir);
        }

        builder.redirectErrorStream(redirectErrorStream);

        process = builder.start();

        if (timeOutInterval > 0) {
            timeOutTimer = new Timer("Shell command timeout");
            timeoutTimerTask = new ShellTimeoutTimerTask(
                    this);
            //One time scheduling.
            timeOutTimer.schedule(timeoutTimerTask, timeOutInterval);
        }
        final BufferedReader errReader =
                new BufferedReader(new InputStreamReader(
                        process.getErrorStream(), Charset.defaultCharset()));
        BufferedReader inReader =
                new BufferedReader(new InputStreamReader(
                        process.getInputStream(), Charset.defaultCharset()));
        final StringBuffer errMsg = new StringBuffer();

        // read error and input streams as this would free up the buffers
        // free the error stream buffer
        Thread errThread = new Thread() {
            @Override
            public void run() {
                try {
                    String line = errReader.readLine();
                    while((line != null) && !isInterrupted()) {
                        errMsg.append(line);
                        errMsg.append(System.getProperty("line.separator"));
                        line = errReader.readLine();
                    }
                } catch(IOException ioe) {
                    LOG.warn("Error reading the error stream", ioe);
                }
            }
        };
        try {
            errThread.start();
        } catch (IllegalStateException ignored) {
        } catch (OutOfMemoryError oe) {
            LOG.error("Caught " + oe + ". One possible reason is that ulimit"
                    + " setting of 'max user processes' is too low. If so, do"
                    + " 'ulimit -u <largerNum>' and try again.");
            throw oe;
        }
        try {
            parseExecResult(inReader); // parse the output
            // clear the input stream buffer
            String line = inReader.readLine();
            while(line != null) {
                line = inReader.readLine();
            }
            // wait for the process to finish and check the exit code
            exitCode  = process.waitFor();
            // make sure that the error thread exits
            joinThread(errThread);
            completed.set(true);
            //the timeout thread handling
            //taken care in finally block
            if (exitCode != 0) {
                throw new ExitCodeException(exitCode, errMsg.toString());
            }
        } catch (InterruptedException ie) {
            throw new IOException(ie.toString());
        } finally {
            if (timeOutTimer != null) {
                timeOutTimer.cancel();
            }
            // close the input stream
            try {
                // JDK 7 tries to automatically drain the input streams for us
                // when the process exits, but since close is not synchronized,
                // it creates a race if we close the stream first and the same
                // fd is recycled.  the stream draining thread will attempt to
                // drain that fd!!  it may block, OOM, or cause bizarre behavior
                // see: https://bugs.openjdk.java.net/browse/JDK-8024521
                //      issue is fixed in build 7u60
                InputStream stdout = process.getInputStream();
                synchronized (stdout) {
                    inReader.close();
                }
            } catch (IOException ioe) {
                LOG.warn("Error while closing the input stream", ioe);
            }
            if (!completed.get()) {
                errThread.interrupt();
                joinThread(errThread);
            }
            try {
                InputStream stderr = process.getErrorStream();
                synchronized (stderr) {
                    errReader.close();
                }
            } catch (IOException ioe) {
                LOG.warn("Error while closing the error stream", ioe);
            }
            process.destroy();
            lastTime = System.currentTimeMillis();
        }
    }

    private static void joinThread(Thread t) {
        while (t.isAlive()) {
            try {
                t.join();
            } catch (InterruptedException ie) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Interrupted while joining on: " + t, ie);
                }
                t.interrupt(); // propagate interrupt
            }
        }
    }

    /** return an array containing the command name & its parameters */
    protected abstract String[] getExecString();

    /** Parse the execution result */
    protected abstract void parseExecResult(BufferedReader lines)
            throws IOException;

    /** get the current sub-process executing the given command
     * @return process executing the command
     */
    public Process getProcess() {
        return process;
    }

    /** get the exit code
     * @return the exit code of the process
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * This is an IOException with exit code added.
     */
    public static class ExitCodeException extends IOException {
        private final int exitCode;

        public ExitCodeException(int exitCode, String message) {
            super(message);
            this.exitCode = exitCode;
        }

        public int getExitCode() {
            return exitCode;
        }

        @Override
        public String toString() {
            final StringBuilder sb =
                    new StringBuilder("ExitCodeException ");
            sb.append("exitCode=").append(exitCode)
                    .append(": ");
            sb.append(super.getMessage());
            return sb.toString();
        }
    }

    public interface CommandExecutor {

        void execute() throws IOException;

        int getExitCode() throws IOException;

        String getOutput() throws IOException;

        void close();

    }

    /**
     * A simple shell command executor.
     *
     * <code>ShellCommandExecutor</code>should be used in cases where the output
     * of the command needs no explicit parsing and where the command, working
     * directory and the environment remains unchanged. The output of the command
     * is stored as-is and is expected to be small.
     */
    public static class ShellCommandExecutor extends ShellUtils
            implements CommandExecutor {

        private String[] command;
        private StringBuffer output;

        public ShellCommandExecutor(String commandString){
            this(commandString.split("\\s+"));
        }

        public ShellCommandExecutor(String[] execString) {
            this(execString, null);
        }

        public ShellCommandExecutor(String[] execString, File dir) {
            this(execString, dir, null);
        }

        public ShellCommandExecutor(String[] execString, File dir,
                                    Map<String, String> env) {
            this(execString, dir, env , 0L);
        }

        /**
         * Create a new instance of the ShellCommandExecutor to execute a command.
         *
         * @param execString The command to execute with arguments
         * @param dir If not-null, specifies the directory which should be set
         *            as the current working directory for the command.
         *            If null, the current working directory is not modified.
         * @param env If not-null, environment of the command will include the
         *            key-value pairs specified in the map. If null, the current
         *            environment is not modified.
         * @param timeout Specifies the time in milliseconds, after which the
         *                command will be killed and the status marked as timedout.
         *                If 0, the command will not be timed out.
         */
        public ShellCommandExecutor(String[] execString, File dir,
                                    Map<String, String> env, long timeout) {
            command = execString.clone();
            if (dir != null) {
                setWorkingDirectory(dir);
            }
            if (env != null) {
                setEnvironment(env);
            }
            timeOutInterval = timeout;
        }


        /** Execute the shell command. */
        public void execute() throws IOException {
            this.run();
        }

        @Override
        public String[] getExecString() {
            return command;
        }

        @Override
        protected void parseExecResult(BufferedReader lines) throws IOException {
            output = new StringBuffer();
            char[] buf = new char[512];
            int nRead;
            while ( (nRead = lines.read(buf, 0, buf.length)) > 0 ) {
                output.append(buf, 0, nRead);
            }
        }

        /** Get the output of the shell command.*/
        public String getOutput() {
            return (output == null) ? "" : output.toString();
        }

        /**
         * Returns the commands of this instance.
         * Arguments with spaces in are presented with quotes round; other
         * arguments are presented raw
         *
         * @return a string representation of the object.
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            String[] args = getExecString();
            for (String s : args) {
                if (s.indexOf(' ') >= 0) {
                    builder.append('"').append(s).append('"');
                } else {
                    builder.append(s);
                }
                builder.append(' ');
            }
            return builder.toString();
        }

        public void close() {
        }
    }

    /**
     * To check if the passed script to shell command executor timed out or
     * not.
     *
     * @return if the script timed out.
     */
    public boolean isTimedOut() {
        return timedOut.get();
    }

    /**
     * Set if the command has timed out.
     *
     */
    private void setTimedOut() {
        this.timedOut.set(true);
    }

    /**
     * Timer which is used to timeout scripts spawned off by shell.
     */
    private static class ShellTimeoutTimerTask extends TimerTask {

        private ShellUtils shell;

        public ShellTimeoutTimerTask(ShellUtils shell) {
            this.shell = shell;
        }

        @Override
        public void run() {
            Process p = shell.getProcess();
            try {
                p.exitValue();
            } catch (Exception e) {
                //Process has not terminated.
                //So check if it has completed
                //if not just destroy it.
                if (p != null && !shell.completed.get()) {
                    shell.setTimedOut();
                    p.destroy();
                }
            }
        }
    }
}
