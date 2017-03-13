package panda.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import panda.dao.entity.Klass;
import panda.dao.entity.Score;
import panda.dao.entity.Student;
import panda.dao.entity.Teacher;
import panda.dao.query.GenericQuery;
import panda.lang.Exceptions;
import panda.log.Log;
import panda.log.Logs;


/**
 */
public abstract class DaoTestCase {
	private static Log log = Logs.getLog(DaoTestCase.class);

	protected Dao dao;
	
	protected abstract DaoClient getDaoClient();
	
	@Before
	public void setUp() {
		if (getDaoClient() == null) {
			log.warn("SKIP: " + this.getClass().getName());
			Assume.assumeTrue(false);
		}
		dao = getDaoClient().getDao();
		try {
			init();
		}
		catch (Exception e) {
			log.error("init error", e);
			throw Exceptions.wrapThrow(e);
		}
	}

	protected void drop(Class clazz) {
		dao.drop(clazz);
	}

	protected void init() {
		drop(Score.class);
		drop(Klass.class);
		drop(Student.class);
		drop(Teacher.class);

		dao.create(Teacher.class);
		dao.create(Student.class);
		dao.create(Klass.class);
		dao.create(Score.class);
		
		dao.inserts(Teacher.creates(1, 5));
		dao.inserts(Student.creates(1, 5));
		dao.inserts(Klass.creates(1, 5));
		dao.inserts(Score.creates(1, 5));
	}

	@Test
	public void testCount() {
		Assert.assertEquals(5,  dao.count(Teacher.class));
	}

	@Test
	public void testCountQuery() {
		GenericQuery<Teacher> q = new GenericQuery<Teacher>(Teacher.class);
		q.equalTo("name", "T1");
		Assert.assertEquals(1,  dao.count(q));
	}

	@Test
	public void testExists() {
		Assert.assertTrue(dao.exists(Teacher.class));
		Assert.assertTrue(dao.exists(Student.class));
		Assert.assertTrue(dao.exists(Klass.class));
		Assert.assertTrue(dao.exists(Score.class));

		Assert.assertTrue(dao.exists(Teacher.class, Teacher.create(1)));
		
	}
	
	@Test
	public void testDrop() {
		dao.drop(Score.class);
		dao.drop(Klass.class);
		dao.drop(Teacher.class);
		dao.drop(Student.class);
	}
	
	@Test
	public void testInsertAutoId() {
		Student expect = Student.create(6);
		expect.setId(0);
		Assert.assertNotNull(dao.insert(expect));
		
		Assert.assertEquals(6, expect.getId());
		
		Student actual = dao.fetch(Student.class, expect);
		Assert.assertEquals(expect, actual);
	}
	
	@Test
	public void testSaveAutoId() {
		Student expect = Student.create(6);
		expect.setId(0);
		Assert.assertNotNull(dao.save(expect));
		
		Assert.assertEquals(6, expect.getId());
		
		Student actual = dao.fetch(Student.class, expect);
		Assert.assertEquals(expect, actual);
	}
	
	@Test
	public void testSaveUpdatePk() {
		Teacher expect = Teacher.create(2);
		expect.setMemo("save");

		Assert.assertNotNull(dao.save(expect));
		
		Teacher actual = dao.fetch(Teacher.class, expect);
		Assert.assertEquals(expect, actual);
	}
	
	@Test
	public void testSaveInsertPk() {
		Teacher expect = Teacher.create(8);
		expect.setMemo("save");

		Assert.assertNotNull(dao.save(expect));
		
		Teacher actual = dao.fetch(Teacher.class, expect);
		Assert.assertEquals(expect, actual);
	}
	
	@Test
	public void testSaveUpdateId() {
		Student expect = Student.create(2);
		expect.setName("save");

		Assert.assertNotNull(dao.save(expect));
		
		Student actual = dao.fetch(Student.class, expect);
		Assert.assertEquals(expect, actual);
	}
	
	@Test
	public void testSaveInsertId() {
		Student expect = Student.create(6);
		Assert.assertNotNull(dao.save(expect));
		
		Assert.assertEquals(6, expect.getId());
		
		Student actual = dao.fetch(Student.class, expect);
		Assert.assertEquals(expect, actual);
	}
	
	@Test
	public void testFetch() {
		Assert.assertEquals(Teacher.create(1), dao.fetch(Teacher.class, "T1"));
		Assert.assertEquals(Teacher.create(2), dao.fetch(Teacher.class, Teacher.create(2)));
	}
	
	@Test
	public void testSelectCamel() {
		Klass expect = Klass.create(1);
		
		Klass actual = dao.fetch(Klass.class, expect);
		
		Assert.assertEquals(expect, actual);
	}
	
	@Test
	public void testSelectInclude() {
		List<Teacher> expect = Teacher.creates(1, 3);
		expect.remove(1);
		for (Teacher t : expect) {
			t.setData(null);
		}
		
		GenericQuery<Teacher> q = new GenericQuery<Teacher>(Teacher.class);

		q.in("name", "T1", "T3").include("name").include("memo");
		List<Teacher> actual = dao.select(q);
		Assert.assertEquals(expect, actual);
	}
	
		
	@Test
	public void testSelectExclude() {
		List<Teacher> expect = Teacher.creates(1, 3);
		expect.remove(1);
		for (Teacher t : expect) {
			t.setData(null);
		}
		
		GenericQuery<Teacher> q = new GenericQuery<Teacher>(Teacher.class);

		q.in("name", "T1", "T3").exclude("data");
		List<Teacher> actual = dao.select(q);
		Assert.assertEquals(expect, actual);
	}
	
	@Test
	public void testSelectOr() {
		List<Teacher> expect = Teacher.creates(1, 3);
		expect.remove(1);
		
		GenericQuery<Teacher> q = new GenericQuery<Teacher>(Teacher.class);
		q.or().equalTo("name", "T1").equalTo("name", "T3").end();
		List<Teacher> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectIn() {
		List<Teacher> expect = Teacher.creates(1, 3);
		expect.remove(1);
		
		GenericQuery<Teacher> q = new GenericQuery<Teacher>(Teacher.class);
		q.in("name", "T1", "T3");
		List<Teacher> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}
	
	@Test
	public void testSelectNotIn() {
		List<Teacher> expect = Teacher.creates(4, 5);
		
		GenericQuery<Teacher> q = new GenericQuery<Teacher>(Teacher.class);
		q.notIn("name", new String[] { "T1", "T2", "T3" });
		List<Teacher> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectBetween() {
		List<Student> expect = Student.creates(1, 3);
		
		GenericQuery<Student> q = new GenericQuery<Student>(Student.class);
		q.between("id", 1, 3);
		List<Student> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectNotBetween() {
		List<Student> expect = Student.creates(4, 5);
		
		GenericQuery<Student> q = new GenericQuery<Student>(Student.class);
		q.notBetween("id", 1, 3);
		List<Student> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectLike() {
		List<Student> expect = Student.creates(1, 1);
		
		GenericQuery<Student> q = new GenericQuery<Student>(Student.class);
		q.like("name", "%1");
		List<Student> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectNotLike() {
		List<Student> expect = Student.creates(2, 5);
		
		GenericQuery<Student> q = new GenericQuery<Student>(Student.class);
		q.notLike("name", "%1");
		List<Student> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectMatch() {
		List<Student> expect = Student.creates(1, 1);
		
		GenericQuery<Student> q = new GenericQuery<Student>(Student.class);
		q.match("name", "1");
		
		List<Student> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectNotMatch() {
		List<Student> expect = Student.creates(2, 5);
		
		GenericQuery<Student> q = new GenericQuery<Student>(Student.class);
		q.notMatch("name", "1");
		List<Student> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectLeftMatch() {
		List<Student> expect = Student.creates(1, 5);
		
		GenericQuery<Student> q = new GenericQuery<Student>(Student.class);
		q.match("name", "S");
		List<Student> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectNotLeftMatch() {
		List<Student> expect = new ArrayList<Student>();
		
		GenericQuery<Student> q = new GenericQuery<Student>(Student.class);
		q.notMatch("name", "S");
		List<Student> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectRightMatch() {
		List<Student> expect = Student.creates(1, 1);
		
		GenericQuery<Student> q = new GenericQuery<Student>(Student.class);
		q.match("name", "1");
		List<Student> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectNotRightMatch() {
		List<Student> expect = Student.creates(2, 5);
		
		GenericQuery<Student> q = new GenericQuery<Student>(Student.class);
		q.notMatch("name", "1");
		List<Student> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectStart() {
		List<Teacher> expect = Teacher.creates(4, 5);
		
		GenericQuery<Teacher> q = new GenericQuery<Teacher>(Teacher.class);
		q.start(3);
		List<Teacher> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectLimit() {
		List<Teacher> expect = Teacher.creates(1, 2);
		
		GenericQuery<Teacher> q = new GenericQuery<Teacher>(Teacher.class);
		q.limit(2);
		List<Teacher> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectStartLimit() {
		List<Teacher> expect = Teacher.creates(3, 4);
		
		GenericQuery<Teacher> q = new GenericQuery<Teacher>(Teacher.class);
		q.start(2).limit(2);
		List<Teacher> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}


	@Test
	public void testSelectOrderStart() {
		List<Teacher> expect = Teacher.creates(4, 5);
		
		GenericQuery<Teacher> q = new GenericQuery<Teacher>(Teacher.class);
		q.orderByAsc("name").start(3);
		List<Teacher> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectOrderLimit() {
		List<Teacher> expect = Teacher.creates(1, 2);
		
		GenericQuery<Teacher> q = new GenericQuery<Teacher>(Teacher.class);
		q.orderByAsc("name").limit(2);
		List<Teacher> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectOrderStartLimit() {
		List<Teacher> expect = Teacher.creates(3, 4);
		
		GenericQuery<Teacher> q = new GenericQuery<Teacher>(Teacher.class);
		q.orderByAsc("name").start(2).limit(2);
		List<Teacher> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectPage() {
		List<Teacher> expect = Teacher.creates(2, 3);
		
		GenericQuery<Teacher> q = new GenericQuery<Teacher>(Teacher.class);
		q.start(1).limit(2);
		List<Teacher> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testSelectPageWhere() {
		List<Student> expect = Student.creates(2, 3);
		
		GenericQuery<Student> q = new GenericQuery<Student>(Student.class);
		q.greaterThan("id", 0).start(1).limit(2);
		List<Student> actual = dao.select(q);
		
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testDelete() {
		Score expect = Score.create(2, 2, 2);
		Assert.assertEquals(1, dao.delete(expect));
		
		Score actual = dao.fetch(Score.class, expect);
		Assert.assertNull(actual);
	}

	@Test
	public void testDeletes() {
		List<Score> expect = Score.creates(1, 1);
		Assert.assertEquals(expect.size(), dao.deletes(expect));
		
		for (Score s : expect) {
			Assert.assertNull(dao.fetch(Score.class, s));
		}
	}

	@Test
	public void testDeleteAll() {
		Assert.assertTrue(dao.deletes(Score.class) > 0);
		Assert.assertEquals(0, dao.count(Score.class));
	}
	
	@Test
	public void testUpdate() {
		Teacher expect = Teacher.create(2);
		expect.setMemo("update");

		Assert.assertEquals(1, dao.update(expect));
		
		Teacher actual = dao.fetch(Teacher.class, expect);
		Assert.assertEquals(expect, actual);
	}
	
	@Test
	public void testUpdateColumn() {
		Teacher expect = Teacher.create(2);
		expect.setMemo("update");

		Assert.assertEquals(1, dao.update(expect, "memo"));
		
		Teacher actual = dao.fetch(Teacher.class, expect);
		Assert.assertEquals(expect, actual);
	}
	
	@Test
	public void testUpdateIgnoreNull() {
		Teacher expect = Teacher.create(2);
		expect.setMemo("update");
	
		Teacher update = Teacher.create(2);
		update.setMemo(expect.getMemo());
		update.setData(null);

		Assert.assertEquals(1, dao.updateIgnoreNull(update));
		
		Teacher actual = dao.fetch(Teacher.class, expect);
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void testUpdates() {
		List<Teacher> expect = Teacher.creates(2, 3);
		expect.get(0).setMemo("update1");
		expect.get(1).setMemo("update2");

		Assert.assertEquals(expect.size(), dao.updates(expect));
		
		Assert.assertEquals(expect.get(0), dao.fetch(Teacher.class, expect.get(0)));
		Assert.assertEquals(expect.get(1), dao.fetch(Teacher.class, expect.get(1)));
	}

	@Test
	public void testUpdatesByQuery() {
		List<Teacher> expect = Teacher.creates(2, 3);
		expect.get(0).setMemo("u");
		expect.get(1).setMemo("u");

		Teacher t = new Teacher();
		t.setMemo("u");

		GenericQuery<Teacher> q = new GenericQuery<Teacher>(dao.getEntity(Teacher.class));
		q.in("name", "T2", "T3").excludeAll().include("memo");
		
		Assert.assertEquals(expect.size(), dao.updates(t, q));
		
		Assert.assertEquals(expect.get(0), dao.fetch(Teacher.class, expect.get(0)));
		Assert.assertEquals(expect.get(1), dao.fetch(Teacher.class, expect.get(1)));
	}

	@Test
	public void testUpdatesColumnByQuery() {
		List<Teacher> expect = Teacher.creates(2, 3);
		expect.get(0).setMemo(expect.get(0).getMemo() + "+u");
		expect.get(1).setMemo(expect.get(1).getMemo() + "+u");

		Teacher t = new Teacher();
		t.setMemo("u");

		GenericQuery<Teacher> q = new GenericQuery<Teacher>(dao.getEntity(Teacher.class));
		q.in("name", "T2", "T3").excludeAll().column("memo", "CONCAT(memo, '+u')");
		
		Assert.assertEquals(expect.size(), dao.updates(t, q));
		
		Assert.assertEquals(expect.get(0), dao.fetch(Teacher.class, expect.get(0)));
		Assert.assertEquals(expect.get(1), dao.fetch(Teacher.class, expect.get(1)));
	}

	@Test
	public void testExecDelete() {
		final Score expect = Score.create(2, 2, 2);
		dao.exec(new Runnable() {
			@Override
			public void run() {
				dao.delete(expect);
			}
		});

		Score actual = dao.fetch(Score.class, expect);
		Assert.assertNull(actual);
	}

	@Test
	public void testExecDelete2() {
		final Score expect1 = Score.create(2, 2, 2);
		final Score expect2 = Score.create(3, 3, 3);
		dao.exec(new Runnable() {
			@Override
			public void run() {
				dao.delete(expect1);
				dao.exec(new Runnable() {
					@Override
					public void run() {
						dao.delete(expect2);
					}
				});
			}
		});

		Score actual1 = dao.fetch(Score.class, expect1);
		Assert.assertNull(actual1);

		Score actual2 = dao.fetch(Score.class, expect2);
		Assert.assertNull(actual2);
	}
}
