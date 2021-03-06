package panda.mvc.view.sitemesh;

import java.util.List;
import java.util.regex.Pattern;

import panda.lang.Collections;
import panda.lang.Regexs;
import panda.lang.Strings;

public class SitemeshConfig {
	public static class SitemeshDecorator {
		protected String headName;
		protected String headValue;
		protected String paraName;
		protected String paraValue;
		protected Pattern path;
		protected Pattern uri;
		public String page;
		
		public void setHead(String head) {
			String[] ss = Strings.split(head, '=');
			headName = ss[0];
			headValue = ss.length > 1 ? ss[1] : "";
		}
		
		public void setPara(String para) {
			String[] ss = Strings.split(para, '=');
			paraName = ss[0];
			paraValue = ss.length > 1 ? ss[1] : "";
		}
		
		public void setPath(String path) {
			this.path = Pattern.compile(path);
		}
		
		public void setUri(String uri) {
			this.uri = Pattern.compile(uri);
		}
	}
	
	protected List<Pattern> excludes;
	public List<SitemeshDecorator> decorators;
	
	public void setExcludes(List<String> excludes) {
		this.excludes = Regexs.compiles(excludes);
	}

	public void initIgnoreCase() {
		if (Collections.isNotEmpty(excludes)) {
			for (int i = 0; i < excludes.size(); i++) {
				Pattern p = excludes.get(i);
				excludes.set(i, Pattern.compile(p.pattern(), Pattern.CASE_INSENSITIVE));
			}
		}

		if (Collections.isNotEmpty(decorators)) {
			for (SitemeshDecorator sd : decorators) {
				if (sd.path != null) {
					sd.path = Pattern.compile(sd.path.pattern(), Pattern.CASE_INSENSITIVE);
				}
				if (sd.uri != null) {
					sd.uri = Pattern.compile(sd.uri.pattern(), Pattern.CASE_INSENSITIVE);
				}
			}
		}
	}
}
