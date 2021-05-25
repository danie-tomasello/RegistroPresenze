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
		if(dtouser.getUsername()==null) {
			String username = dtouser.getName().charAt(0) + "." +dtouser.getSurname();
			user.setUsername(username.toLowerCase());
		}
		else {
			user.setUsername(dtouser.getUsername());
		}
		
		user.setName(dtouser.getName());
		
		user.setSurname(dtouser.getSurname());
	    
	    user.setPassword(dtouser.getPassword());
	    
	    user.setEmail(dtouser.getEmail());
	    
	    user.setPhoneNumber(dtouser.getPhoneNumber());

	    user.setLastModifiedBy(userlog);
	    
	    return user;
	 
	}
	
	public static DTOUser createDTOUser(User user) {
		
		DTOUser dtouser = new DTOUser();
		
		dtouser.setId(user.getId());
		
		dtouser.setUsername(user.getUsername());
		
		dtouser.setName(user.getName());
		
		dtouser.setSurname(user.getSurname());
		
		dtouser.setPassword(user.getPassword());
		
		dtouser.setPhoneNumber(user.getPhoneNumber());
		
		dtouser.setEmail(user.getEmail());

		dtouser.setAuthorities(mapToGrantedAuthorities(user.getAuthorities()));
		
		
		return dtouser;
	}
	
	private static List<String> mapToGrantedAuthorities(List<Authority> authorities) {
        return authorities.stream()
                .map(authority -> authority.getName())
                .collect(Collectors.toList());
    }
	
}
