package edu.unl.hcc.pattern.singleton;

/**
 * Created by chehe on 2017/8/30.
 * Difference between StaticBlockSingleton and EagerSingleton is
 * this class can handle runtime exceptions
 */


public class StaticBlockSingleton {

	private static final StaticBlockSingleton instance;

	private StaticBlockSingleton(){}

	static {
		try{
		 	instance = new StaticBlockSingleton();
		} catch (Exception e){
			throw new RuntimeException("Exception occurs during creating singleton");
		}

	}

	public StaticBlockSingleton getInstance(){
		return instance;
	}
}
