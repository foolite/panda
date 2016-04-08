package panda.ioc;

import panda.lang.Exceptions;

/**
 * 每次获取对象时会触发 fetch 事件，销毁时触发 depose 事件。
 * <p>
 * 这个对象需要小心被创建和使用。为了防止循环注入的问题，通常，ObjectMaker 需要快速<br>
 * 创建一个 ObjectProxy， 存入上下文。 然后慢慢的设置它的 weaver 和 fetch。
 * <p>
 * 在出现异常的时候，一定要将该对象从上下文中移除掉。
 */
public class ObjectProxy {

	/**
	 * 存储动态编织对象的方法
	 */
	private ObjectWeaver weaver;

	/**
	 * 存储静态对象
	 */
	private Object object;

	/**
	 * 获取时触发器
	 */
	private IocEventTrigger<Object> fetch;

	/**
	 * 销毁时触发器。如果有静态对象被销毁，触发
	 */
	private IocEventTrigger<Object> depose;

	public ObjectProxy() {
	}

	public ObjectProxy(Object obj) {
		this.object = obj;
	}

	public ObjectProxy setWeaver(ObjectWeaver weaver) {
		this.weaver = weaver;
		return this;
	}

	public ObjectProxy setObject(Object object) {
		this.object = object;
		return this;
	}

	public ObjectProxy setFetch(IocEventTrigger<Object> fetch) {
		this.fetch = fetch;
		return this;
	}

	public ObjectProxy setDepose(IocEventTrigger<Object> depose) {
		this.depose = depose;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> classOfT, IocMaking ing) {
		Object re;
		if (object != null) {
			re = object;
		}
		else if (weaver != null) {
			re = weaver.onCreate(weaver.fill(ing, weaver.born(ing)));
		}
		else {
			throw Exceptions.makeThrow("NullProxy for '%s'!", ing.getName());
		}
		
		if (null != fetch) {
			fetch.trigger(re);
		}
		
		return (T)re;
	}

	public void depose() {
		if (null != object && null != depose) {
			depose.trigger(object);
		}
	}

}
