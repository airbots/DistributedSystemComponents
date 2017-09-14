package edu.unl.hcc.pattern.singleton;

/**
 * Created by chehe on 2017/8/30.
 */
public class EagerSingleton {

	private static final EagerSingleton instance = new EagerSingleton();

	private EagerSingleton(){}

	public static EagerSingleton getInstance(){
		return instance;
	}

}
