package edu.unl.hcc.pattern.singleton;

/**
 * Created by chehe on 2017/8/30.
 */
public class ImprovedThreadSafeSingleton {

	private static ImprovedThreadSafeSingleton instance;

	private ImprovedThreadSafeSingleton(){}

	public static ImprovedThreadSafeSingleton getInstance(){

		if(instance == null) {
			//这里要声明类
			synchronized (ImprovedThreadSafeSingleton.class) {
				//再次检查的目的是为了让进入synchronized的线程不要在重复创建新的实例
				if (instance == null) instance = new ImprovedThreadSafeSingleton();
			}
		}
		return instance;
	}
}
