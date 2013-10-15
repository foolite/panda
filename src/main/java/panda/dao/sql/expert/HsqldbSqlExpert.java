package panda.dao.sql.expert;

import java.util.ArrayList;
import java.util.List;

import panda.dao.DB;
import panda.dao.criteria.Query;
import panda.dao.entity.Entity;
import panda.dao.entity.EntityField;
import panda.dao.sql.Sql;

/**
 */
public class HsqldbSqlExpert extends SqlExpert {
	@Override
	public DB getType() {
		return DB.HSQLDB;
	}

	@Override
	public boolean isSupportDropIfExists() {
		return true;
	}

	@Override
	public String dropTable(String tableName) {
		return "DROP TABLE " + escapeTable(tableName) + " IF EXISTS";
	}

	@Override
	public List<String> create(Entity<?> entity) {
		List<String> sqls = new ArrayList<String>();
		
		StringBuilder sb = new StringBuilder("CREATE TABLE " + entity.getTableName() + "(");
		for (EntityField ef : entity.getFields()) {
			sb.append('\n').append(ef.getColumn());
			sb.append(' ').append(evalFieldType(ef));
			
			// unsupported
//			if (ef.isUnsigned()) {
//				sb.append(" UNSIGNED");
//			}

			if (ef.isAutoIncrement()) {
				sb.append(" GENERATED BY DEFAULT AS IDENTITY(START WITH ").append(ef.getStartWith()).append(")");
			}
			else if (ef.isNotNull()) {
				sb.append(" NOT NULL");
			}

			if (ef.hasDefaultValue()) {
				sb.append(" DEFAULT '").append(ef.getDefaultValue()).append('\'');
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

	/**
	 * @param sql sql
	 * @param query query
	 * @see <a href="http://hsqldb.org/doc/guide/ch09.html#select-section">http://hsqldb.org/doc/guide/ch09.html#select-section</a>
	 */
	@Override
	protected void limit(Sql sql, Query query) {
		if (query.getStart() > 0 || query.getLimit() > 0) {
			sql.append(" LIMIT ").append(query.getLimit() > 0 ? query.getLimit() : Integer.MAX_VALUE);
			
			if (query.getStart() > 0) {
				sql.append(" OFFSET ").append(query.getStart());
			}
		}
	}
}
