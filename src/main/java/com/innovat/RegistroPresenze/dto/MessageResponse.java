package com.innovat.RegistroPresenze.dto;

import java.util.Date;

import lombok.Data;

@Data
public class MessageResponse {
	

	private Date data = new Date();
	private int cod;
	private String msg;

}
