package panda.mvc.alert;

import java.util.ArrayList;
import java.util.Collection;

import panda.ioc.Scope;
import panda.ioc.annotation.IocBean;
import panda.lang.Collections;

@IocBean(type=ActionAlert.class, scope=Scope.REQUEST)
public class ActionAlertSupport implements ActionAlert {

	private Collection<String> errors;
	private Collection<String> warnings;
	private Collection<String> confirms;
	private Collection<String> messages;

	/**
	 * @see panda.mvc.alert.ActionAlert#getErrors()
	 */
	public Collection<String> getErrors() {
		return errors;
	}

	protected Collection<String> _getErrors() {
		if (errors == null) {
			errors = new ArrayList<String>();
		}
		return errors;
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#setErrors(java.util.Collection)
	 */
	public void setErrors(Collection<String> errors) {
		this.errors = errors;
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#addError(java.lang.String)
	 */
	public void addError(String error) {
		_getErrors().add(error);
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#getWarnings()
	 */
	public Collection<String> getWarnings() {
		return warnings;
	}

	protected Collection<String> _getWarnings() {
		if (warnings == null) {
			warnings = new ArrayList<String>();
		}
		return warnings;
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#setWarnings(java.util.Collection)
	 */
	public void setWarnings(Collection<String> warnings) {
		this.warnings = warnings;
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#addWarning(java.lang.String)
	 */
	public void addWarning(String warning) {
		_getWarnings().add(warning);
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#getConfirms()
	 */
	public Collection<String> getConfirms() {
		return confirms;
	}

	protected Collection<String> _getConfirms() {
		if (confirms == null) {
			confirms = new ArrayList<String>();
		}
		return confirms;
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#setConfirms(java.util.Collection)
	 */
	public void setConfirms(Collection<String> confirms) {
		this.confirms = confirms;
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#addConfirm(java.lang.String)
	 */
	public void addConfirm(String confirm) {
		_getConfirms().add(confirm);
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#getMessages()
	 */
	public Collection<String> getMessages() {
		return messages;
	}

	protected Collection<String> _getMessages() {
		if (messages == null) {
			messages = new ArrayList<String>();
		}
		return messages;
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#setMessages(java.util.Collection)
	 */
	public void setMessages(Collection<String> messages) {
		this.messages = messages;
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#addMessage(java.lang.String)
	 */
	public void addMessage(String message) {
		_getMessages().add(message);
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#hasErrors()
	 */
	public boolean hasErrors() {
		return Collections.isNotEmpty(errors);
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#hasWarnings()
	 */
	public boolean hasWarnings() {
		return Collections.isNotEmpty(warnings);
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#hasConfirms()
	 */
	public boolean hasConfirms() {
		return Collections.isNotEmpty(confirms);
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#hasMessages()
	 */
	public boolean hasMessages() {
		return Collections.isNotEmpty(messages);
	}

	/**
	 * @see panda.mvc.alert.ActionAlert#hasContents()
	 */
	public boolean hasContents() {
		return Collections.isNotEmpty(errors) 
				|| Collections.isNotEmpty(warnings) 
				|| Collections.isNotEmpty(confirms) 
				|| Collections.isNotEmpty(messages);
	}
	
	/**
	 * Clears all error list.
	 * <p/>
	 * Will clear the list that contain errors.
	 */
	public void clearErrors() {
		Collections.clear(errors);
	}

	/**
	 * Clears warning list.
	 * <p/>
	 * Will clear the list that contains warning.
	 */
	public void clearWarnings() {
		Collections.clear(warnings);
	}

	/**
	 * Clears confirm list.
	 * <p/>
	 * Will clear the list that contains confirm.
	 */
	public void clearConfirms() {
		Collections.clear(confirms);
	}

	/**
	 * Clears messages list.
	 * <p/>
	 * Will clear the list that contains messages.
	 */
	public void clearMessages() {
		Collections.clear(messages);
	}

	/**
	 * Clear all.
	 */
	public void clear() {
		clearErrors();
		clearWarnings();
		clearConfirms();
		clearMessages();
	}
}
