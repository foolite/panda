package panda.dao.sql.expert;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import panda.dao.DB;
import panda.dao.DaoTypes;
import panda.dao.entity.Entity;
import panda.dao.entity.EntityField;
import panda.dao.query.Query;
import panda.dao.sql.Sql;
import panda.lang.Classes;
import panda.lang.Strings;

public class DerbySqlExpert extends SqlExpert {
	@Override
	public DB getDatabaseType() {
		return DB.DERBY;
	}

	@Override
	protected String escapeTable(String table) {
		return '"' + table + '"'; 
	}
	
	@Override
	protected String escapeColumn(String column) {
		return '"' + column + '"'; 
	}

	@Override
	public List<String> create(Entity<?> entity) {
		List<String> sqls = new ArrayList<String>();
		
		StringBuilder sb = new StringBuilder("CREATE TABLE " + escapeTable(entity.getTableName()) + " (");
		for (EntityField ef : entity.getFields()) {
			sb.append('\n').append(escapeColumn(ef.getColumn()));
			sb.append(' ').append(evalFieldType(ef));
			
			// unsupported
//			if (ef.isUnsigned()) {
//				sb.append(" UNSIGNED");
//			}

			if (ef.isNotNull()) {
				sb.append(" NOT NULL");
			}

			if (ef.isAutoIncrement()) {
				sb.append(" GENERATED BY DEFAULT AS IDENTITY (START WITH ").append(ef.getStartWith()).append(", INCREMENT BY 1)");
			}
			else if (ef.hasDefaultValue()) {
				sb.append(" DEFAULT '").append(ef.getDefaultValue()).append('\'');
			}
			
			sb.append(',');
		}

		// append primary keys
		addPrimaryKeys(sb, entity);
		sb.setCharAt(sb.length() - 1, ')');
		sqls.add(sb.toString());

		// unsupported
		//addComments(sqls, entity);

		// add constraints
		addIndexes(sqls, entity);
		addForeignKeys(sqls, entity);
		return sqls;
	}


	/**
	 * @see <a href="http://db.apache.org/derby/docs/10.7/ref/crefsqlj31068.html">http://db.apache.org/derby/docs/10.7/ref/crefsqlj31068.html</a>
	 */
	@Override
	protected String evalFieldType(EntityField ef) {
		if (Strings.isNotEmpty(ef.getNativeType())) {
			return super.evalFieldType(ef);
		}
		
		int jdbcType = DaoTypes.getType(ef.getJdbcType());
		switch (jdbcType) {
		case Types.BIT:
		case Types.BOOLEAN:
			return "CHAR(1)";
		case Types.LONGVARCHAR:
			return evalFieldType(DaoTypes.CLOB, ef.getSize(), ef.getScale());
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			return evalFieldType(DaoTypes.BLOB, ef.getSize(), ef.getScale());
		default:
			return super.evalFieldType(ef);
		}
	}

	/**
	 * @param sql sql
	 * @param query query
	 * @see <a href="http://db.apache.org/derby/docs/10.10/ref/rrefsqlj41360.html">http://db.apache.org/derby/docs/10.10/ref/rrefsqlj41360.html</a>
	 */
	@Override
	protected void limit(Sql sql, Query query) {
		if (query.getStart() > 0) {
			sql.append(" OFFSET ").append(query.getStart()).append(" ROWS");
		}
		if (query.getLimit() > 0) {
			sql.append(" FETCH NEXT ").append(query.getLimit()).append(" ROWS ONLY");
		}
	}

	@Override
	protected Object getFieldValue(EntityField ef, Object data) {
		Object v = ef.getValue(data);
		if (v != null && Classes.isBoolean(v.getClass())) {
			return ((Boolean)v).booleanValue() ? '1' : '0';
		}
		return v;
	}
}

