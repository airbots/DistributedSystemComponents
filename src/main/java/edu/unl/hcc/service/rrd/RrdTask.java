package edu.unl.hcc.service.rrd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.Callable;

/**
 * Created by chehe on 2017/6/6.
 */
public class RrdTask implements Callable {

    private static final Log LOG = LogFactory.getLog(RrdTask.class);


    String taskId;
    RrdService.TASK_TYPE taskType;
    String rrdPath;
    String hostname;
    long start;
    long end;
    String value;
    RrdUtils.Metric_Type metric;

    int result;

    public void setTaskId(long serviceStartTime, int counter){
        if(taskId == null){
            taskId = new String("task_"+serviceStartTime + "_" + counter);
        }
    }

    public String getTaskId(){
        return taskId;
    }

    public RrdTask(RrdService.TASK_TYPE task_type){
        this.taskType = task_type;
    }

    public RrdTask(RrdService.TASK_TYPE task_type,
                   RrdUtils.Metric_Type metric,
                   String rrdPath){
        this.taskType = task_type;
        this.metric = metric;
        this.rrdPath = rrdPath;
    }

    public RrdTask(RrdService.TASK_TYPE task_type,
                   RrdUtils.Metric_Type metric,
                   long start,
                   String value,
                   String rrdPath){
        this.taskType = task_type;
        this.metric = metric;
        this.rrdPath = rrdPath;
        this.start = start;
        this.value = value;
    }

    public RrdTask(RrdService.TASK_TYPE task_type,
                   RrdUtils.Metric_Type metric,
                   long start,
                   long end,
                   String value,
                   String rrdPath
                   ){
        this.taskType = task_type;
        this.metric = metric;
        this.rrdPath = rrdPath;
        this.start = start;
        this.end = end;
        this.value = value;
    }

    public Object call() throws Exception {

        switch(taskType){
            case CREATE_TASK:
                RrdUtils.createRrdFile(rrdPath, metric);
                result = 1;
                break;
            case UPDATE_TASK:
                RrdUtils.updateRrd(rrdPath, metric,value);
                result = 1;
                break;
            case FETCH_TASK:
                RrdUtils.fetchRrd(rrdPath, metric, start, end);
                result = 1;
                break;
            default: result = 2;
                break;
        }
        return result;
    }
}
