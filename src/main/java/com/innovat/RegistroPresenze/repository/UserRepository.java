package com.innovat.RegistroPresenze.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.innovat.RegistroPresenze.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
	public User findByUsername(String username);

	public User findByEmail(String email);

	public User findByPhoneNumber(String username);

	

}