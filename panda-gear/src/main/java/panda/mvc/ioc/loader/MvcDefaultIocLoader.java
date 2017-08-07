package panda.mvc.ioc.loader;

import java.util.LinkedHashSet;
import java.util.Set;

import panda.lang.Collections;
import panda.mvc.MvcConfig;
import panda.mvc.adaptor.DefaultParamAdaptor;
import panda.mvc.adaptor.ejector.FormParamEjector;
import panda.mvc.adaptor.ejector.JsonParamEjector;
import panda.mvc.adaptor.ejector.MultiPartParamEjector;
import panda.mvc.adaptor.ejector.StreamParamEjector;
import panda.mvc.adaptor.ejector.XmlParamEjector;
import panda.mvc.alert.ActionAlertSupport;
import panda.mvc.alert.ParamAlertSupport;
import panda.mvc.annotation.Modules;
import panda.mvc.filepool.MvcLocalFilePool;
import panda.mvc.impl.DefaultActionChainMaker;
import panda.mvc.impl.RegexActionMapping;
import panda.mvc.processor.AdaptProcessor;
import panda.mvc.processor.FatalProcessor;
import panda.mvc.processor.InvokeProcessor;
import panda.mvc.processor.LayoutProcessor;
import panda.mvc.processor.LocaleProcessor;
import panda.mvc.processor.PrepareProcessor;
import panda.mvc.processor.RedirectProcessor;
import panda.mvc.processor.ValidateProcessor;
import panda.mvc.processor.ViewProcessor;
import panda.mvc.util.ActionAssist;
import panda.mvc.util.ActionConsts;
import panda.mvc.util.CookieStateProvider;
import panda.mvc.util.ActionTextProvider;
import panda.mvc.util.MvcResourceLoader;
import panda.mvc.util.MvcSettings;
import panda.mvc.util.MvcCryptor;
import panda.mvc.util.MvcURLBuilder;
import panda.mvc.validation.DefaultValidators;
import panda.mvc.validation.validator.BinaryValidator;
import panda.mvc.validation.validator.CIDRValidator;
import panda.mvc.validation.validator.CastErrorValidator;
import panda.mvc.validation.validator.ConstantValidator;
import panda.mvc.validation.validator.CreditCardNoValidator;
import panda.mvc.validation.validator.DateValidator;
import panda.mvc.validation.validator.DecimalValidator;
import panda.mvc.validation.validator.ElValidator;
import panda.mvc.validation.validator.EmailValidator;
import panda.mvc.validation.validator.EmptyValidator;
import panda.mvc.validation.validator.FileValidator;
import panda.mvc.validation.validator.FilenameValidator;
import panda.mvc.validation.validator.ImageValidator;
import panda.mvc.validation.validator.ImailValidator;
import panda.mvc.validation.validator.NumberValidator;
import panda.mvc.validation.validator.ProhibitedValidator;
import panda.mvc.validation.validator.RegexValidator;
import panda.mvc.validation.validator.RequiredValidator;
import panda.mvc.validation.validator.StringValidator;
import panda.mvc.validation.validator.URLValidator;
import panda.mvc.validation.validator.VisitValidator;
import panda.mvc.view.ftl.FreemarkerHelper;
import panda.mvc.view.ftl.FreemarkerManager;
import panda.mvc.view.ftl.FreemarkerTemplateLoader;
import panda.mvc.view.sitemesh.FreemarkerSitemesher;
import panda.mvc.view.sitemesh.SitemeshManager;
import panda.mvc.view.tag.CBoolean;
import panda.mvc.view.tag.CDate;
import panda.mvc.view.tag.CLog;
import panda.mvc.view.tag.CNumber;
import panda.mvc.view.tag.CSet;
import panda.mvc.view.tag.CUrl;
import panda.mvc.view.tag.Csv;
import panda.mvc.view.tag.Head;
import panda.mvc.view.tag.Param;
import panda.mvc.view.tag.Property;
import panda.mvc.view.tag.Text;
import panda.mvc.view.tag.Tsv;
import panda.mvc.view.tag.ui.ActionConfirm;
import panda.mvc.view.tag.ui.ActionError;
import panda.mvc.view.tag.ui.ActionMessage;
import panda.mvc.view.tag.ui.ActionWarning;
import panda.mvc.view.tag.ui.Anchor;
import panda.mvc.view.tag.ui.Button;
import panda.mvc.view.tag.ui.Checkbox;
import panda.mvc.view.tag.ui.CheckboxList;
import panda.mvc.view.tag.ui.DatePicker;
import panda.mvc.view.tag.ui.DateTimePicker;
import panda.mvc.view.tag.ui.Div;
import panda.mvc.view.tag.ui.FieldError;
import panda.mvc.view.tag.ui.File;
import panda.mvc.view.tag.ui.Form;
import panda.mvc.view.tag.ui.Hidden;
import panda.mvc.view.tag.ui.Icon;
import panda.mvc.view.tag.ui.Link;
import panda.mvc.view.tag.ui.ListView;
import panda.mvc.view.tag.ui.OptGroup;
import panda.mvc.view.tag.ui.Pager;
import panda.mvc.view.tag.ui.Password;
import panda.mvc.view.tag.ui.Queryer;
import panda.mvc.view.tag.ui.Radio;
import panda.mvc.view.tag.ui.Reset;
import panda.mvc.view.tag.ui.Select;
import panda.mvc.view.tag.ui.Submit;
import panda.mvc.view.tag.ui.TextArea;
import panda.mvc.view.tag.ui.TextField;
import panda.mvc.view.tag.ui.TimePicker;
import panda.mvc.view.tag.ui.TriggerField;
import panda.mvc.view.tag.ui.Uploader;
import panda.mvc.view.tag.ui.ViewField;
import panda.mvc.view.tag.ui.theme.ThemeRenderEngine;
import panda.mvc.view.taglib.TagLibraryManager;
import panda.mvc.view.util.CsvExporter;
import panda.mvc.view.util.XlsExporter;
import panda.mvc.view.util.XlsxExporter;

public class MvcDefaultIocLoader extends MvcAnnotationIocLoader {
	protected void addModules(MvcConfig config, Set<Object> pkgs) {
		Class<?> mm = config.getMainModule();
		pkgs.add(mm);
		
		Modules ms = mm.getAnnotation(Modules.class);
		if (ms != null) {
			if (ms.scan()) {
				pkgs.remove(mm);
				pkgs.add(mm.getPackage().getName());
			}
			
			for (String pkg : ms.packages()) {
				pkgs.add(pkg);
			}
			
			for (Class<?> cls : ms.value()) {
				pkgs.add(cls);
			}
		}
	}
	
	protected Set<Object> getDefaults(MvcConfig config) {
		Set<Object> ss = new LinkedHashSet<Object>();
		
		addDefaults(ss);
		addModules(config, ss);
		return ss;
	}
	
	@SuppressWarnings("unchecked")
	protected void addDefaults(Set<Object> ss) {
		Collections.addAll(ss,
			RegexActionMapping.class,
			DefaultActionChainMaker.class,
			
			// file pool used by Upload
			MvcLocalFilePool.class,
			
			// utility
			ActionAssist.class,
			ActionConsts.class,
			CookieStateProvider.class,
			ActionTextProvider.class,
			MvcResourceLoader.class,
			MvcSettings.class,
			MvcCryptor.class,
			MvcURLBuilder.class,
	
			// processor
			AdaptProcessor.class,
			FatalProcessor.class,
			InvokeProcessor.class,
			LayoutProcessor.class,
			LocaleProcessor.class,
			PrepareProcessor.class,
			RedirectProcessor.class,
			ValidateProcessor.class,
			ViewProcessor.class,

			// adaptor
			DefaultParamAdaptor.class,
			
			// ejectors
			FormParamEjector.class,
			JsonParamEjector.class,
			MultiPartParamEjector.class,
			StreamParamEjector.class,
			XmlParamEjector.class,
			
			// alerts
			ActionAlertSupport.class,
			ParamAlertSupport.class,
			
			// validator
			DefaultValidators.class,
			
			// validators
			BinaryValidator.class,
			CIDRValidator.class,
			CastErrorValidator.class,
			ConstantValidator.class,
			CreditCardNoValidator.class,
			DateValidator.class,
			DecimalValidator.class,
			ElValidator.class,
			EmailValidator.class,
			EmptyValidator.class,
			FilenameValidator.class,
			FileValidator.class,
			ImageValidator.class,
			ImailValidator.class,
			NumberValidator.class,
			ProhibitedValidator.class,
			RegexValidator.class,
			RequiredValidator.class,
			StringValidator.class,
			URLValidator.class,
			VisitValidator.class,
			
			// View
			ThemeRenderEngine.class,

			// View Utils
			CsvExporter.class,
			XlsExporter.class,
			XlsxExporter.class,
			
			// Taglibs
			TagLibraryManager.class,
	
			// Tags
			CBoolean.class,
			CDate.class,
			CLog.class,
			CNumber.class,
			CSet.class,
			Csv.class,
			Tsv.class,
			CUrl.class,
			Head.class,
			Param.class,
			Property.class,
			Text.class,
			
			// UI Tags
			ActionConfirm.class,
			ActionError.class,
			ActionMessage.class,
			ActionWarning.class,
			Anchor.class,
			Button.class,
			Checkbox.class,
			CheckboxList.class,
			DatePicker.class,
			DateTimePicker.class,
			Div.class,
			FieldError.class,
			File.class,
			Form.class,
			Hidden.class,
			Icon.class,
			Link.class,
			ListView.class,
			OptGroup.class,
			Pager.class,
			Password.class,
			Queryer.class,
			Radio.class,
			Reset.class,
			Select.class,
			Submit.class,
			TextArea.class,
			TextField.class,
			TimePicker.class,
			TriggerField.class,
			Uploader.class,
			ViewField.class,
			
			// Freemarker
			FreemarkerTemplateLoader.class,
			FreemarkerManager.class,
			FreemarkerHelper.class,
			
			// Sitemesh
			SitemeshManager.class,
			FreemarkerSitemesher.class
		);
	};
	
	public MvcDefaultIocLoader(MvcConfig config) {
		super();
		init(getDefaults(config));
	}
}
