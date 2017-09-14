package edu.unl.hcc.stringpool;

/**
 * Created by chehe on 2017/8/31.
 */
public class StringPoolExample {
		/**
		 * Java String Pool example
		 * @param args
		 */
		public static void main(String[] args) {

			/**
			*当使用双引号创建string的时候，java会先从stringpool里找有没有一样的类，因为string是immutable的
			 * 如果没有，则在stringpool内创建，如果有，指向同一个实体
			 *
			 * 而new Stirng("") 直接创建在heap里，并不在stringpool内部
			 */
			String s1 = "Cat";
			String s2 = "Cat";
			String s3 = new String("Cat");

			System.out.println("s1 == s2 :"+(s1==s2));
			System.out.println("s1 == s3 :"+(s1==s3));
      //equals 比较的是value不是类
			System.out.println("s1.equals(s3):" + (s3.equals(s1)));
		}

	}
