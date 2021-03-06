package panda.ioc.loader;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import panda.ioc.meta.IocEventSet;
import panda.ioc.meta.IocObject;
import panda.ioc.meta.IocParam;
import panda.ioc.meta.IocValue;
import panda.lang.Strings;

public abstract class Loaders {
	/**
	 * convert inject description to IocValue
	 * 
	 * <pre>
	 * ${...}: el expression
	 * %{...}: el expression
	 * !{...}: json object
	 * ![...]: json array
	 * '...  : normal: value.substring(1)
	 * #...  : reference ioc bean
	 * </pre>
	 * 
	 * @param value inject object description
	 * @param kind default ioc type
	 * @return the IocValue
	 */
	public static IocValue convert(char kind, Type type, String value) {
		if (value == null) {
			return new IocValue(IocValue.KIND_NULL, type, value);
		}
		if (value.isEmpty()) {
			return new IocValue(IocValue.KIND_RAW, type, value);
		}

		int c0 = value.charAt(0);
		if (c0 == '\'') {
			return new IocValue(IocValue.KIND_RAW, type, value.substring(1));
		}
		if (c0 == '#' && value.length() > 1) {
			return new IocValue(IocValue.KIND_REF, type, value.substring(1));
		}
		if (value.length() > 3) {
			int c1 = value.charAt(1);
			int cx = value.charAt(value.length() - 1);
			
			if ((c0 == '$' || c0 == '%') && c1 == '{' && cx == '}') {
				return new IocValue(IocValue.KIND_EL, type, value.substring(2, value.length() - 1));
			}
			if (c0 == '!' && c1 == '{' && cx == '}') {
				return new IocValue(IocValue.KIND_JSON, type, value.substring(1));
			}
			if (c0 == '!' && c1 == '[' && cx == ']') {
				return new IocValue(IocValue.KIND_JSON, type, value.substring(1));
			}
		}

		return new IocValue(kind, type, value);
	}

	/**
	 * merge IocObject from it to me.
	 * 
	 * @param me the IocObject to be merged
	 * @param it the IocObject to merge from
	 * @return the merged IocObject (me)
	 */
	public static IocObject mergeWith(IocObject me, IocObject it) {
		// merge type
		if (me.getType() == null) {
			me.setType(it.getType());
		}

		// don't need merge singleton

		// merge events
		if (me.getEvents() == null) {
			me.setEvents(it.getEvents());
		}
		else if (it.getEvents() != null) {
			IocEventSet eventSet = it.getEvents();
			IocEventSet myEventSet = me.getEvents();
			if (Strings.isEmpty(myEventSet.getCreate())) {
				myEventSet.setCreate(eventSet.getCreate());
			}
			
			if (Strings.isEmpty(myEventSet.getDepose())) {
				myEventSet.setDepose(eventSet.getDepose());
			}
			
			if (Strings.isEmpty(myEventSet.getFetch())) {
				myEventSet.setFetch(eventSet.getFetch());
			}
		}

		// merge scope
		if (Strings.isEmpty(me.getScope())) {
			me.setScope(it.getScope());
		}

		// merge arguments
		if (me.getArgs() == null) {
			me.setArgs(it.getArgs());
		}
		
		// merge fields
		if (it.getFields() != null) {
			for (Entry<String, IocParam> en : it.getFields().entrySet()) {
				if (!me.hasField(en.getKey())) {
					me.addField(en.getKey(), en.getValue());
				}
			}
		}

		return me;
	}
}
