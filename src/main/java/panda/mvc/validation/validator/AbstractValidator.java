package panda.mvc.validation.validator;

import panda.el.El;
import panda.lang.Strings;
import panda.log.Log;
import panda.log.Logs;
import panda.mvc.ActionContext;
import panda.mvc.Mvcs;
import panda.mvc.util.TextProvider;


/**
 * Base class for Validator.
 */
public abstract class AbstractValidator implements Validator {
	private static final Log log = Logs.getLog(AbstractValidator.class);
	
	private Validator parent;
	
	private String name;
	
	private String message;
	
	private String msgId;

	private boolean shortCircuit;

	private Object value;
	
	/**
	 * @return the parent
	 */
	public Validator getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Validator parent) {
		this.parent = parent;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the msgId
	 */
	public String getMsgId() {
		return msgId;
	}

	/**
	 * @param msgId the msgId to set
	 */
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	/**
	 * @return the shortCircuit
	 */
	public boolean isShortCircuit() {
		return shortCircuit;
	}

	/**
	 * @param shortCircuit the shortCircuit to set
	 */
	public void setShortCircuit(boolean shortCircuit) {
		this.shortCircuit = shortCircuit;
	}

	//-----------------------------------------------
	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	protected void setValue(Object value) {
		this.value = value;
	}

	protected String getFullFieldName(String name) {
		if (parent == null) {
			return name;
		}
		
		StringBuilder sb = new StringBuilder(name);
		Validator p = parent;
		while (p != null) {
			if (Strings.isNotEmpty(p.getName())) {
				sb.insert(0, '.');
				sb.insert(0, p.getName());
			}
			p = p.getParent();
		}
		return sb.toString();
	}
	
	protected Object evalExpression(ActionContext ac, String expression) {
		try {
			ac.push(this);
			return El.eval(expression, ac);
		}
		finally {
			ac.pop();
		}
	}
	
	protected String evalMessage(ActionContext ac) {
		if (Strings.isNotEmpty(message)) {
			return Mvcs.translate(message, ac, this);
		}
		
		if (Strings.isNotEmpty(msgId)) {
			TextProvider tp = ac.getText();
			if (tp == null) {
				log.error("Null TextProvider of " + this.getClass() + "('" + getName() + "')");
				return msgId;
			}
			return tp.getText(msgId, msgId, this);
		}
		
		log.warn("Missing 'message' or 'msgId' of " + this.getClass() + "('" + getName() + "')");
		return "";
	}
	
	protected void addFieldError(ActionContext ac) {
		addFieldError(ac, name);
	}

	protected void addFieldError(ActionContext ac, String name) {
		String fn = getFullFieldName(name);
		String msg = evalMessage(ac);
		ac.getParamAlert().addError(fn, msg);
	}

	@Override
	public boolean validate(ActionContext ac, Object value) {
		this.value = value;
		return validateValue(ac, value);
	}

	protected abstract boolean validateValue(ActionContext ac, Object value);
}
