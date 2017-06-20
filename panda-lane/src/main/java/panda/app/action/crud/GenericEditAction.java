package panda.app.action.crud;

import java.util.Collection;
import java.util.Set;

import panda.app.BusinessRuntimeException;
import panda.app.constant.RES;
import panda.app.entity.Bean;
import panda.app.entity.IUpdate;
import panda.dao.entity.EntityFKey;
import panda.dao.entity.EntityField;
import panda.dao.entity.EntityHelper;
import panda.dao.entity.EntityIndex;
import panda.dao.query.GenericQuery;
import panda.io.Streams;
import panda.lang.Asserts;
import panda.lang.Classes;
import panda.lang.Collections;
import panda.lang.time.DateTimes;
import panda.log.Log;
import panda.log.Logs;
import panda.mvc.Mvcs;


/**
 * @param <T> data type
 */
public abstract class GenericEditAction<T> extends GenericBaseAction<T> {
	private static final Log log = Logs.getLog(GenericEditAction.class);
	
	/**
	 * RESULT_CONFIRM = "confirm";
	 */
	protected final static String RESULT_CONFIRM = "confirm";
	
	//------------------------------------------------------------
	// config properties
	//------------------------------------------------------------
	private boolean checkAbortOnError = false;

	/**
	 * Constructor 
	 */
	public GenericEditAction() {
	}

	//------------------------------------------------------------
	// protected getter & setter
	//------------------------------------------------------------
	/**
	 * @return the checkAbortOnError
	 */
	protected boolean isCheckAbortOnError() {
		return checkAbortOnError;
	}

	/**
	 * @param checkAbortOnError the checkAbortOnError to set
	 */
	protected void setCheckAbortOnError(boolean checkAbortOnError) {
		this.checkAbortOnError = checkAbortOnError;
	}

	//------------------------------------------------------------
	// setting
	//------------------------------------------------------------
	public boolean isInputConfirm() {
		return getTextAsBoolean(RES.UI_INPUT_CONFIRM, false);
	}
	
	//------------------------------------------------------------
	// result
	//------------------------------------------------------------
	/**
	 * set result on ?_execute check error occurs  
	 */
	protected void setResultOnExecCheckError() {
		if (hasActionErrors() || hasFieldErrors() || !isInputConfirm()) {
			setScenarioView();
		}
		else {
			setScenarioView(RESULT_CONFIRM);
		}
	}

	//------------------------------------------------------------
	// end point methods
	//------------------------------------------------------------
	/**
	 * view
	 */
	protected Object view(T key) {
		return doViewSelect(key);
	}
	
	/**
	 * view_input
	 */
	protected Object view_input(T data) {
		return doViewInput(data);
	}

	/**
	 * print
	 */
	protected Object print(T key) {
		return doViewSelect(key);
	}

	/**
	 * print_input
	 */
	protected Object print_input(T data) {
		return doViewInput(data);
	}

	/**
	 * copy
	 */
	protected Object copy(T key) {
		return doCopySelect(key);
	}

	/**
	 * copy_input
	 */
	protected Object copy_input(T data) {
		return doCopyInput(data);
	}

	/**
	 * copy_confirm
	 */
	protected Object copy_confirm(T data) {
		return doInsertConfirm(data);
	}

	/**
	 * copy_execute
	 */
	public Object copy_execute(T data) {
		return doInsertExecute(data);
	}

	/**
	 * add
	 */
	protected Object add() {
		return doInsertInit();
	}

	/**
	 * add_input
	 */
	protected Object add_input(T data) {
		return doInsertInput(data);
	}

	/**
	 * add_confirm
	 */
	protected Object add_confirm(T data) {
		return doInsertConfirm(data);
	}

	/**
	 * add_execute
	 */
	protected Object add_execute(T data) {
		return doInsertExecute(data);
	}

	/**
	 * edit
	 */
	protected Object edit(T key) {
		return doUpdateSelect(key);
	}

	/**
	 * edit_input
	 */
	protected Object edit_input(T data) {
		return doUpdateInput(data);
	}

	/**
	 * edit_confirm
	 */
	public Object edit_confirm(T data) {
		return doUpdateConfirm(data);
	}

	/**
	 * edit_execute
	 */
	protected Object edit_execute(T data) {
		return doUpdateExecute(data);
	}

	/**
	 * delete
	 */
	protected Object delete(T key) {
		return doDeleteSelect(key);
	}

	/**
	 * delete_execute
	 */
	protected Object delete_execute(T key) {
		return doDeleteExecute(key);
	}

	//------------------------------------------------------------
	// do method
	//------------------------------------------------------------
	/**
	 * doViewInput 
	 */
	protected Object doViewInput(T data) {
		return prepareData(data);
	}

	/**
	 * doViewSelect
	 */
	protected Object doViewSelect(T key) {
		T pk = prepareKey(key);
		T sd = selectData(pk);
		return sd;
	}

	/**
	 * doCopySelect
	 */
	protected Object doCopySelect(T key) {
		T pk = prepareKey(key);
		T sd = selectData(pk);
		if (sd != null) {
			clearOnCopy(sd);
			clearOnCopy(key);
		}
		return sd;
	}

	/**
	 * doCopyInput
	 */
	protected Object doCopyInput(T data) {
		return prepareData(data);
	}

	/**
	 * doInsertInit
	 */
	protected Object doInsertInit() {
		T pd = prepareData(null);
		getContext().setParams(pd);
		return pd;
	}

	/**
	 * doInsertInput
	 */
	protected Object doInsertInput(T data) {
		return prepareData(data);
	}

	/**
	 * doInsertConfirm
	 */
	protected Object doInsertConfirm(T data) {
		T pd = prepareData(data);
		if (checkOnInsert(pd)) {
			addActionConfirm(getScenarioMessage(RES.ACTION_CONFIRM_PREFIX));
		}
		else {
			if (hasActionErrors() || hasFieldErrors()) {
				setScenarioView();
			}
		}
		return pd;
	}

	/**
	 * doInsertExecute
	 */
	protected Object doInsertExecute(T data) {
		final T pd = prepareData(data);
		if (!checkOnInsert(pd)) {
			setResultOnExecCheckError();
			return pd;
		}

		try {
			final T id = startInsert(pd);
			getDao().exec(new Runnable() {
				public void run() {
					insertData(id);
					EntityHelper.copyIdentityValue(getEntity(), id, pd);
				}
			});
			afterInsert(pd);
		}
		catch (Throwable e) {
			if (e instanceof BusinessRuntimeException) {
				log.warn(e.getMessage(), e);
			}
			else {
				log.error(e.getMessage(), e);
			}
			addSystemError(RES.ACTION_FAILED_PREFIX, e);
			setScenarioView();
			return data;
		}
		finally {
			finalInsert(pd);
		}

		addActionMessage(getScenarioMessage(RES.ACTION_SUCCESS_PREFIX));
		return pd;
	}
	
	/**
	 * doUpdateInput
	 */
	protected Object doUpdateInput(T data) {
		return prepareData(data);
	}

	/**
	 * doUpdateSelect
	 */
	protected Object doUpdateSelect(T key) {
		T pk = prepareKey(key);
		return selectData(pk);
	}

	/**
	 * doUpdateConfirm
	 */
	protected Object doUpdateConfirm(T data) {
		T pd = prepareData(data);
		T sd = selectData(pd);
		if (sd == null) {
			return null;
		}
		
		if (checkOnUpdate(pd, sd)) {
			addActionConfirm(getScenarioMessage(RES.ACTION_CONFIRM_PREFIX));
		}
		else {
			if (hasActionErrors() || hasFieldErrors()) {
				setScenarioView();
			}
		}
		return pd;
	}
	
	/**
	 * doUpdateExecute
	 */
	protected Object doUpdateExecute(T data) {
		final T pd = prepareData(data);
		final T sd = selectData(pd);
		if (sd == null) {
			return null;
		}

		if (!checkOnUpdate(pd, sd)) {
			setResultOnExecCheckError();
			return data;
		}

		try {
			final T ud = startUpdate(pd, sd);
			getDao().exec(new Runnable() {
				public void run() {
					updateData(ud, sd);
				}
			});
			afterUpdate(pd, sd);
		}
		catch (Throwable e) {
			if (e instanceof BusinessRuntimeException) {
				log.warn(e.getMessage(), e);
			}
			else {
				log.error(e.getMessage(), e);
			}
			addSystemError(RES.ACTION_FAILED_PREFIX, e);
			setScenarioView();
			return pd;
		}
		finally {
			finalUpdate(pd, sd);
		}

		addActionMessage(getScenarioMessage(RES.ACTION_SUCCESS_PREFIX));
		return pd;
	}

	/**
	 * doDeleteSelect
	 */
	protected Object doDeleteSelect(T key) {
		final T pk = prepareKey(key);
		final T sd = selectData(pk);
		if (sd == null) {
			return null;
		}
		
		if (checkOnDelete(pk, sd)) {
			addActionConfirm(getScenarioMessage(RES.ACTION_CONFIRM_PREFIX));
		}
		return sd;
	}

	/**
	 * doDeleteExecute
	 */
	protected Object doDeleteExecute(final T key) {
		final T pk = prepareKey(key);
		final T sd = selectData(pk);
		if (sd == null) {
			return null;
		}

		if (!checkOnDelete(pk, sd)) {
			return sd;
		}
		
		try {
			startDelete(sd);
			getDao().exec(new Runnable() {
				public void run() {
					deleteData(sd);
				}
			});
			afterDelete(sd);
		}
		catch (Throwable e) {
			if (e instanceof BusinessRuntimeException) {
				log.warn(e.getMessage(), e);
			}
			else {
				log.error(e.getMessage(), e);
			}
			addSystemError(RES.ACTION_FAILED_PREFIX, e);
			setScenarioView();
			return sd;
		}
		finally {
			finalDelete(sd);
		}
		
		addActionMessage(getScenarioMessage(RES.ACTION_SUCCESS_PREFIX));
		return sd;
	}

	//------------------------------------------------------------
	// data methods
	//------------------------------------------------------------
	/**
	 * trim data
	 * @param data data object
	 */
	protected T trimData(T data) {
		return data;
	}
	
	/**
	 * prepare key for select data from store
	 * @param key key
	 * @return key
	 */
	protected T prepareKey(T key) {
		return key;
	}
	
	/**
	 * prepare default data
	 * @param data data
	 * @return data
	 */
	protected T prepareData(T data) {
		if (data == null) {
			data = Classes.born(type);
		}
		return data;
	}

	//------------------------------------------------------------
	// select methods
	//------------------------------------------------------------
	/**
	 * selectData
	 * @param key key
	 * @return data data found
	 */
	protected T selectData(T key) {
		if (!EntityHelper.hasPrimaryKeyValues(getEntity(), key)) {
			addActionError(getMessage(RES.ERROR_DATA_NOTFOUND));
			return null;
		}
		
		GenericQuery<T> gq = new GenericQuery<T>(getEntity());
		addQueryFields(gq);
		addQueryJoins(gq);
		addQueryFilters(gq, key);
		
		T d = getDao().fetch(gq);
		if (d == null) {
			addActionError(getMessage(RES.ERROR_DATA_NOTFOUND));
			return null;
		}

		d = trimData(d);
		if (d == null) {
			addActionError(getMessage(RES.ERROR_DATA_NOTFOUND));
			return null;
		}
		return d;
	}

	protected void addQueryFields(GenericQuery<T> gq) {
		Collection<String> ufs = getDisplayFields();
		if (Collections.isNotEmpty(ufs)) {
			gq.excludeAll();
			gq.includePrimayKeys();
			gq.include(ufs);
		}
	}

	protected void addQueryJoins(GenericQuery<T> gq) {
	}
	
	protected void addQueryFilters(GenericQuery<T> gq, T key) {
		gq.equalToPrimaryKeys(key);
	}

	/**
	 * checkCommon
	 * @param data data
	 * @param sd source data (null on insert)
	 * @return true if do something success
	 */
	protected boolean checkCommon(T data, T sd) {
		Asserts.notNull(data, "data is null");
		return true;
	}

	/**
	 * checkOnInput (Insert & Update)
	 * @param data data
	 * @param sd source data (null on insert)
	 * @return true if do something success
	 */
	protected boolean checkOnInput(T data, T sd) {
		Asserts.notNull(data, "data is null");
		return true;
	}

	//------------------------------------------------------------
	// insert methods
	//------------------------------------------------------------
	/**
	 * checkOnInsert
	 * @param data data
	 * @return true if check success
	 */
	protected boolean checkOnInsert(T data) {
		boolean c = true;

		// !IMPORTANT: do not change check order
		if (!checkCommon(data, null)) {
			c = false;
			if (checkAbortOnError) {
				return false;
			}
		}
		
		if (!checkOnInput(data, null)) {
			c = false;
			if (checkAbortOnError) {
				return false;
			}
		}
		
		if (!checkPrimaryKeysOnInsert(data)) {
			c = false;
			if (checkAbortOnError) {
				return false;
			}
		}
		if (!checkUniqueKeysOnInsert(data)) {
			c = false;
			if (checkAbortOnError) {
				return false;
			}
		}
		if (!checkForeignKeys(data)) {
			c = false;
			if (checkAbortOnError) {
				return false;
			}
		}
		return c;
	}

	/**
	 * startInsert
	 * @param data data
	 */
	protected T startInsert(T data) {
		assist().initCommonFields(data);
		return data;
	}

	/**
	 * insert data
	 * @param data data
	 */
	protected void insertData(T data) {
		getDao().insert(data);
	}

	/**
	 * afterInsert
	 * @param data data
	 */
	protected void afterInsert(T data) {
	}

	/**
	 * finalInsert
	 * @param data data
	 */
	protected void finalInsert(T data) {
	}

	//------------------------------------------------------------
	// update methods
	//------------------------------------------------------------
	/**
	 * @return the update fields
	 */
	protected Set<String> getUpdateFields(T data, T sd) {
		return getDisplayFields();
	}

	/**
	 * checkOnUpdate
	 * @param data data
	 * @param sd source data
	 * @return true if check success
	 */
	protected boolean checkOnUpdate(T data, T sd) {
		boolean c = true;

		// !IMPORTANT: do not change check order
		if (!checkCommon(data, sd)) {
			c = false;
			if (checkAbortOnError) {
				return false;
			}
		}
		
		if (!checkOnInput(data, sd)) {
			c = false;
			if (checkAbortOnError) {
				return false;
			}
		}
		
		if (!checkDataChangedOnUpdate(data, sd)) {
			c = false;
			if (checkAbortOnError) {
				return false;
			}
		}
		// primary key can not be modified or null
		if (!checkPrimaryKeysOnUpdate(data, sd)) {
			c = false;
			if (checkAbortOnError) {
				return false;
			}
		}
		if (!checkUniqueKeysOnUpdate(data, sd)) {
			c = false;
			if (checkAbortOnError) {
				return false;
			}
		}
		if (!checkForeignKeys(data)) {
			c = false;
			if (checkAbortOnError) {
				return false;
			}
		}
		return c;
	}

	/**
	 * startUpdate
	 * @param data input data
	 * @param sd source data
	 */
	protected T startUpdate(T data, T sd) {
		assist().initUpdateFields(data, sd);
		return data;
	}

	/**
	 * update data
	 * @param ud update data
	 * @param sd source data
	 * @return update count
	 */
	protected int updateData(T ud, T sd) {
		int cnt = getDao().update(ud, getUpdateFields(ud, sd));
		if (cnt != 1) {
			throw new RuntimeException("The update data count (" + cnt + ") does not equals 1.");
		}
		return cnt;
	}

	/**
	 * afterUpdate
	 * @param data data
	 * @param sd source data
	 */
	protected void afterUpdate(T data, T sd) {
	}

	/**
	 * finalUpdate
	 * @param data data
	 * @param sd source data
	 */
	protected void finalUpdate(T data, T sd) {
	}

	//------------------------------------------------------------
	// delete methods
	//------------------------------------------------------------
	/**
	 * checkOnDelete
	 * @param data data
	 * @param sd source data
	 * @return true if check success
	 */
	protected boolean checkOnDelete(T data, T sd) {
		boolean c = true;
		
		if (!checkCommon(data, sd)) {
			c = false;
			if (checkAbortOnError) {
				return false;
			}
		}
		
		if (!checkDataChangedOnDelete(data, sd)) {
			c = false;
			if (checkAbortOnError) {
				return false;
			}
		}

		return c;
	}

	/**
	 * startDelete(T)
	 * @param data data
	 */
	protected void startDelete(T data) {
	}

	/**
	 * delete data
	 * @param data data
	 */
	protected void deleteData(T data) {
		int cnt = getDao().delete(data);
		if (cnt != 1) {
			throw new RuntimeException("The deleted data count (" + cnt + ") does not equals 1.");
		}
	}

	/**
	 * afterDelete
	 * @param data data
	 */
	protected void afterDelete(T data) {
	}

	/**
	 * finalDelete
	 * @param data data
	 */
	protected void finalDelete(T data) {
	}

	//------------------------------------------------------------
	// error message methods
	//------------------------------------------------------------
	protected void addDataFieldErrors(T data, Collection<EntityField> efs, String itemErrMsg, String dataErrMsg) {
		StringBuilder sb = new StringBuilder();
		for (EntityField ef : efs) {
			EntityField eff = mappedEntityField(ef);
			if (!displayField(eff.getName())) {
				continue;
			}

			addFieldError(eff.getName(), getMessage(itemErrMsg));

			sb.append(Streams.LINE_SEPARATOR);

			String label = getFieldLabel(eff.getName());
			sb.append(label);
			sb.append(": ");
			
			Object fv = eff.getValue(data);
			if (fv != null) {
				sb.append(Mvcs.castString(context, fv));
			}
		}

		addActionError(getMessage(dataErrMsg, sb.toString()));
	}
	
	protected void addDataDuplicateError(T data, Collection<EntityField> efs) {
		addDataFieldErrors(data, efs, RES.ERROR_ITEM_DUPLICATE, RES.ERROR_DATA_DUPLICATE);
	}

	protected void addDataIncorrectError(T data, Collection<EntityField> efs) {
		addDataFieldErrors(data, efs, RES.ERROR_ITEM_INCORRECT, RES.ERROR_DATA_INCORRECT);
	}
	
	//------------------------------------------------------------
	// check methods
	//------------------------------------------------------------
	/**
	 * checkPrimaryKeyOnInsert
	 * @param data data
	 * @return true if check successfully
	 */
	protected boolean checkPrimaryKeysOnInsert(T data) {
		if (EntityHelper.checkPrimaryKeys(getDao(), getEntity(), data)) {
			return true;
		}

		addDataDuplicateError(data, getEntity().getPrimaryKeys());
		return false;
	}

	/**
	 * @param data data
	 * @param sd source data
	 * @return true if check successfully
	 */
	protected boolean checkPrimaryKeysOnUpdate(T data, T sd) {
		if (EntityHelper.hasPrimaryKeyValues(getEntity(), data)) {
			return true;
		}

		addActionError(getMessage(RES.ERROR_DATA_NOTFOUND));
		return false;
	}

	protected boolean checkUniqueIndex(T data, EntityIndex ei) {
		if (EntityHelper.checkUniqueIndex(getDao(), getEntity(), data, ei)) {
			return true;
		}

		addDataDuplicateError(data, ei.getFields());
		return false;
	}

	/**
	 * checkUniqueKeysOnInsert
	 * @return true if check successfully
	 */
	protected boolean checkUniqueKeysOnInsert(T data) {
		Collection<EntityIndex> eis = getEntity().getIndexes();
		if (Collections.isEmpty(eis)) {
			return true;
		}
		
		for (EntityIndex ei : eis) {
			if (!checkUniqueIndex(data, ei)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * checkUniqueKeysOnUpdate
	 * @param data data
	 * @param sd source data
	 * @return true if check successfully
	 */
	protected boolean checkUniqueKeysOnUpdate(T data, T sd) {
		Collection<EntityIndex> eis = getEntity().getIndexes();
		if (Collections.isEmpty(eis)) {
			return true;
		}
		
		for (EntityIndex ei : eis) {
			if (!ei.isUnique()) {
				continue;
			}

			if (EntityHelper.isDifferent(ei.getFields(), data, sd)) {
				if (!checkUniqueIndex(data, ei)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * checkForeignKeys
	 * @param data
	 * @return true if check successfully
	 */
	protected boolean checkForeignKeys(T data) {
		Collection<EntityFKey> efks = getEntity().getForeignKeys();
		if (Collections.isEmpty(efks)) {
			return true;
		}
		
		for (EntityFKey efk : efks) {
			if (!EntityHelper.checkForeignKey(getDao(), getEntity(), data, efk)) {
				addDataIncorrectError(data, efk.getFields());
				return false;
			}
		}
		return true;
	}

	/**
	 * check data is changed on update
	 * @param data data
	 * @param sd source data
	 * @return true if check successfully
	 */
	protected boolean checkDataChangedOnUpdate(T data, T sd) {
		return checkDataChanged(data, sd, RES.WARN_DATA_CHANGED_PREFIX);
	}

	/**
	 * check data changed on delete
	 * @param data data
	 * @param sd source data
	 * @return true if check successfully
	 */
	protected boolean checkDataChangedOnDelete(T data, T sd) {
		if (checkDataChanged(data, sd, RES.WARN_DATA_CHANGED_PREFIX)) {
			return true;
		}

		setScenarioView();
		return false;
	}

	/**
	 * check data is changed or not
	 * @param data data
	 * @param sd source data
	 * @param msg warn message id
	 * @return true if check successfully
	 */
	protected boolean checkDataChanged(T data, T sd, String msg) {
		if (data instanceof IUpdate) {
			IUpdate cb = (IUpdate)data;
			IUpdate sb = (IUpdate)sd;
			if (Bean.isChanged(cb, sb)) {
				cb.setUusid(sb.getUusid());
				cb.setUtime(sb.getUtime());
				
				addActionWarning(getScenarioMessage(msg, DateTimes.isoDatetimeNotFormat().format(sb.getUtime())));
				return false;
			}
		}
		return true;
	}

	//------------------------------------------------------------
	// other methods
	//------------------------------------------------------------
	/**
	 * clear on copy
	 * @param data data
	 */
	protected void clearOnCopy(T data) {
		if (data != null) {
			EntityField eid = getEntity().getIdentity(); 
			if (eid == null) {
				EntityHelper.clearPrimaryKeyValues(getEntity(), data);
			}
			else {
				EntityHelper.clearIdentityValue(getEntity(), data);
			}
		}
	}
}