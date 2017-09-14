package edu.unl.hcc.Reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by chehe on 2017/8/31.
 */
public class ReflectingConstructor {

	public void demoConstructor() throws ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, InstantiationException {


		Constructor<?> constructor = Class.forName("ed..unl.hcc.Reflection.ReflectingConstructor")
				.getConstructor(int.class);
//getting constructor parameters
		System.out.println(Arrays.toString(constructor.getParameterTypes())); // prints "[int]"

		Object myObj = constructor.newInstance(10);
		Method myObjMethod = myObj.getClass().getMethod("method1", null);
		myObjMethod.invoke(myObj, null); //prints "Method1 impl."

		Constructor<?> hashMapConstructor = Class.forName("java.util.HashMap").getConstructor(null);
		System.out.println(Arrays.toString(hashMapConstructor.getParameterTypes())); // prints "[]"
		HashMap<String, String> myMap = (HashMap<String, String>) hashMapConstructor.newInstance(null);
	}
}
