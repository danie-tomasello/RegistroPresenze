package com.innovat.RegistroPresenze.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.innovat.RegistroPresenze.dto.JwtUser;
import com.innovat.RegistroPresenze.model.User;
import com.innovat.RegistroPresenze.utility.DTOUserFactory;
import com.innovat.RegistroPresenze.utility.JwtUserFactory;

import lombok.extern.java.Log;

@Log
@Service
public class CustomerUserDetailsService implements UserDetailsService{
	
	//private static final Logger logger = LoggerFactory.getLogger(CustomerUserDetailsService.class);

	@Autowired
    private UserService service;
	
	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	return getHttpValue(username);
    }
    
    private JwtUser getHttpValue(String userId){
    	
    	User user=null;
    	try {
    		
    		user = service.loadUserByUsername(userId);
    		log.info(this.getClass().getSimpleName()+" "+user.toString());
    	}catch(Exception e) {
    		throw new UsernameNotFoundException(String.format("No user found with username '%s'.", userId));
    	}
    	return JwtUserFactory.create(DTOUserFactory.createDTOUser(user));
    }

}
