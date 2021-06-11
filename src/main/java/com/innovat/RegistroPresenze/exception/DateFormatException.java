package com.innovat.RegistroPresenze.exception;

public class DateFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6068213907274435782L; 
	private String msg;
	
	public DateFormatException() {
		super();
	}
	
	public DateFormatException(String msg) {
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
