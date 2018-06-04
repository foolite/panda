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
import panda.io.Streams;
import panda.lang.Strings;

public class Db2SqlExpert extends SqlExpert {
	@Override
	public DB getDatabaseType() {
		return DB.DB2;
	}

	@Override
	public List<String> create(Entity<?> entity) {
		List<String> sqls = new ArrayList<String>();

		StringBuilder sb = new StringBuilder("CREATE TABLE " + escapeTable(client.getTableName(entity)) + " (");
		for (EntityField ef : entity.getFields()) {
			if (ef.isReadonly()) {
				continue;
			}

			sb.append(Streams.LINE_SEPARATOR);
			sb.append(escapeColumn(ef.getColumn())).append(' ').append(evalFieldType(ef));
			if (ef.isNotNull()) {
				sb.append(" NOT NULL");
			}
			if (ef.hasDefaultValue()) {
				sb.append(" DEFAULT '").append(ef.getDefaultValue()).append('\'');
			}
			if (ef.isAutoIncrement()) {
				sb.append(" GENERATED BY DEFAULT AS IDENTITY ");
				if (ef.getIdStartWith() > 1) {
					sb.append("(START WITH ").append(ef.getIdStartWith()).append(" INCREMENT BY 1) ");
				}
			}
			sb.append(',');
		}
		// append primary keys
		addPrimaryKeys(sb, entity);
		sb.setCharAt(sb.length() - 1, ')');
		sqls.add(sb.toString());

		// add comments & constraints
		addComments(sqls, entity);
		addIndexes(sqls, entity);
		addForeignKeys(sqls, entity);
		return sqls;
	}

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
		case Types.TINYINT:
			return DaoTypes.SMALLINT;
		case Types.FLOAT:
			return "DECFLOAT";
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
		case Types.BLOB:
			return DaoTypes.BLOB;
		case Types.CLOB:
		case Types.LONGVARCHAR:
			return DaoTypes.CLOB;
		default:
			break;
		}
		return super.evalFieldType(ef);
	}

	/**
	 * @param sql sql
	 * @param query query
	 */
	@Override
	protected void limit(Sql sql, Query<?> query, String alias) {
		// TODO: DB2 jdbc PreparedStatement does not support ROW_NUMBER
		if (query.getStart() > 0) {
			sql.insert(0, "SELECT * FROM (SELECT ROW_NUMBER() OVER() AS RN_, T.* FROM (");
			sql.append(") T) WHERE RN_ > ").append(query.getStart());
		}
		
		if (query.getLimit() > 0) {
			sql.append(" FETCH FIRST ").append(query.getLimit()).append(" ROWS ONLY");
		}
	}

	public boolean isSupportPaginate() {
		return false;
	}

	public boolean isSupportScroll() {
		// DB2 does not support ResultSet.TYPE_SCROLL_INSENSITIVE
		return false;
	}
}
