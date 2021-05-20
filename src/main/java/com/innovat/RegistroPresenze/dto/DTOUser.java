package com.innovat.RegistroPresenze.dto;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.ToString;
@Data
@ToString
public class DTOUser {

	private Long id;
	
	@NotNull(message="{NotNull.User.username.Validation}")
    @Size(min = 4, max = 50, message="{Size.User.username.Validation}")
	private String username;
    
	@NotNull(message="{NotNull.User.password.Validation}")
    @Size(min = 4, max = 100, message="{Size.User.password.Validation}")
    private String password;
	
    @NotNull(message="{NotNull.User.email.Validation}")
    @Size(min = 4, max = 50 ,message="{Size.User.email.Validation}")
	private String email;
    
	@NotNull(message="{NotNull.User.phoneNumber.Validation}")
    @Size(min = 4, max = 15, message="{Size.User.phoneNumber.Validation}")
	private String phoneNumber;
	
	private String verification;
	
	private Boolean enabled;
	
	private List<String> authorities;
    
    
    
    public DTOUser(){}
}
