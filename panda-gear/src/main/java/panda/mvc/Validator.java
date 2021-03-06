package panda.mvc;

public interface Validator {
	Validator getParent();
	
	void setParent(Validator parent);
	
	String getName();
	
	void setName(String name);
	
	String getRefer();
	
	void setRefer(String refer);
	
	String getMessage();
	
	void setMessage(String message);
	
	String getMsgId();
	
	void setMsgId(String msgId);
	
	boolean isShortCircuit();

	void setShortCircuit(boolean shortCircuit);
	
	boolean validate(ActionContext ac, Object obj);
}
