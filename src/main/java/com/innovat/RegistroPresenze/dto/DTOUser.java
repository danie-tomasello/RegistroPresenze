package com.innovat.RegistroPresenze.dto;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.innovat.RegistroPresenze.model.Event;

import lombok.Data;
import lombok.ToString;
@Data
@ToString
public class DTOUser {

	private Long id;
	
	private String username;
	
	private String name;

	private String surname;

    private String password;

	private String email;

	private String phoneNumber;
	
	private List<String> authorities;
   
    public DTOUser(){}
}
