package panda.app.util;

import java.util.List;

import panda.app.constant.MVC;
import panda.app.constant.VAL;
import panda.app.entity.Property;
import panda.app.entity.Resource;
import panda.app.entity.query.PropertyQuery;
import panda.app.entity.query.ResourceQuery;
import panda.dao.Dao;
import panda.dao.DaoClient;
import panda.io.resource.BeanResourceMaker;
import panda.io.resource.ResourceLoader;
import panda.ioc.annotation.IocBean;
import panda.ioc.annotation.IocInject;
import panda.log.Log;
import panda.log.Logs;


/**
 * A class for load database resource.
 */
@IocBean(type=ResourceLoader.class, create="initialize")
public class AppResourceBundleLoader extends ResourceLoader {
	private static final Log log = Logs.getLog(AppResourceBundleLoader.class);

	@IocInject(value=MVC.DATABASE_RESOURCE, required=false)
	private boolean resource;

	@IocInject(value=MVC.DATABASE_PROPERTY, required=false)
	private boolean property;

	@IocInject
	protected DaoClient daoClient;

	protected BeanResourceMaker databaseResourceLoader;
	
	/**
	 * @return the databaseResourceLoader
	 */
	public BeanResourceMaker getDatabaseResourceLoader() {
		return databaseResourceLoader;
	}

	/**
	 * initial load external resources
	 * @throws Exception if an error occurs
	 */
	public void initialize() throws Exception {
		if (resource) {
			BeanResourceMaker mrbm = new BeanResourceMaker();

			mrbm.setClassColumn(Resource.CLAZZ);
			mrbm.setLanguageColumn(Resource.LANGUAGE);
			mrbm.setCountryColumn(Resource.COUNTRY);
			//databaseResourceLoader.setVariantColumn("variant");
			mrbm.setSourceColumn(Resource.SOURCE);
			mrbm.setPackageName(getClass().getPackage().getName());

			databaseResourceLoader = mrbm;
			addResourceMaker(mrbm);
		}
		else if (property) {
			BeanResourceMaker mrbm = new BeanResourceMaker();

			mrbm.setClassColumn(Property.CLAZZ);
			mrbm.setLanguageColumn(Property.LANGUAGE);
			mrbm.setCountryColumn(Property.COUNTRY);
			//databaseResourceLoader.setVariantColumn("variant");
			mrbm.setNameColumn(Property.NAME);
			mrbm.setValueColumn(Property.VALUE);
			mrbm.setPackageName(getClass().getPackage().getName());

			databaseResourceLoader = mrbm;
			addResourceMaker(mrbm);
		}
		
		reload();
	}
	
	/**
	 * reload external resources
	 * @throws Exception if an error occurs
	 */
	public void reload() throws Exception {
		if (databaseResourceLoader == null) {
			return;
		}
		
		if (resource) {
			log.info("Loading database resources ...");

			Dao dao = daoClient.getDao();
			ResourceQuery rq = new ResourceQuery();
			rq.status().equalTo(VAL.STATUS_ACTIVE);
			List<Resource> list = dao.select(rq);
			databaseResourceLoader.loadResources(list);
		}
		else if (property) {
			log.info("Loading database properties ...");

			Dao dao = daoClient.getDao();
			PropertyQuery pq = new PropertyQuery();
			pq.status().equalTo(VAL.STATUS_ACTIVE);
			List<Property> list = dao.select(pq);
			databaseResourceLoader.loadResources(list);
		}
	}
}
