package panda.tool.codegen;

import panda.io.FileNames;
import panda.io.PropertiesEx;
import panda.io.Streams;
import panda.lang.Chars;
import panda.lang.Charsets;
import panda.lang.Classes;
import panda.lang.HandledException;
import panda.lang.Numbers;
import panda.lang.Strings;
import panda.lang.Texts;
import panda.tool.IllegalLicenseException;
import panda.tool.codegen.bean.Entity;
import panda.tool.codegen.bean.Module;
import panda.util.tool.AbstractCommandTool;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.cli.CommandLine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * Base class for code generator.
 */
public abstract class AbstractCodeGenerator {
	/**
	 * Base main class for code generator. Parse basic command line options.
	 */
	protected abstract static class Main extends AbstractCommandTool {
		@Override
		protected void addCommandLineOptions() throws Exception {
			super.addCommandLineOptions();
			
			addCommandLineOption("d", "dir", "The directory which contains configuration files.");
			addCommandLineOption("i", "includes", "The files to be include.");
			addCommandLineOption("e", "excludes", "The files to be exclude.");
			addCommandLineOption("o", "output", "Output directory. (default is current directory.)");
			addCommandLineOption("v", "verbose", "Print information level (1-3).");
		}

		@Override
		protected void getCommandLineOptions(CommandLine cl) throws Exception {
			super.getCommandLineOptions(cl);
			
			if (cl.hasOption("d")) {
				setParameter("dir", new File(cl.getOptionValue("d").trim()));
			}

			if (cl.hasOption("i")) {
				setParameter("includes", cl.getOptionValues("i"));
			}
			else {
                errorRequired(options, "includes");
			}

			if (cl.hasOption("e")) {
				setParameter("excludes", cl.getOptionValues("e"));
			}
			
			if (cl.hasOption("o")) {
				setParameter("out", new File(cl.getOptionValue("o").trim()));
			}
			
			if (cl.hasOption("v")) {
				String v = cl.getOptionValue("v").trim();
				setParameter("verbose", Numbers.toInt(v, 5));
			}
		}
	}

	//---------------------------------------------------------------------------------------
	// properties
	//---------------------------------------------------------------------------------------
	protected File dir;
	protected String[] includes;
	protected String[] excludes;

	protected File out;
	protected int verbose = 0;
	protected int cntModule = 0;
	protected int cntFile = 0;
	protected Validator validator;
	
	/**
	 * Constructor
	 */
	public AbstractCodeGenerator() {
		dir = new File(".");
		out = new File(".");
	}

	/**
	 * @param dir the dir to set
	 */
	public void setDir(File dir) {
		this.dir = dir;
	}

	/**
	 * @param includes the includes to set
	 */
	public void setIncludes(String[] includes) {
		this.includes = Strings.trimAll(includes);
	}

	/**
	 * @param includes the includes to set
	 */
	public void setIncludes(String includes) {
		this.includes = Strings.trimAll(Strings.split(includes, ','));
	}

	/**
	 * @param excludes the excludes to set
	 */
	public void setExcludes(String[] excludes) {
		this.excludes = Strings.trimAll(excludes);
	}

	/**
	 * @param excludes the excludes to set
	 */
	public void setExcludes(String excludes) {
		this.excludes = Strings.trimAll(Strings.split(excludes, ','));
	}

	/**
	 * @param outdir the outdir to set
	 */
	public void setOut(File outdir) {
		AbstractCommandTool.checkRequired(outdir, "out");
		this.out = outdir;
	}

	/**
	 * @param verbose the verbose to set
	 */
	public void setVerbose(int verbose) {
		this.verbose = verbose;
	}

	protected boolean needTranslate(String val) {
		return Strings.isNotEmpty(val) && val.indexOf("${") >= 0;
	}

	protected String translateValue(String val, Properties properties) throws Exception {
		return Texts.translate(val, properties);
	}

	protected void translateDom(Node node, Properties properties) throws Exception {
		NamedNodeMap nnm = node.getAttributes();
		for (int i = 0; nnm != null && i < nnm.getLength(); i++) {
			Node n = nnm.item(i);
			String val = n.getNodeValue();
			if (needTranslate(val)) {
				val = translateValue(val, properties);
				n.setNodeValue(val);
			}
		}
		
		NodeList nl = node.getChildNodes();
		for (int i = 0; nl != null && i < nl.getLength(); i++) {
			Node n = nl.item(i);
			translateDom(n, properties);
		}
		
		String val = node.getNodeValue();
		if (needTranslate(val)) {
			val = translateValue(val, properties);
			node.setNodeValue(val);
		}
	}
	
	protected void initValidator() throws Exception {
		InputStream is = AbstractCodeGenerator.class.getResourceAsStream("Module.xsd");
		
		if (is == null) {
			throw new RuntimeException("Failed to load resource Module.xsd");
		}
		
		// create a SchemaFactory capable of understanding WXS schemas
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// load a WXS schema, represented by a Schema instance
		Schema schema = factory.newSchema(new StreamSource(is));

		// create a Validator instance, which can be used to validate an instance document
		validator = schema.newValidator();
		
		Streams.safeClose(is);
	}

	protected void validateDom(File file, Node node) throws Exception {
		try {
			// validate the DOM tree
			validator.validate(new DOMSource(node));
		}
		catch (SAXParseException e) {
			throw new RuntimeException(file.getPath() + ": " + e.getMessage(), e);
		}
	}

	protected void includeDom(File file, Document doc, Properties properties, String extension) throws Exception {
		List<Node> incList = new ArrayList<Node>();
		
		Element el = doc.getDocumentElement();
		NodeList nl = el.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node in = nl.item(i);
			if (in.getNodeType() != Node.ELEMENT_NODE || !"include".equals(in.getNodeName())) {
				continue;
			}
			
			Node tn = in.getFirstChild();
			if (tn == null) {
				throw new Exception("<include> mush have a text node");
			}
			String inc = tn.getNodeValue();
			if (needTranslate(inc)) {
				inc = translateValue(inc, properties);
			}

			if (inc.endsWith(extension)) {
				File fi = new File(file.getParent(), inc);
				if (inc.endsWith(".properties")) {
					PropertiesEx.load(properties, fi);
				}
				else if (inc.endsWith(".xml")) {
					Document idoc = loadDocument(fi, properties);
					Node iec = doc.importNode(idoc.getDocumentElement(), true);
					for (Node fc = iec.getFirstChild(); fc != null; fc = iec.getFirstChild()) {
						el.appendChild(iec.removeChild(fc));
					}
				}
				incList.add(in);
			}
		}
	}

	protected Document loadDocument(File file, Properties properties) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		
		includeDom(file, doc, properties, ".properties");
		translateDom(doc, properties);
		validateDom(file, doc);

		includeDom(file, doc, properties, ".xml");
		
		return doc;
	}
	
	protected Module parseModule(File f) throws Exception {
		Properties properties = new Properties();
		
		properties.load(getClass().getResourceAsStream("default.properties"));

		Document doc = loadDocument(f, properties);
		
		JAXBContext context = JAXBContext.newInstance(Classes.getPackageName(Entity.class), 
			getClass().getClassLoader());
		Unmarshaller unmarshaller = context.createUnmarshaller();
 
		@SuppressWarnings("rawtypes")
		JAXBElement je = (JAXBElement)(unmarshaller.unmarshal(doc));
		Module module = (Module)je.getValue();
		
		module.setProps(properties);
		
		return module;
	}

	protected Configuration ftlConfig;

	protected void loadTemplates(Configuration cfg) throws Exception {
	}

	protected void initTemplate() throws Exception {
		ftlConfig = new Configuration();

		ftlConfig.setClassForTemplateLoading(this.getClass(), "");

		DefaultObjectWrapper ow = new DefaultObjectWrapper();

		ftlConfig.setObjectWrapper(ow);

		loadTemplates(ftlConfig);
	}

	/**
	 * execute
	 */
	public void execute() {
		try {
			cntFile = 0;
			cntModule = 0;

			checkParameters();
			
			initValidator();

			initTemplate();
	
			preProcess();
			
			scan(dir);
			
			postProcess();
		}
		catch (IllegalLicenseException e) {
			throw new IllegalLicenseException(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(e);
		}
	}

	protected void checkParameters() throws Exception {
		AbstractCommandTool.checkRequired(includes, "includes");

		for (int i = 0; i < includes.length; i++) {
			String s = includes[i];
			includes[i] = Strings.replaceChars(s, '/', File.separatorChar);
		}
	}
	
	protected boolean isExclude(File f) {
		if (dir.equals(f)) {
			return false;
		}
		if (excludes != null) {
			for (String s : excludes) {
				if (FileNames.pathMatch(FileNames.removeLeadingPath(dir, f), s)) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean isInclude(File f) {
		if (dir.equals(f)) {
			return true;
		}
		for (String s : includes) {
			if (FileNames.pathMatch(FileNames.removeLeadingPath(dir, f), s)) {
				return true;
			}
		}
		return false;
	}
	
	protected void scan(File f) throws Exception {
		if (f.isHidden()) {
			return;
		}
		else if (f.isDirectory()) {
			if (!isExclude(f)) {
				File[] sfs = f.listFiles();
				for (File sf : sfs) {
					scan(sf);
				}
			}
		}
		else if (f.isFile()) {
			if (!isExclude(f) && isInclude(f)) {
				cntFile++;
				process(f);
			}
		}
	}

	protected void preProcess() throws Exception {
		print0("Processing modules: " + dir.getPath());
	}

	protected void process(File file) throws Exception {
		print1("Processing module: " + file.getName());
		Module m = parseModule(file);
		prepareModule(m);
		processModule(m);
		cntModule++;
	}

	protected void prepareModule(Module module) throws Exception {
		module.prepare();
	}

	protected abstract void processModule(Module m) throws Exception;

	protected void postProcess() throws Exception {
		print0(cntModule + " modules processed, " + cntFile + " files generated successfully.");
	}

	protected void print0(String s) {
		if (verbose >= 0) {
			System.out.println(s);
		}
	}

	protected void print1(String s) {
		if (verbose >= 1) {
			System.out.println(s);
		}
	}

	protected void print2(String s) {
		if (verbose >= 2) {
			System.out.println(s);
		}
	}

	protected void print3(String s) {
		if (verbose >= 3) {
			System.out.println(s);
		}
	}

	protected void print4(String s) {
		if (verbose >= 4) {
			System.out.println(s);
		}
	}

	protected void print5(String s) {
		if (verbose >= 5) {
			System.out.println(s);
		}
	}
	
	private void processTpl(String pkg, String name, Template tpl, Map<String, Object> context, String charset) throws Exception {
		if (Strings.isBlank(pkg)) {
			throw new Exception("package name of [" + name + "] can not be empty");
		}

		PrintWriter pw = null;
		try {
			File dir = new File(out, pkg.replace('.', '/'));
			dir.mkdirs();

			File file = new File(dir.getPath(), name);
			print3("Generate - " + file.getPath());

			if (charset == null) {
				pw = new PrintWriter(file);
			}
			else {
				pw = new PrintWriter(file, charset);
			}

			tpl.process(context, pw);
			pw.flush();

			cntFile++;
		}
		finally {
			Streams.safeClose(pw);
		}
	}
	
	protected void processTpl(String pkg, String name, Map<String, Object> context, Template tpl) throws Exception {
		processTpl(pkg, name, context, tpl, false);
	}

	protected void processTpl(String pkg, String name, Map<String, Object> context, Template tpl, boolean serializable) throws Exception {
		context.put("package", pkg);
		context.put("name", FileNames.removeExtension(name));
		if (serializable) {
			context.put("svuid", 1);

			StringWriter sw = new StringWriter();
			tpl.process(context, sw);
			int svuid = Strings.remove(sw.toString(), Chars.CR).hashCode();
			
			context.put("svuid", svuid);
		}
		
		processTpl(pkg, name, tpl, context, Charsets.UTF_8);

		if (serializable) {
			context.remove("svuid");
		}
	}

	protected void checkLicense(Module m, String pkg) {
//		if (StringUtils.isBlank(pkg)) {
//			throw new IllegalArgumentException("package is required.");
//		}
//		
//		pkg = StringUtils.replaceChars(pkg, '/', '.');
//		String license = m.getProps().getProperty("license");
//		try {
//			license = StringCryptoUtils.decrypt(license);
//			String[] ls = StringUtils.split(license, ',');
//			if (pkg.startsWith(ls[0])) {
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//				Date ld = sdf.parse(ls[1]);
//				if (ld.after(Calendar.getInstance().getTime())) {
//					return;
//				}
//			}
//		}
//		catch (Exception e) {
//		}
//
//		throw new IllegalLicenseException("Illegal license: " + pkg);
	}

	/**
	 * add type to set
	 * @param imports import set
	 * @param type java type
	 */
	protected void addImportType(Set<String> imports, String type) {
		if (type.endsWith(Classes.ARRAY_SUFFIX)) {
			type = type.substring(0, type.length() - Classes.ARRAY_SUFFIX.length());
		}
		
		int lt = type.indexOf('<');
		int gt = type.lastIndexOf('>');
		
		if (lt > 0 && gt > 0 && gt > lt) {
			addImportType(imports, type.substring(0, lt));
			type = type.substring(lt + 1, gt);
			String[] ts = Strings.split(type, ", ");
			for (String t : ts) {
				addImportType(imports, t);
			}
		}
		else {
			if (type.indexOf(".") > 0 && !type.startsWith("java.lang.")) {
				imports.add(type);
			}
		}
	}
}