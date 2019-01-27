package panda.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import panda.log.Log;
import panda.log.Logs;

public class ReloadableSettings extends Settings {

	private static final Log log = Logs.getLog(ReloadableSettings.class);
	
	private File file;

	/**
	 * loaded time
	 */
	private long loaded;

	/**
	 * last check time
	 */
	private long checkpoint = 0;
	
	/**
	 * check delay (default: 60s)
	 */
	private long delay = 60000;

	public ReloadableSettings() {
	}

	public ReloadableSettings(String file) throws IOException {
		load(file);
	}

	/**
	 * @return the delay
	 */
	public long getDelay() {
		return delay;
	}

	/**
	 * @param delay the delay to set
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	private synchronized void reload() {
		if (file != null && checkpoint > 0) {
			long now = System.currentTimeMillis();
			if (now > checkpoint) {
				checkpoint = now + delay;
				
				long lm = file.lastModified();
				if (lm != loaded) {
					try {
						load(file);
					}
					catch (IOException e) {
						log.error("Failed to reload " + file.getAbsolutePath());
					}
				}
			}
		}
	}
	
	
	//-------------------------------------------------------
	@Override
	public synchronized void load(File file) throws IOException {
		super.load(file);
		this.file = file;
		this.loaded = file.lastModified();
		this.checkpoint = System.currentTimeMillis() + delay;
	}

	//-------------------------------------------------------
	@Override
	public synchronized int size() {
		reload();
		return super.size();
	}

	@Override
	public synchronized boolean isEmpty() {
		reload();
		return super.isEmpty();
	}

	@Override
	public synchronized boolean containsKey(Object key) {
		reload();
		return super.containsKey(key);
	}

	@Override
	public synchronized boolean containsValue(Object value) {
		reload();
		return super.containsValue(value);
	}

	@Override
	public synchronized String get(Object key) {
		reload();
		return super.get(key);
	}

	@Override
	public synchronized String put(String key, String value) {
		return super.put(key, value);
	}

	@Override
	public synchronized String remove(Object key) {
		return super.remove(key);
	}

	@Override
	public synchronized void putAll(Map<? extends String, ? extends String> m) {
		super.putAll(m);
	}

	@Override
	public synchronized void clear() {
		super.clear();
	}

	@Override
	public synchronized Set<String> keySet() {
		reload();
		return super.keySet();
	}

	@Override
	public synchronized Collection<String> values() {
		reload();
		return super.values();
	}

	@Override
	public synchronized Set<java.util.Map.Entry<String, String>> entrySet() {
		reload();
		return super.entrySet();
	}
}