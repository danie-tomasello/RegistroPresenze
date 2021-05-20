package com.innovat.RegistroPresenze.exception;

public class BindingException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6168954173410326723L;  
	
	private String msg;
	
	public BindingException() {
		super();
	}
	
	public BindingException(String msg) {
		super(msg);
		this.msg=msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
