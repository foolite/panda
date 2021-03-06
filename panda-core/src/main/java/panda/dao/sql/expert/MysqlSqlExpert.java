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

public class MysqlSqlExpert extends SqlExpert {
	@Override
	public DB getDatabaseType() {
		return DB.MYSQL;
	}

	@Override
	public String escape(String s) {
		return '`' + s + '`'; 
	}
	
	@Override
	public boolean isSupportDropIfExists() {
		return true;
	}

	@Override
	public String dropTable(String tableName) {
		return "DROP TABLE IF EXISTS " + escapeTable(tableName);
	}

	protected String getTableOption(Entity<?> entity, String name, String defv) {
		return getEntityOptionString(entity, "mysql-" + name, defv);
	}

	@Override
	public List<String> create(Entity<?> entity) {
		List<String> sqls = new ArrayList<String>();

		StringBuilder sb = new StringBuilder("CREATE TABLE " + escapeTable(client.getTableName(entity)) + " (");
		for (EntityField ef : entity.getFields()) {
			if (ef.isReadonly()) {
				continue;
			}

			sb.append(Streams.EOL);
			sb.append(escapeColumn(ef.getColumn())).append(' ').append(evalFieldType(ef));

			if (ef.isUnsigned()) {
				sb.append(" UNSIGNED");
			}

			if (ef.isNotNull()) {
				sb.append(" NOT NULL");
			}
			else if (DaoTypes.TIMESTAMP.equals(ef.getJdbcType())) {
				sb.append(" NULL");
			}
			
			if (ef.isAutoIncrement()) {
				sb.append(" AUTO_INCREMENT");
			}
/*			else {
				if (DaoTypes.TIMESTAMP.equals(ef.getJdbcType())) {
					if (ef.hasDefaultValue()) {
						sb.append(' ').append(ef.getDefaultValue());
					}
					else {
						if (ef.isNotNull()) {
							sb.append(" DEFAULT 0");
						}
						else {
							sb.append(" DEFAULT NULL");
						}
					}
				}
				else if (ef.hasDefaultValue()) {
					sb.append(" DEFAULT '").append(ef.getDefaultValue()).append('\'');
				}
			}*/
			else if (ef.hasDefaultValue()) {
				sb.append(" DEFAULT '").append(ef.getDefaultValue()).append('\'');
			}
			
			if (Strings.isNotEmpty(ef.getComment())) {
				sb.append(" COMMENT '").append(ef.getComment()).append("'");
			}
			sb.append(',');
		}

		addPrimaryKeys(sb, entity);
		sb.setCharAt(sb.length() - 1, ')');
		
		EntityField eid = entity.getIdentity();
		if (eid != null && eid.isAutoIncrement() && eid.getIdStartWith() > 1) {
			sb.append(" AUTO_INCREMENT=").append(eid.getIdStartWith());
		}

		String engine = getTableOption(entity, "engine", null);
		if (Strings.isNotEmpty(engine)) {
			sb.append(" ENGINE=" + engine);
		}
		
		String charset = getTableOption(entity, "charset", "UTF8");
		sb.append(" CHARSET=" + charset);

		if (Strings.isNotEmpty(entity.getComment())) {
			sb.append(" COMMENT='").append(entity.getComment()).append("'");
		}
		sqls.add(sb.toString());

		// sometimes mysql needs alter table sql to change the identity start value
		if (eid != null && eid.isAutoIncrement() && eid.getIdStartWith() > 1) {
			String sql = "ALTER TABLE " + client.getTableName(entity) + " AUTO_INCREMENT=" + eid.getIdStartWith();
			sqls.add(sql);
		}

		// add constraints
		addIndexes(sqls, entity);
		addForeignKeys(sqls, entity);
		return sqls;
	}

	/**
	 * @see <a href="http://dev.mysql.com/doc/refman/5.0/en/storage-requirements.html">http://dev.mysql.com/doc/refman/5.0/en/storage-requirements.html</a>
	 */
	@Override
	protected String evalFieldType(EntityField ef) {
		int jdbcType = DaoTypes.getType(ef.getJdbcType());
		switch (jdbcType) {
		case Types.TIMESTAMP:
			return "DATETIME";
		case Types.BLOB:
			return "LONGBLOB";
		case Types.LONGVARBINARY:
			return "MEDIUMBLOB";
		case Types.CLOB:
			return "LONGTEXT";
		case Types.LONGVARCHAR:
			return "MEDIUMTEXT";
		default:
			return super.evalFieldType(ef);
		}
	}

	/**
	 * @param sql sql
	 * @param query query
	 * @param alias table alias
	 * @see <a href="https://dev.mysql.com/doc/refman/5.5/en/select.html">https://dev.mysql.com/doc/refman/5.5/en/select.html</a>
	 */
	@Override
	protected void limit(Sql sql, Query<?> query, String alias) {
		if (query.getStart() > 0 || query.getLimit() > 0) {
			sql.append(" LIMIT ");
			if (query.getStart() > 0) {
				sql.append(query.getStart()).append(',');
			}
			sql.append(query.getLimit() > 0 ? query.getLimit() : Integer.MAX_VALUE);
		}
	}
}
