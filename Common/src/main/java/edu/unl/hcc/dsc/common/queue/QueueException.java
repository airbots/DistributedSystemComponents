package edu.unl.hcc.dsc.common.queue;

import java.io.IOException;

/**
 * Created by chehe on 16/1/26.
 */
public class QueueException extends IOException {


  public QueueException () {}


  public QueueException (String s) { super(s);}


  public QueueException (Throwable throwable) {super (throwable);}


}
