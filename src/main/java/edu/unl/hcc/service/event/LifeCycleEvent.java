package edu.unl.hcc.service.event;

/**
 * Created by chehe on 2017/6/4.
 */

import edu.unl.hcc.service.common.Service;

import java.io.Serializable;

/**
 * A serializable lifecycle event: the time a state
 * transition occurred, and what state was entered.
 */

public class LifecycleEvent implements Serializable {

    private static final long serialVersionUID = 1648576996238247836L;

    /**
     * Local time in milliseconds when the event occurred
     */
    public long time;
    /**
     * new state
     */
    public Service.STATE state;
}
