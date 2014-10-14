package panda.tool.codegen2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import panda.lang.Strings;
import panda.tool.codegen.bean.Action;
import panda.tool.codegen.bean.ActionProperty;
import panda.tool.codegen.bean.Entity;
import panda.tool.codegen.bean.InputUI;
import panda.tool.codegen.bean.ListUI;
import panda.tool.codegen.bean.Module;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * action source generator
 */
public class ActionGenerator extends AbstractCodeGenerator {
	/**
	 * Main class for ActionGenerator
	 */
	public static class Main extends AbstractCodeGenerator.Main {
		/**
		 * @param args arguments
		 */
		public static void main(String[] args) {
			Main cgm = new Main();
			
			AbstractCodeGenerator cg = new ActionGenerator();

			cgm.execute(cg, args);
		}
	}

	//---------------------------------------------------------------------------------------
	// properties
	//---------------------------------------------------------------------------------------
	private Template tplAction;
	private Template tplActionV;
	private Template tplActionLV;
	private Template tplActionVV;
	private Template tplActionIV;
	private Template tplActionUV;

	private int cntAction = 0;
	
	@Override
	protected void loadTemplates(Configuration cfg) throws Exception {
		tplAction = cfg.getTemplate("action/Action.java.ftl");
		tplActionV = cfg.getTemplate("action/Action-validation.xml.ftl");
		tplActionLV = cfg.getTemplate("action/Action-list-validation.xml.ftl");
		tplActionVV = cfg.getTemplate("action/Action-view-validation.xml.ftl");
		tplActionIV = cfg.getTemplate("action/Action-insert-validation.xml.ftl");
		tplActionUV = cfg.getTemplate("action/Action-update-validation.xml.ftl");
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

	private void processJavaAction(Module module, Action action, Entity entity) throws Exception {
		String pkg = action.getActionPackage();

		checkLicense(module, pkg);
		
		String cls = action.getSimpleActionClass();

		Map<String, Object> wrapper = getWrapper(module, action, entity);

		Set<String> imports = new TreeSet<String>();
		prepareImportList(action.getPropertyList(), imports);

		imports.add(List.class.getName());
		imports.add(entity.getName());
		imports.add(action.getActionBaseClass());
		
		setImports(wrapper, imports);
		processTpl(pkg, cls + ".java", wrapper, tplAction, true);

		if (!action.getPropertyList().isEmpty()) {
			processTpl(pkg, cls + "-validation.xml", wrapper, tplActionV);
		}

		for (ListUI lui : action.getListUIList()) {
			if (Boolean.TRUE.equals(lui.getGenerate())) {
				setActionUI(wrapper, lui);
				processTpl(pkg, cls + "-" + action.getName() + "_" + lui.getName()
						+ "-validation.xml", wrapper, tplActionLV);
			}
		}
		
		for (InputUI iui : action.getInputUIList()) {
			if (Boolean.TRUE.equals(iui.getGenerate())) {
				setActionUI(wrapper, iui);

				if (iui.getTemplates().contains("view") || iui.getTemplates().contains("print")) {
					processTpl(pkg, cls + "-" + action.getName() + "_" + iui.getName()
							+ "-validation.xml", wrapper, tplActionVV);
				}
				else if (iui.getTemplates().contains("delete")) {
					processTpl(pkg, cls + "-" + action.getName() + "_" + iui.getName()
							+ "-validation.xml", wrapper, tplActionVV);
					processTpl(pkg, cls + "-" + action.getName() + "_" + iui.getName()
							+ "_execute-validation.xml", wrapper, tplActionVV);
				}
				else if (iui.getTemplates().contains("insert")) {
					processTpl(pkg, cls + "-" + action.getName() + "_" + iui.getName()
							+ "_confirm-validation.xml", wrapper, tplActionIV);
					processTpl(pkg, cls + "-" + action.getName() + "_" + iui.getName()
							+ "_execute-validation.xml", wrapper, tplActionIV);
				}
				else if (iui.getTemplates().contains("copy")) {
					processTpl(pkg, cls + "-" + action.getName() + "_" + iui.getName()
							+ "-validation.xml", wrapper, tplActionVV);
					processTpl(pkg, cls + "-" + action.getName() + "_" + iui.getName()
							+ "_confirm-validation.xml", wrapper, tplActionIV);
					processTpl(pkg, cls + "-" + action.getName() + "_" + iui.getName()
							+ "_execute-validation.xml", wrapper, tplActionIV);
				}
				else if (iui.getTemplates().contains("update")) {
					processTpl(pkg, cls + "-" + action.getName() + "_" + iui.getName()
							+ "-validation.xml", wrapper, tplActionVV);
					processTpl(pkg, cls + "-" + action.getName() + "_" + iui.getName()
							+ "_confirm-validation.xml", wrapper, tplActionUV);
					processTpl(pkg, cls + "-" + action.getName() + "_" + iui.getName()
							+ "_execute-validation.xml", wrapper, tplActionUV);
				}
			}
		}
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
	
	private void setActionUI(Map<String, Object> wrapper, Object actionUI) {
		wrapper.put("ui", actionUI);
	}
	
	private void setImports(Map<String, Object> wrapper, Object imports) {
		wrapper.put("imports", imports);
	}


}
