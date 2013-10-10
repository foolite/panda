package panda.dao.sql.expert;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import panda.bind.json.JsonObject;
import panda.lang.Charsets;
import panda.lang.ClassLoaders;
import panda.lang.Classes;
import panda.log.Log;
import panda.log.Logs;

public class SqlExpertConfig {
	private static Log log = Logs.getLog(SqlExpertConfig.class);
	
	private Map<String, Class<? extends SqlExpert>> experts;

	private Map<Pattern, Class<? extends SqlExpert>> _experts;

	private Map<String, Object> properties;

	public SqlExpertConfig() {
		InputStream is = ClassLoaders.getResourceAsStream("sql-experts.json");
		if (is == null) {
			is = ClassLoaders.getResourceAsStream(
				SqlExpertConfig.class.getPackage().getName().replace('.', '/') + "/sql-experts.json");
		}
		if (is == null) {
			throw new RuntimeException("Failed to find sql-experts.json");
		}
		
		JsonObject jo = JsonObject.fromJson(is, Charsets.UTF_8);
		
		setExperts(jo.getJsonObject("experts"));
		setProperties(jo.optJsonObject("properties"));
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public SqlExpert getExpert(String str) {
		Class<? extends SqlExpert> type = experts.get(str);
		try {
			return type.newInstance();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public SqlExpert matchExpert(String dbName) {
		for (Entry<Pattern, Class<? extends SqlExpert>> entry : _experts.entrySet()) {
			if (entry.getKey().matcher(dbName).find()) {
				try {
					return entry.getValue().newInstance();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}

	public Map<String, Class<? extends SqlExpert>> getExperts() {
		return experts;
	}

	@SuppressWarnings("unchecked")
	public void setExperts(Map<String, Object> exps) {
		this.experts = new HashMap<String, Class<? extends SqlExpert>>();
		this._experts = new HashMap<Pattern, Class<? extends SqlExpert>>();
		for (Entry<String, Object> entry : exps.entrySet()) {
			Class clazz;
			try {
				clazz = Classes.getClass(entry.getValue().toString());
			}
			catch (ClassNotFoundException e) {
				log.warn("Failed to get class " + entry.getValue() + " for " + entry.getKey());
				continue;
			}

			// case insensitive
			Pattern pattern = Pattern.compile(entry.getKey(), Pattern.DOTALL & Pattern.CASE_INSENSITIVE);
			experts.put(entry.getKey(), clazz);
			_experts.put(pattern, clazz);
		}
	}
}
