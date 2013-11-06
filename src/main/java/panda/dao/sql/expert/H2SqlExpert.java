package panda.dao.sql.expert;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import panda.dao.DB;
import panda.dao.criteria.Query;
import panda.dao.entity.Entity;
import panda.dao.entity.EntityField;
import panda.dao.sql.JdbcTypes;
import panda.dao.sql.Sql;
import panda.lang.Strings;

public class H2SqlExpert extends SqlExpert {
	@Override
	public DB getDatabaseType() {
		return DB.H2;
	}

	@Override
	public boolean isSupportDropIfExists() {
		return true;
	}

	@Override
	public String dropTable(String tableName) {
		return "DROP TABLE IF EXISTS " + escapeTable(tableName);
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
			if (ef.isNotNull()) {
				sb.append(" NOT NULL");
			}

			if (ef.isAutoIncrement()) {
				sb.append(" AUTO_INCREMENT(").append(ef.getStartWith()).append(")");
			}
			else {
				if (ef.hasDefaultValue()) {
					sb.append(" DEFAULT '").append(ef.getDefaultValue()).append('\'');
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


	/**
	 * @see <a href="http://www.h2database.com/html/datatypes.html">http://www.h2database.com/html/datatypes.html</a>
	 */
	@Override
	protected String evalFieldType(EntityField ef) {
		if (Strings.isNotEmpty(ef.getDbType())) {
			return super.evalFieldType(ef);
		}
		
		int jdbcType = JdbcTypes.getType(ef.getJdbcType());
		switch (jdbcType) {
		case Types.BIT:
			return JdbcTypes.BOOLEAN;
		case Types.LONGVARCHAR:
			return evalFieldType(JdbcTypes.VARCHAR, ef.getSize(), ef.getScale());
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			return evalFieldType(JdbcTypes.BINARY, ef.getSize(), ef.getScale());
		default:
			return super.evalFieldType(ef);
		}
	}

	/**
	 * @param sql sql
	 * @param query query
	 * @see <a href="http://www.h2database.com/html/grammar.html">http://www.h2database.com/html/grammar.html</a>
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

