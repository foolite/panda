package panda.dao.entity;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;

import panda.bean.BeanHandler;
import panda.bean.Beans;
import panda.bind.json.JsonObject;
import panda.dao.DaoClient;
import panda.dao.entity.annotation.ColDefine;
import panda.dao.entity.annotation.Column;
import panda.dao.entity.annotation.Comment;
import panda.dao.entity.annotation.FK;
import panda.dao.entity.annotation.Id;
import panda.dao.entity.annotation.Index;
import panda.dao.entity.annotation.PK;
import panda.dao.entity.annotation.Post;
import panda.dao.entity.annotation.Prep;
import panda.dao.entity.annotation.Readonly;
import panda.dao.entity.annotation.SQL;
import panda.dao.entity.annotation.Table;
import panda.dao.entity.annotation.TableFKeys;
import panda.dao.entity.annotation.TableIndexes;
import panda.dao.entity.annotation.TableMeta;
import panda.dao.entity.annotation.View;
import panda.dao.sql.JdbcTypes;
import panda.dao.sql.SqlNamings;
import panda.lang.Classes;
import panda.lang.Exceptions;
import panda.lang.Strings;
import panda.lang.Types;
import panda.log.Log;
import panda.log.Logs;

/**
 * Create a Entity by Class
 * @author yf.frank.wang@gmail.com
 */
public class AnnotationEntityMaker implements EntityMaker {
	private static final Log log = Logs.getLog(AnnotationEntityMaker.class);

	private DaoClient client;
	
	public AnnotationEntityMaker(DaoClient client) {
		this.client = client; 
	}

	public <T> Entity<T> make(Class<T> type) {
		Entity<T> en = createEntity(type);

		Collection<Field> fields = Classes.getFields(type);

		// Is @Column declared
		boolean shouldUseColumn = false;
		for (Field field : fields) {
			if (null != field.getAnnotation(Column.class)) {
				shouldUseColumn = true;
				break;
			}
		}

		// loop for fields
		for (Field field : fields) {
			addEntityField(en, field, shouldUseColumn);
		}

		// loop for methods
		for (Method method : Classes.getMethods(en.getType())) {
			addEntityField(en, method);
		}

		// check empty
		if (en.getFields().isEmpty()) {
			throw new IllegalArgumentException(type + " has no Mapping Field!!");
		}

		// find identity field & check primary key
		for (EntityField ef : en.getFields()) {
			if (ef.isIdentity()) {
				if (en.getIdentity() != null) {
					throw new IllegalArgumentException("Allows only a single @Id of " + type);
				}
				en.setIdentity(ef);
			}
			if (ef.isPrimaryKey()) {
				en.addPrimaryKey(ef);
			}
		}

		// fix primary key not null
		for (EntityField ef : en.getPrimaryKeys()) {
			ef.setNotNull(true);
		}

		// evaluate indexes
		TableIndexes annIndexes = Classes.getAnnotation(type, TableIndexes.class);
		if (annIndexes != null) {
			evalEntityIndexes(en, annIndexes);
		}

		// evaluate foreign keys
		TableFKeys annFKeys = Classes.getAnnotation(type, TableFKeys.class);
		if (annFKeys != null) {
			evalEntityFKeys(en, annFKeys);
		}

		// done
		return en;
	}

	private <T> Entity<T> createEntity(Class<T> type) {
		Entity<T> en = new Entity<T>(type);

		BeanHandler<T> bh = client.getBeans().getBeanHandler(type);
		if (bh == null) {
			throw new RuntimeException("Failed to get BeanHander for " + type);
		}
		en.setBeanHandler(bh);
		
		// Table meta
		TableMeta annMeta = Classes.getAnnotation(type, TableMeta.class);
		if (annMeta != null) {
			JsonObject jo = JsonObject.fromJson(annMeta.value());
			en.setTableMeta(jo);
		}

		Table annTable = Classes.getAnnotation(type, Table.class);
		// table name
		if (annTable == null) {
			en.setTableName(SqlNamings.javaName2TableName(type.getSimpleName()));
		}
		else {
			en.setTableName(annTable.value());
		}

		// table view
		View annView = Classes.getAnnotation(type, View.class);
		if (annView == null) {
			en.setViewName(en.getTableName());
		}
		else {
			en.setViewName(annView.value());
		}

		// check table or view
		if (Strings.isEmpty(en.getTableName()) && Strings.isEmpty(en.getViewName())) {
			throw new IllegalArgumentException("@Table or @View of [" + type + "] is not defined");
		}
		
		// table comment
		Comment annComment = Classes.getAnnotation(type, Comment.class);
		if (annComment != null) {
			en.setComment(annComment.value());
		}
		return en;
	}

	/**
	 * guess the entity field jdbc type by java type
	 * 
	 * @param ef entity field
	 */
	public static void guessEntityFieldJdbcType(EntityField ef) {
		Class<?> clazz = Types.getRawType(ef.getType());

		if (Classes.isBoolean(clazz)) {
			ef.setJdbcType(JdbcTypes.BOOLEAN);
			ef.setSize(1);
		}
		else if (Classes.isChar(clazz)) {
			ef.setJdbcType(JdbcTypes.CHAR);
			ef.setSize(1);
		}
		else if (Classes.isByte(clazz)) {
			ef.setJdbcType(JdbcTypes.TINYINT);
		}
		else if (Classes.isFloat(clazz)) {
			ef.setJdbcType(JdbcTypes.FLOAT);
		}
		else if (Classes.isDouble(clazz)) {
			ef.setJdbcType(JdbcTypes.DOUBLE);
		}
		else if (Classes.isInt(clazz)) {
			ef.setJdbcType(JdbcTypes.INTEGER);
		}
		else if (Classes.isShort(clazz)) {
			ef.setJdbcType(JdbcTypes.SMALLINT);
		}
		else if (Classes.isLong(clazz)) {
			ef.setJdbcType(JdbcTypes.BIGINT);
		}
		else if (Classes.isStringLike(clazz)) {
			ef.setJdbcType(JdbcTypes.VARCHAR);
			ef.setSize(50);
		}
		else if (Classes.isEnum(clazz)) {
			ef.setJdbcType(JdbcTypes.VARCHAR);
			ef.setSize(20);
		}
		else if (Classes.isAssignable(clazz, java.sql.Timestamp.class)) {
			ef.setJdbcType(JdbcTypes.TIMESTAMP);
		}
		else if (Classes.isAssignable(clazz, java.sql.Date.class)) {
			ef.setJdbcType(JdbcTypes.DATE);
		}
		else if (Classes.isAssignable(clazz, java.sql.Time.class)) {
			ef.setJdbcType(JdbcTypes.TIME);
		}
		else if (Classes.isAssignable(clazz, Calendar.class) || Classes.isAssignable(clazz, java.util.Date.class)) {
			ef.setJdbcType(JdbcTypes.TIMESTAMP);
		}
		else if (Classes.isAssignable(clazz, BigDecimal.class)) {
			ef.setJdbcType(JdbcTypes.DECIMAL);
			ef.setSize(20);
			ef.setScale(2);
		}
		else if (Classes.isAssignable(clazz, Reader.class)) {
			ef.setJdbcType(JdbcTypes.CLOB);
		}
		else if (Classes.isAssignable(clazz, InputStream.class) || byte[].class.equals(clazz)) {
			ef.setJdbcType(JdbcTypes.BLOB);
		}
		else {
			// default to string
			if (log.isDebugEnabled()) {
				log.debugf("take field '%s(%s)' as VARCHAR(50)", ef.getName(), clazz.toString());
			}
			ef.setJdbcType(JdbcTypes.VARCHAR);
			ef.setSize(50);
		}
	}

	private static class MappingInfo {
		String name;
		Type type;

		Id annId;
		PK annPk;
		FK annFk;
		Index annIndex;
		Column annColumn;
		Comment annComment;
		ColDefine annDefine;
		Readonly annReadonly;
		Prep annPrep;
		Post annPost;
		
		public static MappingInfo create(Field field, boolean useColumn) {
			MappingInfo mi = new MappingInfo();
			mi.annId = field.getAnnotation(Id.class);
			mi.annPk = field.getAnnotation(PK.class);
			mi.annFk = field.getAnnotation(FK.class);
			mi.annIndex = field.getAnnotation(Index.class);
			mi.annColumn = field.getAnnotation(Column.class);

			if (Modifier.isTransient(field.getModifiers())) {
				useColumn = true;
			}

			if (useColumn && mi.annColumn == null && mi.annId == null && mi.annPk == null && mi.annFk == null
					&& mi.annIndex == null) {
				return null;
			}

			mi.name = field.getName();
			mi.type = field.getGenericType();
			mi.annComment = field.getAnnotation(Comment.class);
			mi.annDefine = field.getAnnotation(ColDefine.class);
			mi.annReadonly = field.getAnnotation(Readonly.class);
			mi.annPrep = field.getAnnotation(Prep.class);
			mi.annPost = field.getAnnotation(Post.class);
			return mi;
		}

		public static MappingInfo create(Method method) {
			MappingInfo mi = new MappingInfo();
			mi.annId = method.getAnnotation(Id.class);
			mi.annPk = method.getAnnotation(PK.class);
			mi.annFk = method.getAnnotation(FK.class);
			mi.annIndex = method.getAnnotation(Index.class);
			mi.annColumn = method.getAnnotation(Column.class);

			if (mi.annColumn == null && mi.annId == null && mi.annPk == null && mi.annFk == null && mi.annIndex == null) {
				return null;
			}

			String name = Beans.getBeanName(method);
			if (Strings.isEmpty(name)) {
				throw Exceptions.makeThrow("Method '%s'(%s) can not add '@Column', it MUST be a setter or getter!",
					method.getName(), method.getDeclaringClass().getName());
			}

			mi.name = name;
			mi.type = method.getGenericReturnType();
			mi.annComment = method.getAnnotation(Comment.class);
			mi.annDefine = method.getAnnotation(ColDefine.class);
			mi.annReadonly = method.getAnnotation(Readonly.class);
			mi.annPrep = method.getAnnotation(Prep.class);
			mi.annPost = method.getAnnotation(Post.class);
			return mi;
		}
	}
	
	private void addEntityField(Entity<?> entity, MappingInfo mi) {
		EntityField ef = new EntityField();

		if (mi.annId != null) {
			ef.setIdentity(true);
			ef.setAutoIncrement(mi.annId.auto());
			ef.setStartWith(mi.annId.start());
			ef.setPrimaryKey(true);
		}
		if (mi.annPk != null) {
			ef.setPrimaryKey(true);
		}
		if (mi.annReadonly != null) {
			ef.setReadonly(true);
		}

		ef.setName(mi.name);
		ef.setType(mi.type);
		if (mi.annColumn != null && Strings.isNotBlank(mi.annColumn.value())) {
			ef.setColumn(mi.annColumn.value());
		}
		else {
			ef.setColumn(mi.name);
		}

		if (mi.annComment != null && Strings.isNotBlank(mi.annComment.value())) {
			ef.setComment(mi.annComment.value());
		}

		if (mi.annDefine != null) {
			ef.setJdbcType(mi.annDefine.type());
			ef.setSize(mi.annDefine.size());
			ef.setScale(mi.annDefine.scale());
			ef.setUnsigned(mi.annDefine.unsigned());
			ef.setNotNull(mi.annDefine.notNull());

			if (Strings.isNotBlank(mi.annDefine.dbType())) {
				ef.setDbType(mi.annDefine.dbType());
			}
		}
		else {
			guessEntityFieldJdbcType(ef);
		}

		if (mi.annPrep != null) {
			for (SQL s : mi.annPrep.value()) {
				entity.addPrepSql(s.db(), s.value());
			}
		}
		if (mi.annPost != null) {
			for (SQL s : mi.annPost.value()) {
				entity.addPostSql(s.db(), s.value());
			}
		}

		if (mi.annFk != null) {
			evalEntityFKey(entity, mi.annFk.name(), mi.annFk.target(), ef);
		}
		
		if (mi.annIndex != null) {
			evalEntityIndex(entity, mi.annIndex.name(), ef, mi.annIndex.unique());
		}

		entity.addField(ef);
	}

	private void addEntityField(Entity<?> entity, Field field, boolean useColumn) {
		MappingInfo mi = MappingInfo.create(field, useColumn);
		if (mi == null) {
			return;
		}
	
		addEntityField(entity, mi);
	}

	private void addEntityField(Entity<?> entity, Method method) {
		MappingInfo mi = MappingInfo.create(method);
		if (mi == null) {
			return;
		}
	
		addEntityField(entity, mi);
	}

	private void evalEntityIndex(Entity<?> en, String name, EntityField field, boolean unique) {
		EntityIndex ei = new EntityIndex();
		ei.setUnique(unique);
		ei.setName(Strings.isEmpty(name) ? field.getName() : name);
		ei.addField(field);
		en.addIndex(ei);
	}

	private void evalEntityIndex(Entity<?> en, String name, String[] fields, boolean unique) {
		EntityIndex ei = new EntityIndex();
		ei.setUnique(unique);
		ei.setName(Strings.isEmpty(name) ? Strings.join(fields, '_') : name);
		if (fields == null || fields.length == 0) {
			throw Exceptions.makeThrow("Empty fields for @Index(%s: %s)", 
				ei.getName(), Strings.join(fields, '|'));
		}
		for (String in : fields) {
			EntityField ef = en.getField(in);
			if (null == ef) {
				throw Exceptions.makeThrow("Failed to find field '%s' in '%s' for @Index(%s: %s)", 
					in, en.getType(), ei.getName(), Strings.join(fields, '|'));
			}
			ei.addField(ef);
		}
		en.addIndex(ei);
	}

	private void evalEntityIndexes(Entity<?> en, TableIndexes indexes) {
		for (Index idx : indexes.value()) {
			evalEntityIndex(en, idx.name(), idx.fields(), idx.unique());
		}
	}

	private void evalEntityFKey(Entity<?> en, String name, Class<?> target, String[] fields) {
		EntityFKey efk = new EntityFKey();
		Entity<?> ref = client.getEntity(target);
		if (null == ref) {
			throw new IllegalArgumentException("Failed to find target entity for " + target);
		}
		efk.setReference(ref);
		efk.setName(Strings.isEmpty(name) ? ref.getTableName() : name);

		if (fields == null || fields.length == 0) {
			throw Exceptions.makeThrow("Empty fields for @FK(%s: %s)", efk.getName(), Strings.join(fields, '|'));
		}
		for (String in : fields) {
			EntityField ef = en.getField(in);
			if (null == ef) {
				throw Exceptions.makeThrow("Failed to find field '%s' in '%s' for @FK(%s: %s)", 
					in, en.getType(), efk.getName(), Strings.join(fields, '|'));
			}
			efk.addField(ef);
		}
		en.addForeignKey(efk);
	}

	private void evalEntityFKey(Entity<?> en, String name, Class<?> target, EntityField field) {
		EntityFKey efk = new EntityFKey();
		Entity<?> ref = client.getEntity(target);
		if (null == ref) {
			throw new IllegalArgumentException("Failed to find target entity for " + target);
		}
		efk.setReference(ref);
		efk.setName(Strings.isEmpty(name) ? ref.getTableName() : name);

		efk.addField(field);
		en.addForeignKey(efk);
	}

	private void evalEntityFKeys(Entity<?> en, TableFKeys fks) {
		for (FK fk: fks.value()) {
			evalEntityFKey(en, fk.name(), fk.target(), fk.fields());
		}
	}
}
