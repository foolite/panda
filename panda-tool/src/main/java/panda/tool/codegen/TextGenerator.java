package panda.tool.codegen;

import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;

import panda.io.Streams;
import panda.lang.Charsets;
import panda.lang.Strings;
import panda.tool.codegen.bean.Action;
import panda.tool.codegen.bean.ActionProperty;
import panda.tool.codegen.bean.Entity;
import panda.tool.codegen.bean.EntityProperty;
import panda.tool.codegen.bean.InputField;
import panda.tool.codegen.bean.InputUI;
import panda.tool.codegen.bean.ListColumn;
import panda.tool.codegen.bean.ListUI;
import panda.tool.codegen.bean.Module;
import panda.tool.codegen.bean.Resource;

import freemarker.template.Configuration;

/**
 * generate ".properties" resource file 
 */
public class TextGenerator extends AbstractCodeGenerator {
	/**
	 * Main class for PropertyResourceGenerator
	 */
	public static class Main extends AbstractCodeGenerator.Main {
		@Override
		protected void addCommandLineOptions() throws Exception {
			super.addCommandLineOptions();
			
			addCommandLineOption("l", "locale", "Resource locale (e.g: ja zh_CN)");
		}

		@Override
		protected void getCommandLineOptions(CommandLine cl) throws Exception {
			super.getCommandLineOptions(cl);
			
			if (cl.hasOption("l")) {
				setParameter("locale", cl.getOptionValue("l").trim());
			}
		}

		/**
		 * @param args arguments
		 */
		public static void main(String[] args) {
			Main cgm = new Main();
			
			AbstractCodeGenerator cg = new TextGenerator();

			cgm.execute(cg, args);
		}

	}

	//---------------------------------------------------------------------------------------
	// properties
	//---------------------------------------------------------------------------------------
	protected static final String EXT = ".txt";
	
	protected String locale;
	protected int cntModelFile = 0;
	protected int cntActionFile = 0;

	// disable unicode escape
	protected boolean escapeUnicode = false;
	protected String charset = "UTF-8";
	protected boolean sourceTime = false;
	
	/**
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Override
	protected void checkParameters() throws Exception {
		super.checkParameters();
	}

	protected void saveProperty(PrintWriter pw, String key, String value, boolean comment) {
		if (comment) {
			pw.print("#>");
		}
		
		pw.print(saveConvert(key, true, escapeUnicode));
		pw.print("=");
		pw.print(saveConvert(value, false, escapeUnicode));

		pw.print('\n');
	}

	protected void saveProperty(PrintWriter pw, String key, String value) {
		saveProperty(pw, key, value, false);
	}

	protected void saveComment(PrintWriter pw, String key, String value) {
		saveProperty(pw, key, value, true);
	}

	protected void saveGenerateInfo(PrintWriter pw) {
//		String time = (new SimpleDateFormat("yyyy-mm-dd HH:mm:ss")).format(Calendar.getInstance().getTime());
//		pw.print("# " + "This file was auto-generated by " + getClass().getName()
//				+ (sourceTime ? (" (" + time + ")") : ""));
//		pw.print("\n\n");
	}

	protected void saveComment(PrintWriter pw, String value) {
		pw.print("# " + value + '\n');
	}

	protected void saveBlockComment(PrintWriter pw, String value) {
		pw.print("#---------------------------------------------------------\n");
		pw.print("# " + value + '\n');
		pw.print("#---------------------------------------------------------\n");
	}

	/**
	 * Convert a nibble to a hex character
	 * 
	 * @param nibble the nibble to convert.
	 */
	private static char toHex(int nibble) {
		return hexDigit[(nibble & 0xF)];
	}

	/** A table of hex digits */
	private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

	/*
	 * Converts unicodes to encoded &#92;uxxxx and escapes special characters with a preceding slash
	 */
	protected String saveConvert(String theString, boolean escapeSpace, boolean escapeUnicode) {
		int len = theString.length();
		int bufLen = len * 2;
		if (bufLen < 0) {
			bufLen = Integer.MAX_VALUE;
		}
		StringBuilder outBuffer = new StringBuilder(bufLen);

		for (int x = 0; x < len; x++) {
			char aChar = theString.charAt(x);
			// Handle common case first, selecting largest block that
			// avoids the specials below
			if ((aChar > 61) && (aChar < 127)) {
				if (aChar == '\\') {
					outBuffer.append('\\');
					outBuffer.append('\\');
					continue;
				}
				outBuffer.append(aChar);
				continue;
			}
			switch (aChar) {
			case ' ':
				if (x == 0 || escapeSpace)
					outBuffer.append('\\');
				outBuffer.append(' ');
				break;
			case '\t':
				outBuffer.append('\\');
				outBuffer.append('t');
				break;
			case '\n':
				outBuffer.append('\\');
				outBuffer.append('n');
				break;
			case '\r':
				outBuffer.append('\\');
				outBuffer.append('r');
				break;
			case '\f':
				outBuffer.append('\\');
				outBuffer.append('f');
				break;
			case '=': // Fall through
			case ':': // Fall through
			case '#': // Fall through
			case '!':
				outBuffer.append('\\');
				outBuffer.append(aChar);
				break;
			default:
				if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode) {
					outBuffer.append('\\');
					outBuffer.append('u');
					outBuffer.append(toHex((aChar >> 12) & 0xF));
					outBuffer.append(toHex((aChar >> 8) & 0xF));
					outBuffer.append(toHex((aChar >> 4) & 0xF));
					outBuffer.append(toHex(aChar & 0xF));
				}
				else {
					outBuffer.append(aChar);
				}
			}
		}
		return outBuffer.toString();
	}

	@Override
	protected void loadTemplates(Configuration cfg) throws Exception {
	}
	
	@Override
	protected void prepareModule(Module module) throws Exception {
	}
	
	@Override
	protected void processModule(Module module) throws Exception {
		sourceTime = "true".equals(module.getProps().getProperty("source.datetime"));
		escapeUnicode = "true".equals(module.getProps().getProperty("resource.escapeUnicode"));
		if (escapeUnicode) {
			charset = Charsets.ISO_8859_1;
		}
		
		if (Strings.isEmpty(locale)) {
			for (Resource resource : module.getResourceList()) {
				processLocaleResource(module, resource);
			}
		}
		else {
			for (Resource resource : module.getResourceList()) {
				if (locale.equals(resource.getLocale())) {
					processLocaleResource(module, resource);
				}
			}
		}
	}
	
	protected void processLocaleResource(Module module, Resource resource) throws Exception {
		String locale = Strings.isEmpty(resource.getLocale()) ? "" : "_" + resource.getLocale();

		for (Entity entity : resource.getEntityList()) {
			print2("Processing text of entity - " + entity.getName() + locale);
			processLocaleEntity(entity, locale);
		}
		
		for (Action action : resource.getActionList()) {
			if (Strings.isEmpty(action.getActionClass()) && Strings.isEmpty(action.getName())) {
				throw new IllegalArgumentException("Missing name or actionClass: " + resource.getLocale()); 
			}
			
			if (Strings.isNotEmpty(action.getActionClass())) {
				processLocaleAction(action, locale);
				continue;
			}

			boolean gened = false;
			for (Action a : module.getActionList()) {
				if (action.getName().equals(a.getName())) {
					action.setActionClass(a.getActionClass());
					processLocaleAction(action, locale);
					gened = true;
				}
			}

			if (!gened) {
				throw new IllegalArgumentException("Missing actionClass: " + action.getName() + "/" + resource.getLocale()); 
			}
		}
	}

	@Override
	protected void postProcess() throws Exception {
		print0(cntModule + " modules processed, " + cntModelFile + " model & " + cntActionFile + " action resource files generated successfully.");
	}

	protected void processLocaleEntity(Entity entity, String locale) throws Exception {
		PrintWriter pwmbp = null;

		try {
			File fmdir = new File(out, entity.getPackage().replace('.', '/'));
			fmdir.mkdirs();
			
			File fmbp = new File(fmdir.getPath(), entity.getSimpleName() + locale + EXT);
			print3("Generating - " + fmbp.getPath());

			pwmbp = new PrintWriter(fmbp, charset);
			saveGenerateInfo(pwmbp);

			for (EntityProperty p : entity.getPropertyList()) {
				if (p.getLabel() != null) {
					saveProperty(pwmbp, p.getName(), p.getLabel());
				}
				
				if (p.getTooltip() != null) {
					saveProperty(pwmbp, p.getName() + "-tip", p.getTooltip());
				}

				pwmbp.print('\n');
			}

			pwmbp.print('\n');
			pwmbp.flush();
			
			cntModelFile++;
		}
		finally {
			Streams.safeClose(pwmbp);
		}
	}	
	
	protected void processLocaleAction(Action action, String locale) throws Exception {
		print2("Processing text of action - " + action.getActionClass() + locale);

		PrintWriter pwabp = null;
		try {
			File foutdir = new File(out, action.getActionPackage().replace('.', '/'));
			foutdir.mkdirs();

			File fabp = new File(foutdir.getPath(), action.getSimpleActionClass() + locale + EXT);
			print3("Generating - " + fabp.getPath());

			pwabp = new PrintWriter(fabp, charset);
			saveGenerateInfo(pwabp);

			if (action.getTitle() != null) {
				saveProperty(pwabp, "title", action.getTitle());
				pwabp.print('\n');
			}

			for (ActionProperty ap : action.getPropertyList()) {
				boolean write = false;
				if (Strings.isNotEmpty(ap.getLabel())) {
					saveProperty(pwabp, ap.getName(), ap.getLabel());
					write = true;
				}
				if (Strings.isNotEmpty(ap.getTooltip())) {
					saveProperty(pwabp, ap.getName() + "-tip", ap.getTooltip());
					write = true;
				}

				if (write) {
					pwabp.print('\n');
				}
			}
			
			for (ListUI listui : action.getListUIList()) {
				if (Boolean.TRUE.equals(listui.getGenerate())) {
					pwabp.print('\n');
					pwabp.print('\n');
					for (ListColumn lc : listui.getColumnList()) {
						boolean write = false;
						if (Strings.isNotEmpty(lc.getLabel())) {
							saveProperty(pwabp, 
								listui.getName() + "-column-" + lc.getName(),
								lc.getLabel());
							write = true;
						}
						if (Strings.isNotEmpty(lc.getTooltip())) {
							saveProperty(pwabp, 
								listui.getName() + "-column-" + lc.getName() + "-tip",
								lc.getTooltip());
							write = true;
						}

						if (write) {
							pwabp.print('\n');
						}
					}
				}
			}

			for (InputUI inputui : action.getInputUIList()) {
				if (Boolean.TRUE.equals(inputui.getGenerate())) {
					pwabp.print('\n');
					pwabp.print('\n');
					for (InputField ifd : inputui.getFieldList()) {
						boolean write = false;
						if (Strings.isNotEmpty(ifd.getLabel())) {
							saveProperty(pwabp, 
								inputui.getName() + "-" + ifd.getName(), 
								ifd.getLabel());
							write = true;
						}
						if (Strings.isNotEmpty(ifd.getTooltip())) {
							saveProperty(pwabp, 
								inputui.getName() + "-" + ifd.getName() + "-tip", 
								ifd.getTooltip());
							write = true;
						}

						if (write) {
							pwabp.print('\n');
						}
					}
				}
			}
			pwabp.print('\n');
			pwabp.flush();

			cntActionFile++;
		}
		finally {
			Streams.safeClose(pwabp);
		}
	}
}
