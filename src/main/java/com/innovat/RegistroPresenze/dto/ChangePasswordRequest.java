package com.innovat.RegistroPresenze.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChangePasswordRequest {

	
	@NotNull(message="{NotNull.User.password}")
    @Size(min = 2, max = 50, message="{Size.User.password}")
	private String oldPassword;
	
	@NotNull(message="{NotNull.User.password}")
    @Size(min = 2, max = 50, message="{Size.User.password}")
	private String newPassword;
	
}
