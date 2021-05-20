package com.innovat.RegistroPresenze.exception;

public class ExpiredSessionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8293626885694428335L;

	private String msg = "Sessione scaduta";
		
		public ExpiredSessionException() {
			super();
		}
		
		public ExpiredSessionException(String msg) {
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
