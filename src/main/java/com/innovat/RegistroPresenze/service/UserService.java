package com.innovat.RegistroPresenze.service;

import java.util.List;

import com.innovat.RegistroPresenze.dto.DTOUser;
import com.innovat.RegistroPresenze.dto.JwtUser;
import com.innovat.RegistroPresenze.exception.DuplicateException;
import com.innovat.RegistroPresenze.model.User;


public interface UserService {
	
	public List<User> getAll();
	
	public User loadUserByUsername(String username);
	
	public void save(DTOUser user, String userLogged) throws DuplicateException;
	
	public void update(DTOUser user, String userlogged) throws DuplicateException;
	
	public void delete(Long id);
	
	public boolean isExist(Long id);

	public User loadUserByEmail(String email);

	public User loadUserByPhoneNumber(String username);

	public User loadUserById(Long id);

	public void updatePassword(JwtUser userlogged, String username);

}
