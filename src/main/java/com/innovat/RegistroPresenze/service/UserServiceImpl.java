package com.innovat.RegistroPresenze.service;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.innovat.RegistroPresenze.dto.DTOUser;
import com.innovat.RegistroPresenze.model.Authority;
import com.innovat.RegistroPresenze.model.User;
import com.innovat.RegistroPresenze.repository.AuthorityRepository;
import com.innovat.RegistroPresenze.repository.UserRepository;
import com.innovat.RegistroPresenze.utility.DTOUserFactory;

import lombok.extern.java.Log;

@Log
@Service
@CacheConfig(cacheNames= {"user"})
public class UserServiceImpl implements UserService {	
     
	@Autowired
    private AuthorityRepository auth; 	    
    
    @Autowired
    private UserRepository repo;
    
   
    
   @Cacheable(value="user", key = "#username", sync = true)
    public User loadUserByUsername(String username) {
	   log.info("loadUser richiesta non cachata");
	   return repo.findByUsername(username);
    }

    @Cacheable
	public List<User> getAll() {
		// TODO Auto-generated method stub
    	log.info("getAll richiesta non cachata");
		return repo.findAll();
	}
	
    @Cacheable(value="user", key = "#id", sync = true)
	public boolean isExist(Long id) {
		// TODO Auto-generated method stub
		log.info("isExist richiesta non cachata");
		return repo.existsById(id);
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "users", allEntries = true),
			@CacheEvict(cacheNames = "user", key = "#dtouser.username")
	})
	public void save(DTOUser dtouser,String userLogged) {
		// TODO Auto-generated method stub
		User user = DTOUserFactory.createUser(dtouser,userLogged);
		
		Authority authorityUser = auth.findByName("ROLE_USER");
	    List<Authority> authorities = Arrays.asList(new Authority[] {authorityUser});
	    user.setAuthorities(authorities);
	    
    	repo.save(user);
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "users", allEntries = true),
			@CacheEvict(cacheNames = "user", key = "#id")
	})
	public void delete(Long id) {
		// TODO Auto-generated method stub
		repo.deleteById(id);
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "users", allEntries = true),
			@CacheEvict(cacheNames = "user", key = "#dtouser.username")
	})
	@Override
	public void update(DTOUser dtouser, String userLogged) {
		// TODO Auto-generated method stub
		
		User user = DTOUserFactory.createUser(dtouser,userLogged);
		List<Authority> authlist = new ArrayList<>();
		Map<String,Authority> mapAuth = mapListAuth(auth.findAll());
		
		for(String auth : dtouser.getAuthorities()) {
			if(mapAuth.containsKey(auth)) {
				authlist.add(mapAuth.get(auth));
			}
		}
		user.setAuthorities(authlist);
		repo.save(user);
	}
	
	private Map<String,Authority> mapListAuth(List<Authority> authorities) {
		Map<String,Authority> res = new HashMap<>();
        for(Authority authority : authorities) {
        	res.put(authority.getName(), authority);
        }
        return res;
    }

        
}
