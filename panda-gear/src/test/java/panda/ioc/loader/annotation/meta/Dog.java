package panda.ioc.loader.annotation.meta;

import panda.ioc.annotation.IocBean;

@IocBean
public class Dog {

	public Dog() {
		// 总是抛出异常,这个bean就总无法被创建
		throw new RuntimeException();
	}
}
