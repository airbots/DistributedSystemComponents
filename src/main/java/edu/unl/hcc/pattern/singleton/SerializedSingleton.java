package edu.unl.hcc.pattern.singleton;

import java.io.Serializable;
/**
 * Created by chehe on 2017/8/30.
 * 在分布式系统中，有时我们需要实现可序列化的接口singleton，以便于RPC或是存入
 * 系统
 */
public class SerializedSingleton implements Serializable{

	private static SerializedSingleton instance;

	private SerializedSingleton(){}

	public static SerializedSingleton getInstance(){
		if(instance == null) {
			synchronized(SerializedSingleton.class){
				if(instance == null) instance = new SerializedSingleton();
			}
		}
		return instance;
	}
}
