package panda.wing.action.template;

import java.util.List;
import java.util.Locale;

import panda.dao.query.GenericQuery;
import panda.lang.Locales;
import panda.lang.Strings;
import panda.wing.constant.AC;
import panda.wing.constant.VC;
import panda.wing.entity.Template;
import panda.wing.entity.query.TemplateQuery;

public class TemplateExAction extends TemplateAction {
	/**
	 * Constructor
	 */
	public TemplateExAction() {
	}

	@Override
	protected void queryList(final GenericQuery<Template> q) {
		TemplateQuery tq = new TemplateQuery(q);
		tq.source().exclude();
		super.queryList(q);
	}

	/**
	 * isValidLocale
	 * @param language lanaguage
	 * @param country country
	 * @return true - if locale is valid
	 */
	public boolean isValidLocale(String language, String country) {
		if (Strings.isNotEmpty(language) && Strings.isNotEmpty(country)) {
			if (!VC.LOCALE_ALL.equals(country)) {
				return Locales.isAvailableLocale(new Locale(language, country));
			}
		}
		return true;
	}
	
	private void addReloadMessage() {
		addApplicationMessage(AC.TEMPLATE_RELOAD);
	}

	@Override
	protected void insertData(Template data) {
		super.insertData(data);

//		Application.getDatabaseTemplateLoader().putTemplate(data.getName(), data.getLanguage(),
//			data.getCountry(), null, data.getSource(), data.getUtime().getTime());

		addReloadMessage();
	}

	@Override
	protected int updateData(Template data, Template srcData) {
		int cnt = super.updateData(data, srcData);

		if (cnt > 0) {
	//		Application.getDatabaseTemplateLoader().putTemplate(data.getName(), data.getLanguage(),
	//			data.getCountry(), null, data.getSource(), data.getUtime().getTime());
			addReloadMessage();
		}
		
		return cnt;
	}

	@Override
	protected void deleteData(Template data) {
		super.deleteData(data);

//		Application.getDatabaseTemplateLoader().putTemplate(data.getName(), data.getLanguage(),
//			data.getCountry(), null, null);
		addReloadMessage();
	}

	@Override
	protected int deleteDataList(List<Template> dataList) {
		int cnt = super.deleteDataList(dataList);

		if (cnt > 0) {
	//		for (Template data : dataList) {
	//			Application.getDatabaseTemplateLoader().putTemplate(data.getName(), data.getLanguage(),
	//				data.getCountry(), null, null);
	//		}
			addReloadMessage();
		}
		return cnt;
	}
}