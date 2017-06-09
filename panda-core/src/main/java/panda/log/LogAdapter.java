package panda.log;

import java.util.Properties;

public interface LogAdapter {

	void init(String name, Properties props);
	
	Log getLog(String name);

}
