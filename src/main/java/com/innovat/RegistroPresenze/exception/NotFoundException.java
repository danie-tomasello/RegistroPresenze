package com.innovat.RegistroPresenze.exception;


public class NotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6036055356889060053L;

	private String msg = "Elemento non trovato";
	
	public NotFoundException() {
		super();
	}
	
	public NotFoundException(String msg) {
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
