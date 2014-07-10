package panda.ioc.val;

import panda.ioc.IocMaking;
import panda.ioc.ValueProxy;
import panda.lang.Classes;
import panda.lang.Strings;
import panda.util.Pair;

public class ReferValue implements ValueProxy {

	private String name;
	private Class<?> type;

	public static Pair<Class<?>> parseName(String name) {
		String _name = null;
		Class<?> type = null;
		int pos = name.indexOf(':');
		if (pos < 0) {
			_name = Strings.trim(name);
		}
		else {
			_name = Strings.trim(name.substring(0, pos));
			String typeName = Strings.trim(name.substring(pos + 1));
			type = Classes.load(typeName, false);
		}
		return new Pair<Class<?>>(_name, type);
	}

	public ReferValue(String name) {
		Pair<Class<?>> p = parseName(name);
		this.name = p.getName();
		this.type = p.getValue();
	}

	public ReferValue(Class<?> type) {
		this.type = type;
	}

	public Object get(IocMaking ing) {
		return ing.getIoc().get(type, name, ing.getContext());
	}

}
