package com.innovat.RegistroPresenze.service;

import java.util.List;

import com.innovat.RegistroPresenze.dto.DTOUser;
import com.innovat.RegistroPresenze.model.User;


public interface UserService {
	
	public List<User> getAll();
	
	public User loadUserByUsername(String username);
	
	public void save(DTOUser user, String userLogged);
	
	public void update(DTOUser user, String userLogged);
	
	public void delete(Long id);
	
	public boolean isExist(Long id);

}
