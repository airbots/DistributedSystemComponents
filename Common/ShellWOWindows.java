package utils;

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
 * Created by Chen He 5/30/2017
 */
abstract public class Shell {

    public static final Log LOG = LogFactory.getLog(Shell.class);

    private static boolean IS_JAVA7_OR_ABOVE =
            System.getProperty("java.version").substring(0, 3).compareTo("1.7") >= 0;

    public static boolean isJava7OrAbove() {
        return IS_JAVA7_OR_ABOVE;
    }

    static String connectCommands(CharSequence separator, String[] strings) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : strings) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    /** a Unix command to get the current user's name */
    public final static String USER_NAME_COMMAND = "whoami";

    /** Windows CreateProcess synchronization object */
    public static final Object WindowsProcessLaunchLock = new Object();

    // OSType detection

    public enum OSType {
        OS_TYPE_LINUX,
        OS_TYPE_SOLARIS,
        OS_TYPE_MAC,
        OS_TYPE_FREEBSD,
        OS_TYPE_OTHER
    }

    public static final OSType osType = getOSType();

    static private OSType getOSType() {
        String osName = System.getProperty("os.name");
        if (osName.contains("SunOS") || osName.contains("Solaris")) {
            return OSType.OS_TYPE_SOLARIS;
        } else if (osName.contains("Mac")) {
            return OSType.OS_TYPE_MAC;
        } else if (osName.contains("FreeBSD")) {
            return OSType.OS_TYPE_FREEBSD;
        } else if (osName.startsWith("Linux")) {
            return OSType.OS_TYPE_LINUX;
        } else {
            // Some other form of Unix
            return OSType.OS_TYPE_OTHER;
        }
    }

    // Helper static vars for each platform
    public static final boolean SOLARIS = (osType == OSType.OS_TYPE_SOLARIS);
    public static final boolean MAC     = (osType == OSType.OS_TYPE_MAC);
    public static final boolean FREEBSD = (osType == OSType.OS_TYPE_FREEBSD);
    public static final boolean LINUX   = (osType == OSType.OS_TYPE_LINUX);
    public static final boolean OTHER   = (osType == OSType.OS_TYPE_OTHER);

    public static final boolean PPC_64
            = System.getProperties().getProperty("os.arch").contains("ppc64");

    /** a Unix command to get the current user's groups list */
    public static String[] getGroupsCommand() {
        return new String[]{"bash", "-c", "groups"};
    }

    /**
     * a Unix command to get a given user's groups list.
     * If the OS is not WINDOWS, the command will get the user's primary group
     * first and finally get the groups list which includes the primary group.
     * i.e. the user's primary group will be included twice.
     */
    public static String[] getGroupsForUserCommand(final String user) {
        //'groups username' command return is non-consistent across different unixes
        return new String [] {"bash", "-c", "id -gn " + user
                + "&& id -Gn " + user};
    }

    /** a Unix command to get a given netgroup's user list */
    public static String[] getUsersForNetgroupCommand(final String netgroup) {
        //'groups username' command return is non-consistent across different unixes
        return new String [] {"bash", "-c", "getent netgroup " + netgroup};
    }

    /** Return a command to get permission information. */
    public static String[] getGetPermissionCommand() {
        return new String[] { "/bin/ls", "-ld" };
    }

    /** Return a command to set permission */
    public static String[] getSetPermissionCommand(String perm, boolean recursive) {
        if (recursive) {
            return new String[] { "chmod", "-R", perm };
        } else {
            return new String[] { "chmod", perm };
        }
    }

    /**
     * Return a command to set permission for specific file.
     *
     * @param perm String permission to set
     * @param recursive boolean true to apply to all sub-directories recursively
     * @param file String file to set
     * @return String[] containing command and arguments
     */
    public static String[] getSetPermissionCommand(String perm, boolean recursive,
                                                   String file) {
        String[] baseCmd = getSetPermissionCommand(perm, recursive);
        String[] cmdWithFile = Arrays.copyOf(baseCmd, baseCmd.length + 1);
        cmdWithFile[cmdWithFile.length - 1] = file;
        return cmdWithFile;
    }

    /** Return a command to set owner */
    public static String[] getSetOwnerCommand(String owner) {
        return new String[] { "chown", owner };
    }

    /** Return a command to create symbolic links */
    public static String[] getSymlinkCommand(String target, String link) {
        return new String[] { "ln", "-s", target, link };
    }

    /** Return a command to read the target of the a symbolic link*/
    public static String[] getReadlinkCommand(String link) {
        return new String[] { "readlink", link };
    }

    /** Return a command for determining if process with specified pid is alive. */
    public static String[] getCheckProcessIsAliveCommand(String pid) {
        return new String[] { "kill", "-0", isSetsidAvailable ? "-" + pid : pid };
    }

    /** Return a command to send a signal to a given pid */
    public static String[] getSignalKillCommand(int code, String pid) {
        return new String[] { "kill", "-" + code, isSetsidAvailable ? "-" + pid : pid };
    }

    /** Return a regular expression string that match environment variables */
    public static String getEnvironmentVariableRegex() {
        return "\\$([A-Za-z_][A-Za-z0-9_]*)";
    }

    /**
     * Returns a File referencing a script with the given basename, inside the
     * given parent directory.  The file extension is inferred by platform: ".cmd"
     * on Windows, or ".sh" otherwise.
     *
     * @param parent File parent directory
     * @param basename String script file basename
     * @return File referencing the script in the directory
     */
    public static File appendScriptExtension(File parent, String basename) {
        return new File(parent, appendScriptExtension(basename));
    }

    /**
     * Returns a script file name with the given basename.  The file extension is
     * inferred by platform: ".cmd" on Windows, or ".sh" otherwise.
     *
     * @param basename String script file basename
     * @return String script file name
     */
    public static String appendScriptExtension(String basename) {
        return basename + ".sh";
    }

    /**
     * Returns a command to run the given script.  The script interpreter is
     * inferred by platform: cmd on Windows or bash otherwise.
     *
     * @param script File script to run
     * @return String[] command to run the script
     */
    public static String[] getRunScriptCommand(File script) {
        String absolutePath = script.getAbsolutePath();
        return new String[] { "/bin/bash", absolutePath };
    }

    /** a Unix command to set permission */
    public static final String SET_PERMISSION_COMMAND = "chmod";
    /** a Unix command to set owner */
    public static final String SET_OWNER_COMMAND = "chown";

    /** a Unix command to set the change user's groups list */
    public static final String SET_GROUP_COMMAND = "chgrp";
    /** a Unix command to create a link */
    public static final String LINK_COMMAND = "ln";
    /** a Unix command to get a link target */
    public static final String READ_LINK_COMMAND = "readlink";

    /**Time after which the executing script would be timedout*/
    protected long timeOutInterval = 0L;
    /** If or not script timed out*/
    private AtomicBoolean timedOut;

    public static final boolean isSetsidAvailable = isSetsidSupported();
    private static boolean isSetsidSupported() {
        ShellCommandExecutor shexec = null;
        boolean setsidSupported = true;
        try {
            String[] args = {"setsid", "bash", "-c", "echo $$"};
            shexec = new ShellCommandExecutor(args);
            shexec.execute();
        } catch (IOException ioe) {
            LOG.debug("setsid is not available on this machine. So not using it.");
            setsidSupported = false;
        } finally { // handle the exit code
            if (LOG.isDebugEnabled()) {
                LOG.debug("setsid exited with exit code "
                        + (shexec != null ? shexec.getExitCode() : "(null executor)"));
            }
        }
        return setsidSupported;
    }

    private long    interval;   // refresh interval in msec
    private long    lastTime;   // last time the command was performed
    final private boolean redirectErrorStream; // merge stdout and stderr
    private Map<String, String> environment; // env for the command execution
    private File dir;
    private Process process; // sub process used to execute the command
    private int exitCode;

    /**If or not script finished executing*/
    private volatile AtomicBoolean completed;

    public Shell() {
        this(0L);
    }

    public Shell(long interval) {
        this(interval, false);
    }

    /**
     * @param interval the minimum duration to wait before re-executing the
     *        command.
     */
    public Shell(long interval, boolean redirectErrorStream) {
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
        ShellTimeoutTimerTask timeoutTimerTask = null;
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
        } catch (IllegalStateException ise) {
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

    /**
     * Get the environment variable
     */
    public String getEnvironment(String env) {
        return environment.get(env);
    }

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
    public static class ShellCommandExecutor extends Shell
            implements CommandExecutor {

        private String[] command;
        private StringBuffer output;


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

        @Override
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
     * Static method to execute a shell command.
     * Covers most of the simple cases without requiring the user to implement
     * the <code>Shell</code> interface.
     * @param cmd shell command to execute.
     * @return the output of the executed command.
     */
    public static String execCommand(String ... cmd) throws IOException {
        return execCommand(null, cmd, 0L);
    }

    /**
     * Static method to execute a shell command.
     * Covers most of the simple cases without requiring the user to implement
     * the <code>Shell</code> interface.
     * @param env the map of environment key=value
     * @param cmd shell command to execute.
     * @param timeout time in milliseconds after which script should be marked timeout
     * @return the output of the executed command.o
     */

    public static String execCommand(Map<String, String> env, String[] cmd,
                                     long timeout) throws IOException {
        ShellCommandExecutor exec = new ShellCommandExecutor(cmd, null, env,
                timeout);
        exec.execute();
        return exec.getOutput();
    }

    /**
     * Static method to execute a shell command.
     * Covers most of the simple cases without requiring the user to implement
     * the <code>Shell</code> interface.
     * @param env the map of environment key=value
     * @param cmd shell command to execute.
     * @return the output of the executed command.
     */
    public static String execCommand(Map<String,String> env, String ... cmd)
            throws IOException {
        return execCommand(env, cmd, 0L);
    }

    /**
     * Timer which is used to timeout scripts spawned off by shell.
     */
    private static class ShellTimeoutTimerTask extends TimerTask {

        private Shell shell;

        public ShellTimeoutTimerTask(Shell shell) {
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
