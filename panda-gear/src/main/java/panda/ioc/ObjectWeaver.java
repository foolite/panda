package panda.ioc;

/**
 * 对象编织器
 */
public interface ObjectWeaver {

	/**
	 * 根据容器构造时，为一个对象填充字段
	 * 
	 * @param im 容器构造时
	 * @param obj 对象，要被填充字段
	 * @return 被填充后的字段
	 */
	<T> T fill(IocMaking im, T obj);

	/**
	 * 根据自身内容创建一个对象，并触发创建事件
	 * 
	 * @param im 容器构造时
	 * @return the object
	 */
	Object born(IocMaking im);

	/**
	 * 为对象触发 CREATE 事件
	 * 
	 * @param obj 对象
	 * @return the return value
	 */
	Object onCreate(Object obj);

}
