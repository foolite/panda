package panda.el;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Test;

import panda.bind.json.Jsons;
import panda.lang.Strings;

@SuppressWarnings("unchecked")
public class El2Test {
	@Test
	public void notCalculateOneNumber() {
		assertEquals(1, El.eval("1"));
		assertEquals(0.1, El.eval(".1"));
		assertEquals(0.1d, El.eval("0.1"));
		assertEquals(0.1f, El.eval("0.1f"));
		assertEquals(0.1d, El.eval("0.1d"));
		assertEquals(true, El.eval("true"));
		assertEquals(false, El.eval("false"));
		assertEquals("jk", El.eval("'jk'"));
	}

	@Test
	public void simpleCalculate() {
		// 加
		assertEquals(2, El.eval("1+1"));
		assertEquals(2.2, El.eval("1.1+1.1"));
		// 减
		assertEquals(1, El.eval("2-1"));
		// 乘
		assertEquals(9, El.eval("3*3"));
		assertEquals(0, El.eval("3*0"));
		// 除
		assertEquals(3, El.eval("9/3"));
		assertEquals(2.2, El.eval("4.4/2"));
		assertEquals(9.9 / 3, El.eval("9.9/3"));
		// 取余
		assertEquals(1, El.eval("5%2"));
		assertEquals(1.0 % 0.1, El.eval("1.0%0.1"));

	}

	/**
	 * 位运算
	 */
	@Test
	public void bit() {
		assertEquals(-40, El.eval("-5<<3"));
		assertEquals(-1, El.eval("-5>>3"));
		assertEquals(5, El.eval("5>>>32"));
		assertEquals(-5, El.eval("-5>>>32"));
		assertEquals(1, El.eval("5&3"));
		assertEquals(7, El.eval("5|3"));
		assertEquals(-6, El.eval("~5"));
		assertEquals(6, El.eval("5^3"));
	}

	/**
	 * 多级运算
	 */
	@Test
	public void multiStageOperation() {
		assertEquals(3, El.eval("1 + 1 + 1"));
		assertEquals(1, El.eval("1+1-1"));
		assertEquals(-1, El.eval("1-1-1"));
		assertEquals(1, El.eval("1-(1-1)"));
		assertEquals(7, El.eval("1+2*3"));
		assertEquals(2 * 4 + 2 * 3 + 4 * 5, El.eval("2*4+2*3+4*5"));
		assertEquals(9 + 8 * 7 + (6 + 5) * ((4 - 1 * 2 + 3)), El.eval("9+8*7+(6+5)*((4-1*2+3))"));
		assertEquals(.3 + .2 * .5, El.eval(".3+.2*.5"));
		assertEquals((.5 + 0.1) * .9, El.eval("(.5 + 0.1)*.9"));
	}

	/**
	 * 空格
	 */
	@Test
	public void sikpSpace() {
		// 空格检测
		assertEquals(3, El.eval("    1 + 2    "));
	}

	@Test
	public void testNull() {
		assertEquals(null, El.eval("null"));
		assertTrue((Boolean)El.eval("null == null"));
	}

	/**
	 * 逻辑运算
	 */
	@Test
	public void logical() {
		assertEquals(true, El.eval("2 > 1"));
		assertEquals(false, El.eval("2 < 1"));
		assertEquals(true, El.eval("2 >= 2"));
		assertEquals(true, El.eval("2 <= 2"));
		assertEquals(true, El.eval("2 == 2 "));
		assertEquals(true, El.eval("1 != 2"));
		assertEquals(true, El.eval("!(1 == 2)"));
		assertEquals(true, El.eval("!false"));
		assertEquals(true, El.eval("true || false"));
		assertEquals(false, El.eval("true && false"));
		assertEquals(false, El.eval("false || true && false"));
	}

	/**
	 * 三元运算 ?:
	 */
	@Test
	public void threeTernary() {
		assertEquals(2, El.eval("1>0?2:3"));
		assertEquals(2, El.eval("1>0&&1<2?2:3"));
	}

	/**
	 * 字符串测试
	 */
	@Test
	public void stringTest() {
		assertEquals("jk", El.eval("'jk'"));
		assertEquals(2, El.eval("'jk'.length()"));
		assertEquals(2, El.eval("\"jk\".length()"));
		assertEquals("jk", El.eval("\"    jk   \".trim()"));
		assertEquals("j\\n\\tk", El.eval("\"j\\n\\tk\""));
	}

	@Test
	public void test_issue_397_3() {
		int expect = 1 / 1 + 10 * (1400 - 1400) / 400;
		Object val = El.eval("1/1+10*(1400-1400)/400");
		assertEquals(expect, val);
	}

	/**
	 * 带负数的运算
	 */
	@Test
	public void negative() {
		assertEquals(-1, El.eval("-1"));
		assertEquals(0, El.eval("-1+1"));
		assertEquals(-1 - -1, El.eval("-1 - -1"));
		assertEquals(9 + 8 * 7 + (6 + 5) * (-(4 - 1 * 2 + 3)), El.eval("9+8*7+(6+5)*(-(4-1*2+3))"));
	}

	/**
	 * 方法调用
	 */
	@Test
	public void callMethod() {
		assertEquals('j', El.eval("'jk'.charAt(0)"));
		assertEquals("cde", El.eval("\"abcde\".substring(2)"));
		assertEquals("b", El.eval("\"abcde\".substring(1,2)"));
		assertEquals(true, El.eval("\"abcd\".regionMatches(2,\"ccd\",1,2)"));
		assertEquals("bbbb", El.eval("'  abab  '.replace('a','b').trim()"));
	}

	/**
	 * 参数
	 */
	@Test
	public void test_simple_condition() {
		Map context = new HashMap();
		context.put("a", 10);
		assertEquals(10, El.eval(context, "a"));
		assertEquals(20, El.eval(context, "a + a"));

		context.put("b", "abc");
		assertEquals(25, El.eval(context, "a + 2 +a+ b.length()"));

		String s = "a>5?'GT 5':'LTE 5'";
		assertEquals("GT 5", El.eval(context, s));
		context.put("a", 5);
		assertEquals("LTE 5", El.eval(context, s));

		assertEquals("jk", El.eval("\"j\"+\"k\""));

	}

	@Test
	public void context() {
		Map context = new HashMap();
		List<String> list = new ArrayList<String>();
		list.add("jk");
		context.put("a", list);
		assertEquals("jk", El.eval(context, "a.get((1-1))"));
		assertEquals("jk", El.eval(context, "a.get(1-1)"));
		assertEquals("jk", El.eval(context, "a.get(0)"));

		assertTrue((Boolean)El.eval(new HashMap(), "a==null"));
		try {
			assertTrue((Boolean)El.eval(new HashMap(), "a.a"));
			fail();
		}
		catch (Exception e) {
		}
	}

	/**
	 * 数组测试
	 */
	@Test
	public void array() {
		Map context = new HashMap();
		String[] str = new String[] { "a", "b", "c" };
		String[][] bb = new String[][] { { "a", "b" }, { "c", "d" } };
		context.put("a", str);
		context.put("b", bb);
		assertEquals("b", El.eval(context, "a[1]"));
		assertEquals("b", El.eval(context, "a[1].toString()"));
		assertEquals("b", El.eval(context, "a[2-1]"));
		assertEquals("d", El.eval(context, "b[1][1]"));
	}

	/**
	 * 属性测试
	 */
	@Test
	public void field() {
		class abc {
			@SuppressWarnings("unused")
			public String name = "jk";
		}
		Map context = new HashMap();
		context.put("a", new abc());
		assertEquals("jk", El.eval(context, "a.name"));
		// 这个功能放弃
		// assertFalse((Boolean)El.eval("java.lang.Boolean.FALSE"));
		// assertFalse((Boolean)El.eval("Boolean.FALSE"));
	}

//	/**
//	 * 自定义函数
//	 */
//	@Test
//	public void custom() {
//		assertEquals(2, El.eval("max(1, 2)"));
//		assertEquals(1, El.eval("min(1, 2)"));
//		assertEquals("jk", El.eval("trim('    jk    ')"));
//	}

	@Test
	public void speed() {
		SimpleSpeedTest z = new SimpleSpeedTest();
		int num = 4988;
		String elstr = "num + (i - 1 + 2 - 3 + 4 - 5 + 6 - 7)-z.abc(i)";
		int i = 5000;
		Map con = new HashMap();
		con.put("num", num);
		con.put("i", i);
		con.put("z", z);
		assertEquals(num + (i - 1 + 2 - 3 + 4 - 5 + 6 - 7) - z.abc(i), El.eval(con, elstr));
	}

	@Test
	public void lssue_486() {
		assertEquals(2 + (-3), El.eval("2+(-3)"));
		assertEquals(2 + -3, El.eval("2+-3"));
		assertEquals(2 * -3, El.eval("2*-3"));
		assertEquals(-2 * -3, El.eval("-2*-3"));
		assertEquals(2 / -3, El.eval("2/-3"));
		assertEquals(2 % -3, El.eval("2%-3"));
	}

	/**
	 * map测试
	 */
	@Test
	public void map() {
		Map context = new HashMap();
		context.put("a", Jsons.fromJson("{'x':10,'y':50,'txt':'Hello'}", Map.class));

		assertEquals(100, El.eval(context, "a.get('x')*10"));
		assertEquals(100, El.eval(context, "a.x*10"));
		assertEquals(100, El.eval(context, "a['x']*10"));
		assertEquals("Hello-40", El.eval(context, "a.get('txt')+(a.get('x')-a.get('y'))"));
	}

	/**
	 * list测试
	 */
	@Test
	public void list() {
		Map context = new HashMap();
		List<String> list = new ArrayList<String>();
		context.put("b", list);
		assertEquals(0, El.eval(context, "b.size()"));
		list.add("");
		assertEquals(1, El.eval(context, "b.size()"));
		El.eval(context, "b.add('Q\nQ')");
		assertEquals(2, El.eval(context, "b.size()"));
	}

	@SuppressWarnings("unused")
	@Test
	public void complexOperation() {
		assertEquals(1000 + 100.0 * 99 - (600 - 3 * 15) % (((68 - 9) - 3) * 2 - 100) + 10000 % 7 * 71,
			El.eval("1000+100.0*99-(600-3*15)%(((68-9)-3)*2-100)+10000%7*71"));
		assertEquals(6.7 - 100 > 39.6 ? true ? 4 + 5 : 6 - 1 : !(100 % 3 - 39.0 < 27) ? 8 * 2 - 199 : 100 % 3,
			El.eval("6.7-100>39.6 ? 5==5? 4+5:6-1 : !(100%3-39.0<27) ? 8*2-199: 100%3"));

		Map vars = new HashMap();
		vars.put("i", 100);
		vars.put("pi", 3.14f);
		vars.put("d", -3.9);
		vars.put("b", (byte)4);
		vars.put("bool", false);
		vars.put("t", "");
		String t = "i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99 ==i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99";
		// t =
		// "i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99";
		assertEquals(true, El.eval(vars, t));

		// assertEquals('A' == ('A') || 'B' == 'B' && "ABCD" == "" && 'A' ==
		// 'A', el.eval(vars,
		// "'A' == 'A' || 'B' == 'B' && 'ABCD' == t &&  'A' == 'A'"));
		assertEquals(true || true && false && true,
			El.eval(vars, "'A' == 'A' || 'B' == 'B' && 'ABCD' == t &&  'A' == 'A'"));
	}

	@Test
	public void testIntValue() {
		Map context = new HashMap();
		context.put("a", new BigDecimal("7"));
		context.put("b", new BigDecimal("3"));
		assertEquals(10, El.eval(context, "a.add(b).intValue()"));
	}

	@Test
	public void testFloat() {
		assertEquals(El.eval("0.1354*((70-8)%70)*100"), 0.1354 * ((70 - 8) % 70) * 100);
		assertEquals(El.eval("0.1354*((70d-8)/70)*100"), 0.1354 * ((70d - 8) / 70) * 100);
		assertEquals(El.eval("0.5006*(70/600*100)"), 0.5006 * (70 / 600 * 100));
	}

	@Test
	public void testStaticMethod() {
		Map context = new HashMap();
		context.put("strings", Strings.class);
		context.put("math", Math.class);
		assertEquals("a", El.eval(context, "strings@trim(\"  a  \")"));
		assertEquals(2, El.eval(context, "math@max(1, 2)"));
	}

	// @Test
	// public void testIssue279() throws InterruptedException {
	// Map context = new HashMap();
	// context.put("math", Math.class);
	// assertEquals("java.lang.Math", El.eval(context, "math.toString()"));
	//
	// NutConf.load("org/nutz/el/issue279/279.js");
	// assertEquals(El.eval("uuuid(false)"), "abc");
	// assertEquals(El.eval("uuuid()"), "abc");
	// }

	@Test
	public void testIssueQuestionOpe() {
		Map context = new HashMap();
		context.put("a", 123);
		context.put("b", 20);
		Object o = El.eval(context, "a>b?a:b");
		assertEquals(123, o);
	}

	public static class Static {
		public static final String info = "xxx";

		public static String printParam(String x) {
			return x;
		}
	}
	
	@Test
	public void testStaticMember() {
		Map context = new HashMap();
		context.put("static", new Static());
		context.put("a", Static.class);

		assertEquals("xxx", El.eval(context, "a@printParam(a@info)"));
	}

	public static class SelfRef {
		public String name;
		public SelfRef child;
		public List<String> list = new ArrayList<String>();

		public SelfRef(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	@Test
	public void testSelfRef() {
		Map context = new HashMap();
		SelfRef item = new SelfRef("item");
		item.child = new SelfRef("child");
		context.put("item", item);

		assertEquals("child", El.eval(context, "item.child.getName()"));
		assertEquals(0, El.eval(context, "item.list.size()"));
	}

	@Test
	public void testThreadSafe1() throws InterruptedException {
		int size = 100;
		final CountDownLatch count = new CountDownLatch(size);
		final List<Integer> error = new ArrayList<Integer>();
		for (int index = 0; index < size; index++) {
			new Thread() {
				public void run() {
					try {
						El.eval("1+1");
					}
					catch (Exception e) {
						error.add(1);
					}
					finally {
						count.countDown();
					}
				}

			}.start();
		}
		count.await();
		if (error.size() > 0) {
			fail();
		}
	}

	@Test
	public void testThreadSafe2() throws InterruptedException {
		final El el = El.parse("a+1");

		int size = 100;
		final CountDownLatch count = new CountDownLatch(size);
		final List<Integer> error = new ArrayList<Integer>();
		for (int index = 0; index < size; index++) {
			new Thread() {
				public void run() {
					try {
						Map m = new HashMap();
						m.put("a", 0);
						for (int i = 0; i < 10000; i++) {
							m.put("a", el.eval(m));
						}
						assertEquals(10000, m.get("a"));
					}
					catch (Exception e) {
						error.add(1);
					}
					finally {
						count.countDown();
					}
				}

			}.start();
		}
		count.await();
		if (error.size() > 0) {
			fail();
		}
	}


	@Test
	public void testList() {
		Map context = new HashMap();
		List<String> list = new ArrayList<String>();
		list.add("jk");
		context.put("list", list);
		context.put("System", System.class);

		El.eval(context, "list.add(list.get(0))");
		assertEquals(2, list.size());
	}

	@Test
	public void testSystem() {
		Map context = new HashMap();
		List<String> list = new ArrayList<String>();
		list.add("jk");
		context.put("list", list);
		context.put("System", System.class);

		Object val = El.eval(context, "System@getenv('PATH').getClass().getName()");
		Assert.assertNotNull(val);
	}

	public static class ListTest {
		private List<String> list;

		public List<String> getList() {
			return list;
		}

		public void setList(List<String> list) {
			this.list = list;
		}
	}

	@Test
	public void testListTest() {
		Map context = new HashMap();

		context.put("String", String.class);

		ListTest lt = new ListTest();
		List<String> list = new ArrayList<String>();
		list.add("123");
		lt.setList(list);
		context.put("map", lt);

		assertEquals("123", El.eval(context, "String@valueOf(123)"));
		assertEquals("123", El.eval(context, "map.list.get(0)"));
	}

	public static class InnerClass {
		public static class A {
			B b = new B();

			public B getB() {
				return b;
			}

			public void setB(B b) {
				this.b = b;
			}
		}

		public static class B {
			public boolean isPass(String a) {
				return true;
			}
		}
	}

	@Test
	public void testInnerClass() {
		El el = new El("a[0].b.isPass('')?'1':'2'");
		Map ctx = new HashMap();
		ctx.put("a", new Object[] { new InnerClass.A() });
		assertEquals("1", el.eval(ctx));
	}
}
