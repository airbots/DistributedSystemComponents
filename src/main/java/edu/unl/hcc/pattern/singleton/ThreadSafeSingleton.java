package edu.unl.hcc.pattern.singleton;

/**
 * Created by chehe on 2017/8/30.
 * Synchronized keyword will serialize the parallel access
 * However, it is not efficient since Java Synchronized is using
 */

/**
 * Java synchronization works on locking and unlocking of resource, before any thread enters into synchronized
   code, it has to acquire lock on the Object and when code execution ends, it unlocks the resource that can be
   locked by other threads. In the mean time other threads are in wait state to lock the synchronized resource.
 * We can use synchronized keyword in two ways, one is to make a complete method synchronized and other way is
   to create synchronized block.
 * When a method is synchronized, it locks the Object, if method is static it locks the Class, so it's always
   best practice to use synchronized block to lock the only sections of method that needs synchronization.
 * While creating synchronized block, we need to provide the resource on which lock will be acquired, it can
   be XYZ.class or any Object field of the class.
 * synchronized(this) will lock the Object before entering into the synchronized block.
 * You should use the lowest level of locking, for example if there are multiple synchronized block
   in a class and one of them is locking the Object, then other synchronized blocks will also be not
   available for execution by other threads. When we lock an Object, it acquires lock on all the fields of the Object.
 * Java Synchronization provides data integrity on the cost of performance, so it should be used only
   when it's absolutely necessary.
 * Java Synchronization works only in the same JVM, so if you need to lock some resource in multiple JVM
   environment, it will not work and you might have to look after some global locking mechanism.
 * Java Synchronization could result in deadlocks, check this post about deadlock in java and how to avoid them.
 * Java synchronized keyword cannot be used for constructors and variables.
 * It is preferable to create a dummy private Object to use for synchronized block, so that it's reference
   can't be changed by any other code. For example if you have a setter method for Object on which you are
   synchronizing, it's reference can be changed by some other code leads to parallel execution of the synchronized block.
 * We should not use any object that is maintained in a constant pool, for example String should not be used for
   synchronization because if any other code is also locking on same String, it will try to acquire lock on the same
   reference object from String pool and even though both the codes are unrelated, they will lock each other.
 */

public class ThreadSafeSingleton {
	private static ThreadSafeSingleton instance;

	private ThreadSafeSingleton(){}

	public static synchronized ThreadSafeSingleton getInstance(){
		if (instance == null) instance = new ThreadSafeSingleton();
		return instance;
	}
}
