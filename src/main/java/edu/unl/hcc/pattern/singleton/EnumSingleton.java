package edu.unl.hcc.pattern.singleton;

/**
 * Created by chehe on 2017/8/30.
 * In java, since Enum type is single and global accessible
 * we can use enum type class to achieve the singleton
 *
 * Drawbacks: not flexible, does not allow lazy initilization
 */
public enum EnumSingleton {

	INSTANCE;

	public static void doSomething(){}
}
