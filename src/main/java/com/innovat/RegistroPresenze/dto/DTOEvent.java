package com.innovat.RegistroPresenze.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DTOEvent implements Serializable{
	   /**
		 * 
		 */
		private static final long serialVersionUID = -3518018508482697232L;
		
		private String id;
		
		private String idUser;
		
		private String username;
		
		private String data;
		
		private String input1;
		
		private String output1;
		
		private String input2;
		
		private String output2;
	    
	    
	    public DTOEvent() {}
	    
	    public DTOEvent(
	    				String id,
	    				String idUser, 
	    				String username, 
	    				String data, 
	    				String input1, 
	    				String output1,
	    				String input2, 
	    				String output2) {
	    	this.id = id;
	    	this.idUser = idUser;
	    	this.username = username;
	    	this.data = data;
	    	this.input1 = input1;
	    	this.output1 = output1;
	    	this.input2 = input2;
	    	this.output2 = output2;
	    }
	    
}
