package panda.ioc.json;

import java.util.Map;

import org.junit.Test;

import panda.ioc.Ioc;
import panda.ioc.ObjectProxy;
import panda.ioc.impl.ScopeIocContext;
import panda.ioc.json.pojo.Animal;

import static org.junit.Assert.*;
import static panda.ioc.json.Utils.*;

public class ScopeJsonIocTest {

	@Test
	public void test_simple_scope() {
		Ioc ioc = I(J("f1", "scope:'app',fields:{name:'F1'}"), J("f2", "scope:'MyScope',fields:{name:'F2'}"));

		Animal f1 = ioc.get(Animal.class, "f1");
		assertEquals("F1", f1.getName());

		Animal f2 = ioc.get(Animal.class, "f2");
		assertEquals("F2", f2.getName());
		Animal f22 = ioc.get(Animal.class, "f2");
		assertEquals("F2", f22.getName());
		assertFalse(f2 == f22);

		ScopeIocContext ic = new ScopeIocContext("MyScope");
		Map<String, ObjectProxy> map = ic.getObjs();
		f2 = ioc.get(Animal.class, "f2", ic);
		assertEquals("F2", f2.getName());
		f22 = ioc.get(Animal.class, "f2", ic);
		assertEquals("F2", f22.getName());
		assertTrue(f2 == f22);
		assertEquals(1, map.size());

		ioc.get(Animal.class, "f1", ic);

		assertEquals(1, map.size());

	}

	@Test
	public void test_refer_from_diffenent_scope() {
		Ioc ioc = I(J("f1", "type : '" + Animal.class.getName() + "' , scope:'app',fields:{name:'F1'}"),
			J("f2", "type : '" + Animal.class.getName() + "' , scope:'MyScope',fields:{name:{refer : 'f3'}}"),
			J("f3", "type : '" + Animal.class.getName() + "' , scope:'MyScope'}"));
		ioc.get(null, "f2");
	}

}
