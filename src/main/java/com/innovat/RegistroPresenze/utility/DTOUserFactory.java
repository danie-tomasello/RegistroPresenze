package com.innovat.RegistroPresenze.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.innovat.RegistroPresenze.dto.DTOUser;
import com.innovat.RegistroPresenze.model.Authority;
import com.innovat.RegistroPresenze.model.User;


public class DTOUserFactory {
	
	
	
	public static User createUser(DTOUser dtouser,String userlog) {
		User user = new User();
		if(dtouser.getId()!=null) {
			user.setId(dtouser.getId());
		}
		user.setUsername(dtouser.getUsername());
	    
	    user.setPassword(dtouser.getPassword());
	    
	    user.setEnabled(true); 
	    if(dtouser.getEnabled()!=null&&dtouser.getEnabled()==false) {
	    	user.setEnabled(dtouser.getEnabled());
	    }
	    
	    user.setEmail(dtouser.getEmail());
	    
	    user.setPhoneNumber(dtouser.getPhoneNumber());
	    
	    if(dtouser.getVerification()!=null) {
	    	user.setVerification(dtouser.getVerification());
	    }
	    user.setLastModifiedBy(userlog);
	    return user;
	 
	}
	
	public static DTOUser createDTOUser(User user) {
		DTOUser dtouser = new DTOUser();
		dtouser.setId(user.getId());
		
		dtouser.setUsername(user.getUsername());
		dtouser.setPassword(user.getPassword());
		dtouser.setVerification(user.getVerification());
		dtouser.setPhoneNumber(user.getPhoneNumber());
		dtouser.setEmail(user.getEmail());
		
		dtouser.setEnabled(user.getEnabled());
		dtouser.setAuthorities(mapToGrantedAuthorities(user.getAuthorities()));
		
		
		return dtouser;
	}
	
	private static List<String> mapToGrantedAuthorities(List<Authority> authorities) {
        return authorities.stream()
                .map(authority -> authority.getName())
                .collect(Collectors.toList());
    }
	
}
