package edu.unl.hcc.pattern.singleton;

/**
 * Created by chehe on 2017/8/30.
 *
 * 在java 1。5之前，有很多情况导致多线程同事创建出多个singleton
 * 一下方法通过额外的InnerSingle 类，因为内部类不会立即调入内存，
 * 因为只有getInstance被调用时，InnerSingleton才会被调入内存，通过增加额外的调入时间，
 * 来减少同时创建的可能性
 */
public class NestedClassSingleton {

	private NestedClassSingleton(){}
	private static class InnerSingleton{
		private static final NestedClassSingleton INSTANCE= new NestedClassSingleton();
	}

	public static NestedClassSingleton getInstance(){
		return InnerSingleton.INSTANCE;
	}
}
