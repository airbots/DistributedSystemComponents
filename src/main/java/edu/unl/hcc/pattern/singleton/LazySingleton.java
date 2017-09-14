package edu.unl.hcc.pattern.singleton;

/**
 * Created by chehe on 2017/8/30.
 * This LazySingleton only initialize and occupy memory for new instance when
 * getInstance() method is called.
 */

public class LazySingleton {

	private static LazySingleton instance;

	private LazySingleton(){}

	public static LazySingleton getInstance(){
		if (instance == null) instance = new LazySingleton();
		return instance;
	}
}
