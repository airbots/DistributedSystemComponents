package edu.unl.hcc.service.rrd;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.exception.NotANumberException;
import org.codehaus.jackson.map.ObjectMapper;

import edu.unl.hcc.shell.ShellUtils;

/**
 * This is class for java code to do I/O to rrd file in local machine
 * Created by chehe on 2017/5/29.
 */
public class RrdUtils {

    private static final Log LOG = LogFactory.getLog(RrdUtils.class);
    //for test
    static ObjectMapper objectMapper = new ObjectMapper();

    public enum Metric_Type {
        /**
         * Number of brokers in cluster
         */
        METRIC_BROKER(0,"BROKER"),

        /**
         * Number of byte input
         */
        METRIC_TPYE_BYTE_IN(1,"BYTE_IN");

        private final int value;

        /**
         * A name of the metric that can be used in messages
         */
        private final String metricName;

        Metric_Type(int value, String name) {
            this.value = value;
            this.metricName = name;
        }

        /**
         * Get the integer value of a metric
         * @return the numeric value of the metric
         */
        public int getValue() {
            return value;
        }

        /**
         * Get the name of a metric
         * @return the metric's name
         */

        public String toString() {
            return metricName;
        }
    }

    public static void createRrdFile(String rrdPath, Metric_Type metricType)
            throws IOException {

        checkPath(rrdPath);

        //create Rrd file through RRDTOOL using shell command:
        //1. compose shell command
        StringBuilder sb = new StringBuilder("rrdtool create " + rrdPath);
        //TODO need to add all metrics
        switch (metricType) {
            case METRIC_TPYE_BYTE_IN: sb.append("/byte_in.rrd " +
                    "--step 15 --start " + (System.currentTimeMillis()/1000-10) + " " +
                    "DS:byte_in:GAUGE:120:0:12500000 " +
                    "RRA:MAX:0.5:1:5856 " +
                    "RRA:AVERAGE:0.5:4:20160 " +
                    "RRA:AVERAGE:0.5:40:52704");
                break;
            default: sb.append("/broker_topics.rrd --start " +
                    "now DS:byte_in:GAUGE:30:0:1024 " +
                    "RRA:AVERAGE:0.5:120:24 RRA:AVERAGE:0.5:2880:31");
                break;
        }

        //2. create rrd file in given path
        ShellUtils.ShellCommandExecutor shCextor =
                new ShellUtils.ShellCommandExecutor(sb.toString());
        LOG.debug("Command to create rrd file: " + shCextor.toString());

        try {
            shCextor.execute();
        } catch (IOException e) {
            LOG.error("Failed to create rrd file " + rrdPath);
            System.out.println("Failed to create rrd file " + rrdPath);
            e.printStackTrace();
        }
    }


    public static void updateRrd(String rrdPath, Metric_Type valueType, String value)
            throws IOException {
        //boundary conditions
        checkPath(rrdPath);
        if (null == value || !value.contains(":")){
            LOG.error("No valid value to update: "+ value);
            System.out.println("No valid value to update: "+ value);
            return;
        }

        StringBuilder sb = new StringBuilder("rrdtool update " + rrdPath);
        switch (valueType) {
            case METRIC_TPYE_BYTE_IN:
                sb.append("/byte_in.rrd " + value);
                break;
            default:
                sb.append("/broker_topics.rrd " + value);
                break;
        }

        //System.out.println("Command to update is: " + sb.toString());
        //update rrd file
        ShellUtils.ShellCommandExecutor shCextor =
                new ShellUtils.ShellCommandExecutor(sb.toString());

        try {
            shCextor.execute();
        } catch (IOException e) {
            LOG.error("Failed to update rrd file " + rrdPath);
            System.out.println("Failed to update rrd file " + rrdPath);
            e.printStackTrace();
        }
    }

    public static String createGraph(String imgPath, String rrdPath, int imgWidth,
                              int imgHeight, Metric_Type metric, long start, long end)
            throws IOException {

        //boundary condition
        checkPath(imgPath);
        checkPath(rrdPath);
        if (end < start) return null;

        if(imgWidth <= 0) imgWidth = 500;
        if(imgHeight <= 0) imgHeight = 300;

        StringBuilder sb = new StringBuilder("rrdtool graph " + imgPath);
        switch (metric) {
            case METRIC_BROKER:
                sb.append("/byte_in.png --start " + start +
                        "--end " + end + "--vertical-label Topics " +
                        "DEF:ds0="+rrdPath+"/broker_topics.rrd:ds0:AVERAGE");
                break;
            default:
                sb.append("/broker_topics.png --start " + start +
                        "--end " + end + "--vertical-label Topics " +
                        "DEF:ds0="+rrdPath+"/broker_topics.rrd:ds0:AVERAGE");
                break;
        }

        //2. create rrd file in given path
        ShellUtils.ShellCommandExecutor shCextor =
                new ShellUtils.ShellCommandExecutor(sb.toString());
        try {
            shCextor.execute();
        } catch (IOException e) {
            LOG.error("Failed to create rrd graph file " + rrdPath);
            System.out.println("Failed to create rrd graph file " + rrdPath);
            e.printStackTrace();
        }

        return imgPath + "/broker_topics.png";
    }

    public static String[] fetchRrd(String rrdPath, Metric_Type valueType,
                                    long start, long end) throws IOException {
        //boundary condition
        checkPath(rrdPath);

        StringBuilder sb = new StringBuilder("rrdtool fetch " + rrdPath);
        switch (valueType) {
            case METRIC_TPYE_BYTE_IN:
                sb.append("/byte_in.rrd MAX " +
                        "--start " + start +
                        " --end " + end +
                        " DEF:ds0="+rrdPath+"/byte_in.rrd:ds0:MAX");
                break;
            default:
                break;
        }

        // fetch data from rrdfile
        ShellUtils.ShellCommandExecutor shCextor =
                new ShellUtils.ShellCommandExecutor(sb.toString());
        try {
            shCextor.execute();
        } catch (IOException e) {
            LOG.error("Failed to fetch data from rrd file " + rrdPath);
            System.out.println("Failed to fetch data from rrd file " + rrdPath);
            e.printStackTrace();
        }

        return shCextor.getOutput().split("\n");

    }

    public static String getBrokerByteInMetric(String response) throws IOException {
        //boundary condition
        if (null == response) {
            System.out.println("Please make sure hostNameOrIpAddress/clusterId/componentType is not null!");
            return null;
        }

        if (response != null){
            Map<String, Object> responseMap = objectMapper.readValue(
                    response.getBytes(), Map.class);
            Map<String, Object> dataMap = (Map<String, Object>) responseMap
                    .get("data");
            List<Object> vms = (List<Object>) dataMap.get("vms");
            if (vms != null && !vms.isEmpty()) {
                for (Object vm : vms) {
                    Map<String, Object> vmMap = ((Map<String, Object>) vm);
                    if (vmMap != null && vmMap.containsKey("objects")) {
                        List<Object> listMap = (List<Object>) vmMap.get("objects");
                        if (listMap != null && !listMap.isEmpty()) {
                            for(Object obj: listMap) {
                                Map<String, Object> objectMap = ((Map<String, Object>) obj);
                                if (objectMap != null && objectMap.containsKey("attributes")) {
                                    Map<String, Object> attribMap = (Map<String, Object>) objectMap.get("attributes");
                                    if (attribMap != null && attribMap.containsKey("Count")) {
                                        Map<String, Object> valueMap = (Map<String, Object>) attribMap.get("Count");
                                        if (valueMap != null && valueMap.containsKey("value")) {
                                            return "N:" + valueMap.get("value");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getMetricValue() throws IOException {
        //get demo metric from localhost
        String command = "sudo -u oracle /u01/oehpcs/oehpcs-apps/monitor-app/bin/oehpcs-monitor-app-cli.sh -a " +
                "rrdtool-poc -b kafka -c kafka.server:name=BytesInPerSec,type=BrokerTopicMetrics";
        ShellUtils.ShellCommandExecutor shCextor =
                new ShellUtils.ShellCommandExecutor(command);

        try {
            shCextor.execute();
        } catch (IOException e) {
            System.out.println("Failed to get update rrd file " + shCextor.getOutput());
            e.printStackTrace();
        }
        return getBrokerByteInMetric(shCextor.getOutput());
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        String update = getMetricValue();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1000000);
        System.out.println("update is: " + update);
        long currentTime = System.currentTimeMillis();
        createRrdFile("/tmp/localhost", Metric_Type.METRIC_TPYE_BYTE_IN);
        int i = 1000000;

        while(i>0){
            executor.submit(new RrdTask(RrdService.TASK_TYPE.UPDATE_TASK,
                    RrdUtils.Metric_Type.METRIC_TPYE_BYTE_IN,
                    System.currentTimeMillis(),
                    update,
                    "/tmp/localhost/rrd"));
            i--;
        }

        System.out.println("Time to do 1M updates: " + (System.currentTimeMillis() - currentTime) / 1000);
        //updateRrd("/tmp/localhost", Metric_Type.METRIC_TPYE_BYTE_IN, update);
        Thread.sleep(30);
        String[] result = fetchRrd("/tmp/localhost", Metric_Type.METRIC_TPYE_BYTE_IN,
                currentTime/1000-30,
                System.currentTimeMillis()/1000);
        for(String str: result) {
            System.out.println(str);
        }
    }

    private static void checkPath(String path) throws IOException {
        //boundary condition
        if (null == path) {
            System.out.println("RrdPath is null");
            throw new NullPointerException("RrdPath is null");
        }

        //check if path exists, if not try to create it
        File file = new File(path);
        if(!file.exists()) {
            LOG.info("Path " + path + " does not exists! Creating it...");
            System.out.println("Path " + path + " does not exists!");
            if (!file.mkdirs()) {
                LOG.error("Failed to create path" + path);
                throw new IOException("Failed to create path" + path);
            }
        }
    }

    static float scientificNumberToFloat(String sNumber) throws NotANumberException {
        if (null == sNumber) return (float) 0.0;

        float part1;
        int multiplier;

        String[] strs = sNumber.split("e+");
        if (strs.length != 2) {
            LOG.warn("Result is not a scientific number: " + sNumber);
            throw new NotANumberException();
        }
        part1 = Float.parseFloat(strs[0]);
        multiplier = Integer.parseInt(strs[1]);

        return part1*(float)java.lang.Math.pow(10,multiplier);
    }

}
