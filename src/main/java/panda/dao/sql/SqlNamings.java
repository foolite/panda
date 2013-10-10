package panda.dao.sql;

import panda.lang.Strings;
import panda.lang.Texts;


/**
 * @author yf.frank.wang@gmail.com
 */
public class SqlNamings {
	/**
	 * javaName2ColumnLabel
	 * @param javaName java style name
	 * @return sql column label
	 */
	public static String javaName2ColumnLabel(String javaName) {
		if (Strings.isAllUpperCase(javaName)) {
			return javaName;
		}
		
		StringBuilder sb = new StringBuilder();
		int len = javaName.length();
		for (int i = 0; i < len; i++) {
			char c = javaName.charAt(i);
			if (c == '.') {
				sb.append("_0_");
			}
			else if (Character.isUpperCase(c)) {
				sb.append('_');
				sb.append(c);
			}
			else {
				sb.append(Character.toUpperCase(c));
			}
		}
		
		if (sb.length() < 1) {
			throw new IllegalArgumentException("Illegal java name: " + javaName);
		}
		return sb.toString();
	}

	/**
	 * columnLabel2JavaName
	 * @param columnLabel sql column label
	 * @return java style name
	 */
	public static String columnLabel2JavaName(String columnLabel) {
		if (Strings.isEmpty(columnLabel)) {
			return Strings.EMPTY;
		}
		
		StringBuilder sb = new StringBuilder();
		String javaName = Strings.replace(columnLabel, "_0_", ".");
		
		boolean toUpper = false;
		int len = javaName.length();
		for (int i = 0; i < len; i++) {
			char c = javaName.charAt(i);
			if (c == '.') {
				sb.append(c);
			}
			else if (c == '_') {
				toUpper = true;
			}
			else if (toUpper) {
				sb.append(Character.toUpperCase(c));
				toUpper = false;
			}
			else {
				sb.append(Character.toLowerCase(c));
			}
		}
		return sb.toString();
	}

	/**
	 * convert a java class name to table name
	 * e.g.: 'HelloWorld' -> 'Hello_World'
	 * @param javaName java style name
	 * @return table name
	 */
	public static String javaName2TableName(String javaName) {
		return Texts.uncamelWord(javaName, '_');
	}
}
