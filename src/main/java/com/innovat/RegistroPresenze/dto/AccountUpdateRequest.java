package com.innovat.RegistroPresenze.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccountUpdateRequest {

	
	@NotNull(message="{NotNull.User.username}")
    @Size(min = 2, max = 50, message="{Size.User.username}")
	private String username;
	
	@NotNull(message="{NotNull.User.name.}")
    @Size(min = 2, max = 50, message="{Size.User.name}")
	private String name;
	
	@NotNull(message="{NotNull.User.surname}")
    @Size(min = 2, max = 50, message="{Size.User.surname}")
	private String surname;
    
	@NotNull(message="{NotNull.User.password}")
    @Size(min = 4, max = 100, message="{Size.User.password}")
    private String password;
	
    @NotNull(message="{NotNull.User.email}")
    @Size(min = 4, max = 50 ,message="{Size.User.email.")
	private String email;
    
    @Size(min = 4, max = 15, message="{Size.User.phoneNumber}")
	private String phoneNumber;
	
    
    public AccountUpdateRequest(){}
}
