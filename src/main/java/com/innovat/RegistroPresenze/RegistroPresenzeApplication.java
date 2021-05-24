package com.innovat.RegistroPresenze;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.innovat.RegistroPresenze.model.Authority;
import com.innovat.RegistroPresenze.model.User;
import com.innovat.RegistroPresenze.repository.AuthorityRepository;
import com.innovat.RegistroPresenze.repository.UserRepository;

@EnableJpaRepositories
@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class RegistroPresenzeApplication {
	
	@Autowired
	PasswordEncoder passwordEncoder;

	@Bean
	public CommandLineRunner loadData (UserRepository userRepository, AuthorityRepository authorityRepository) {
		return (args) -> {

			
			
			User user=userRepository.findByUsername("admin");
			User user2=userRepository.findByUsername("dani");
			
			
			User dani=userRepository.findByUsername("daniele");
			if(dani!=null) {
				userRepository.delete(dani);
			}
			
			if(user == null){

				/**
				 * Inizializzo i dati del mio test
				 */

				Authority authorityAdmin=new Authority();
				authorityAdmin.setName("ROLE_ADMIN");
				authorityAdmin=authorityRepository.save(authorityAdmin);

				Authority authorityUser=new Authority();
				authorityUser.setName("ROLE_USER");
				authorityUser=authorityRepository.save(authorityUser);
				


				List<Authority> authorities = Arrays.asList(new Authority[] {authorityAdmin,authorityUser});


				user = new User();
				user.setAuthorities(authorities);
				user.setUsername("admin");
				user.setEmail("email@example.it");
				user.setPhoneNumber("123456789");
				user.setPassword(passwordEncoder.encode("admin"));

				user = userRepository.save(user);

			}
			if(user2 == null){

				/**
				 * Inizializzo i dati del mio test
				 */



				
				Authority authorityUser= authorityRepository.findByName("ROLE_USER");


				List<Authority> authorities = Arrays.asList(new Authority[] {authorityUser});


				user = new User();
				user.setAuthorities(authorities);
				user.setUsername("dani");
				user.setEmail("email2@example.it");
				user.setPhoneNumber("987654321");
				user.setPassword(passwordEncoder.encode("dani"));

				user = userRepository.save(user);

			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(RegistroPresenzeApplication.class, args);
	}

}
