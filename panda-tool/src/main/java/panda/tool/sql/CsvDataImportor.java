package panda.tool.sql;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import panda.args.Option;
import panda.dao.sql.SqlExecutor;
import panda.io.stream.CsvReader;
import panda.lang.Strings;

/**
 * Import data from csv to database
 */
public class CsvDataImportor extends AbstractDataImportor {
	/**
	 * @param args arguments
	 */
	public static void main(String[] args) {
		new CsvDataImportor().execute(args);
	}

	/**
	 * Constructor
	 */
	public CsvDataImportor() {
		includes = new String[] { "**/*.csv" };
	}

	//---------------------------------------------------------------------------------------
	// properties
	//---------------------------------------------------------------------------------------
	protected String charset;
	
	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset the charset to set
	 */
	@Option(opt='C', option="charset", arg="CHARSET", usage="The charset of the file")
	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Override
	protected void importFile(FileInputStream fis) throws Exception {
		InputStreamReader isr;
		if (Strings.isEmpty(charset)) {
			isr = new InputStreamReader(fis);
		}
		else {
			isr = new InputStreamReader(fis, charset);
		}

		CsvReader csv = new CsvReader(isr);
		impCsvData(csv);
	}
	
	private void impCsvData(CsvReader csv) throws Exception {
		List<String> tns = csv.readList();
		if (tns == null || tns.isEmpty()) {
			throw new Exception("[" + currentFile.getName() + "] - the table name is empty!");
		}
		
		String tableName = tns.get(0);
		
		List<String> columns = csv.readList();
		if (columns == null || columns.isEmpty()) {
			throw new Exception("[" + tableName + "] - the table column is empty!");
		}
		
		List<String> row2 = csv.readList();
		if (row2 == null || row2.size() != columns.size()) {
			throw new Exception("[" + tableName + "] - the column types is incorrect!");
		}
		
		List<DataType> types = new ArrayList<DataType>();
		for (String v : row2) {
			types.add(new DataType(v));
		}
		
		println2("Importing table: " + tableName);
		if (truncate) {
			truncateTable(tableName);
		}

		String insertSql = getInsertSql(tableName, columns, types);
		try {
			SqlExecutor executor = getSqlExecutor(); 

			int cnt = 0;
			for (int i = 3; ; i++) {
				Map<String, Object> values = getRowValues(csv, i, columns, types);
				if (values == null) {
					break;
				}
				cntRecord += executor.update(insertSql, values);
				cnt++;
				if (commit > 0 && cnt >= commit) {
					connection.commit();
				}
			}

			if (cnt > 0) {
				connection.commit();
			}
		}
		catch (Exception e) {
			rollback();
			throw new Exception("Failed to import table [" + tableName + "]", e);
		}
	}	

	private Map<String, Object> getRowValues(CsvReader csv, int r, List<String> columns, List<DataType> types) throws Exception {
		List<String> row = csv.readList();
		if (row == null) {
			return null;
		}

		boolean empty = true;
		
		Map<String, Object> values = new HashMap<String, Object>(columns.size());
		for (int c = 0; c < columns.size(); c++) {
			String v = null;
			if (c < row.size()) {
				v = row.get(c);
			}
			
			try {
				Object cv = getCellValue(v, c, types);

				empty = (cv == null);

				values.put(columns.get(c), cv);
			}
			catch (Exception e) {
				throw new Exception("value is incorrect: (" + r + "," + c + ") - " + v, e);
			}
		}
		
		return empty ? null : values;
	}
}
