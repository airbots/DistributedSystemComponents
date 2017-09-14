package edu.unl.hcc.lock;

/**
 * Created by chehe on 2017/8/30.
 *
 *
 * Reentrant means that locks are bound to the current thread.
 * A thread can safely acquire the same lock multiple times without running into deadlocks
 * (e.g. a synchronized method calls another synchronized method on the same object).
 *
 */
public class ReentrantLock {
}
