package com.innovat.RegistroPresenze.exception;

public class DuplicateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6068213907274435782L; 
	private String msg;
	
	public DuplicateException() {
		super();
	}
	
	public DuplicateException(String msg) {
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

