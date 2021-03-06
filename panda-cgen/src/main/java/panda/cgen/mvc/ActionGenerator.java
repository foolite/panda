package panda.cgen.mvc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import freemarker.template.Configuration;
import freemarker.template.Template;
import panda.cgen.mvc.bean.Action;
import panda.cgen.mvc.bean.ActionProperty;
import panda.cgen.mvc.bean.Entity;
import panda.cgen.mvc.bean.InputUI;
import panda.cgen.mvc.bean.ListUI;
import panda.cgen.mvc.bean.Module;
import panda.dao.query.DataQuery;
import panda.lang.Arrays;
import panda.lang.Collections;
import panda.lang.Strings;
import panda.mvc.annotation.At;
import panda.mvc.annotation.To;
import panda.mvc.annotation.TokenProtect;
import panda.mvc.annotation.param.Param;
import panda.mvc.annotation.validate.RequiredValidate;
import panda.mvc.annotation.validate.VisitValidate;
import panda.mvc.bean.Queryer;
import panda.mvc.bean.QueryerEx;
import panda.mvc.view.Views;
import panda.mvc.view.util.ListColumn;
import panda.net.http.HttpMethod;

/**
 * action source generator
 */
public class ActionGenerator extends AbstractCodeGenerator {
	/**
	 * @param args arguments
	 */
	public static void main(String[] args) {
		new ActionGenerator().execute(args);
	}

	//---------------------------------------------------------------------------------------
	// properties
	//---------------------------------------------------------------------------------------
	private Template tplAction;

	private int cntAction = 0;
	
	@Override
	protected void loadTemplates(Configuration cfg) throws Exception {
		tplAction = cfg.getTemplate("action/Action.java.ftl");
	}

	@Override
	protected void processModule(Module module) throws Exception {
		for (Action action : module.getActionList()) {
			if (Boolean.TRUE.equals(action.getGenerate())) {
				print2("Processing action - " + action.getName() + " / " + action.getEntity());
				
				if (Strings.isEmpty(action.getEntity())) {
					throw new IllegalArgumentException("Missing entity: " + action.getName());
				}

				Entity ae = null;
				for (Entity entity : module.getEntityList()) {
					if (action.getEntity().equals(entity.getName())) {
						ae = entity;
						break;
					}
				}
				
				if (ae == null) {
					throw new IllegalArgumentException("Can not find entity[" + action.getEntity() + "] of action[" + action.getName() + "]");
				}
	
				processJavaAction(module, action, ae);
				
				cntAction++;
			}
		}
	}

	@Override
	protected void postProcess() throws Exception {
		print0(cntModule + " modules processed, " + cntFile + " files of " + cntAction + " actions generated successfully.");
	}

	private void prepareImportList(List<ActionProperty> ps, Set<String> imports) {
		for (ActionProperty p : ps) {
			String type = p.getFullJavaType();
			if (type.endsWith("[]")) {
				type = type.substring(0, type.length() - 2);
			}
			addImportType(imports, type);
		}
	}

	private final static Set<String> colus = Arrays.toSet("csv", "tsv", "xls", "xlsx");
	
	private void processJavaAction(Module module, Action action, Entity entity) throws Exception {
		String pkg = action.getActionPackage();

		checkLicense(module, pkg);
		
		String cls = action.getSimpleActionClass();

		Map<String, Object> wrapper = getWrapper(module, action, entity);

		Set<String> imports = new TreeSet<String>();
		setImports(wrapper, imports);

		prepareImportList(action.getPropertyList(), imports);
		imports.add(entity.getName());
		imports.add(action.getActionBaseClass());
		if (Collections.isNotEmpty(action.getSortedListUIList())) {
			for (ListUI lui : action.getSortedListUIList()) {
				String s = lui.getTemplate();
				if ("bdelete".equals(s) 
						|| "bupdate".equals(s)
						|| "bedit".equals(s)) {
					imports.add(TokenProtect.class.getName());
					imports.add(HttpMethod.class.getName());
					imports.add(Map.class.getName());
				}
				if ("import".equals(s)) {
					imports.add(TokenProtect.class.getName());
				}
				
				if ("list".equals(s) || Strings.startsWith(s, "list_")) {
					imports.add(VisitValidate.class.getName());
					imports.add(Queryer.class.getName());
				}
				if (Strings.startsWith(s, "expo_")) {
					imports.add(VisitValidate.class.getName());
					imports.add(QueryerEx.class.getName());
				}
				
				String se = Strings.substringAfter(s, '_');
				if (colus.contains(se)) {
					imports.add(List.class.getName());
					imports.add(ArrayList.class.getName());
					imports.add(ListColumn.class.getName());
				}
			}
		}

		imports.add(At.class.getName());
		imports.add(To.class.getName());
		imports.add(Param.class.getName());
		if (Collections.isNotEmpty(action.getSortedInputUIList())) {
			for (InputUI iui : action.getSortedInputUIList()) {
				if (Strings.equals(iui.getTemplate(), "copy") 
						|| Strings.equals(iui.getTemplate(), "edit")
						|| Strings.equals(iui.getTemplate(), "add")) {
					imports.add(VisitValidate.class.getName());
					imports.add(TokenProtect.class.getName());
					imports.add(HttpMethod.class.getName());
				}
				if (Strings.equals(iui.getTemplate(), "delete")) {
					imports.add(TokenProtect.class.getName());
					imports.add(HttpMethod.class.getName());
				}
			}
		}
		if (Collections.isNotEmpty(entity.getNotNullList()) && Collections.isNotEmpty(action.getSortedInputUIList())) {
			for (InputUI iui : action.getSortedInputUIList()) {
				if (Strings.equals(iui.getTemplate(), "copy") 
						|| Strings.equals(iui.getTemplate(), "edit")
						|| Strings.equals(iui.getTemplate(), "add")) {
					imports.add(VisitValidate.class.getName());
					if (Collections.isNotEmpty(iui.getRequiredValidateFieldList())) {
						imports.add(RequiredValidate.class.getName());
					}
				}
			}
		}
		imports.add(Views.class.getName());
		if (Strings.isNotEmpty(action.getAuth())) {
			imports.add("panda.app.auth.Auth");
			imports.add("panda.app.constant.AUTH");
		}
		
		if (Strings.isNotEmpty(action.getAutoJoin())) {
			imports.add(entity.getQueryName());
			imports.add(DataQuery.class.getName());
		}
		
		processTpl(pkg, cls + ".java", wrapper, tplAction, true);
	}

	private Map<String, Object> getWrapper(Module module, Action action, Entity entity) {
		Map<String, Object> wrapper = new HashMap<String, Object>();
		
		if ("true".equals(module.getProps().getProperty("source.datetime"))) {
			wrapper.put("date", Calendar.getInstance().getTime());
		}
		wrapper.put("module", module);
		wrapper.put("props", module.getProps());
		wrapper.put("action", action);
		wrapper.put("entity", entity);
		wrapper.put("gen", this);
		
		return wrapper;
	}
	
	private void setImports(Map<String, Object> wrapper, Object imports) {
		wrapper.put("imports", imports);
	}


	public String trimAtName(String nm) {
		if ("import".equals(nm)) {
			return "(\"import\")";
		}
		return "";
	}

	public String postAtName() {
		return "(method=HttpMethod.POST)";
	}

	public String postAtName(String nm) {
		if ("import".equals(nm)) {
			return "(value=\"import\", method=HttpMethod.POST)";
		}
		return "(method=HttpMethod.POST)";
	}

	public String trimMethodName(String nm) {
		if ("import".equals(nm)) {
			return "importx";
		}
		
		return Strings.replaceChars(nm, '.', '_');
	}

	public String translateToJava(String sv) {
		if (Strings.startsWithChar(sv, '"') && Strings.endsWithChar(sv, '"')) {
			return sv;
		}
		if (Strings.startsWith(sv, "assist.")) {
			return "assist().get" + Strings.capitalize(Strings.substringAfter(sv, "assist.")) + "()";
		}
		if (Strings.startsWith(sv, "consts.")) {
			return "consts().get(\"" + Strings.substringAfter(sv, "consts.") + "\")";
		}
		return "panda.mvc.Mvcs.findValue(\"" + sv + "\")";
	}
}
