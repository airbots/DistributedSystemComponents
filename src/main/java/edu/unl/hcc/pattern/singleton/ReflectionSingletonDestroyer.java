package edu.unl.hcc.pattern.singleton;

import java.lang.reflect.Constructor;
/**
 * Created by chehe on 2017/8/30.
 *
 * 这个类可以轻松的打破singleton的定义，也就是说，创建两个同一类singleton实例
 */
public class ReflectionSingletonDestroyer {

		public static void main(String[] args) {
			EagerSingleton instanceOne = EagerSingleton.getInstance();
			EagerSingleton instanceTwo = null;
			try {
				Constructor[] constructors = EagerSingleton.class.getDeclaredConstructors();
				for (Constructor constructor : constructors) {
					//Below code will destroy the singleton pattern
					constructor.setAccessible(true);
					instanceTwo = (EagerSingleton) constructor.newInstance();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(instanceOne.hashCode());
			System.out.println(instanceTwo.hashCode());
		}
}
