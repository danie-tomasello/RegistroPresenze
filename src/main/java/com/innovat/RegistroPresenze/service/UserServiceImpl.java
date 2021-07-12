package com.innovat.RegistroPresenze.service;



import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.innovat.RegistroPresenze.dto.DTOUser;
import com.innovat.RegistroPresenze.dto.JwtUser;
import com.innovat.RegistroPresenze.exception.DuplicateException;
import com.innovat.RegistroPresenze.model.Authority;
import com.innovat.RegistroPresenze.model.User;
import com.innovat.RegistroPresenze.repository.AuthorityRepository;
import com.innovat.RegistroPresenze.repository.UserRepository;
import com.innovat.RegistroPresenze.utility.DTOUserFactory;

import lombok.extern.java.Log;
import net.bytebuddy.utility.RandomString;

@Log
@Service
public class UserServiceImpl implements UserService {	
     
	@Autowired
    private AuthorityRepository auth; 	    
    
    @Autowired
    private UserRepository repo;
    
    @Autowired
    private JavaMailSender mailSender;
    
   
    
   @Cacheable(value="user", key ="#username")
    public User loadUserByUsername(String username) {
	   log.info("loadUser richiesta non cachata");
	   return repo.findByUsername(username);
    }

    @Cacheable(value="users")
	public List<User> getAll() {
		// TODO Auto-generated method stub
    	log.info("getAll richiesta non cachata");
		return repo.findAll();
	}
	
	public boolean isExist(Long id) {
		// TODO Auto-generated method stub
		return repo.existsById(id);
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "users", allEntries = true),
			@CacheEvict(cacheNames = "user", allEntries = true)
	})
	public void save(DTOUser dtouser,String userLogged) throws DuplicateException {
		// TODO Auto-generated method stub
		User user = DTOUserFactory.createUser(dtouser,userLogged);
		
		List<Authority> authlist = new ArrayList<>();
		Map<String,Authority> mapAuth = mapListAuth(auth.findAll());
		authlist.add(mapAuth.remove("ROLE_USER"));
		
		for(String auth : dtouser.getAuthorities()) {
			if(mapAuth.containsKey(auth)) {
				authlist.add(mapAuth.get(auth));
			}
		}
		
		int n = 0; 
		for(boolean i = false; i==false; n++ ) {
			
			String username = user.getUsername();
			if(n>0) {
				username+=n;
			}
			
			i = repo.findByUsername(username)==null;
			
			if(i==true) {
				user.setUsername(username);
			}
		}
		
		user.setAuthorities(authlist);
	    
		try {
			
			repo.save(user);
			
		} catch (DataIntegrityViolationException e) {
		    String[] key = e.getRootCause().toString().split("'");
		    
		    String msg = "Variabile '"+key[1]+ "' duplicata";
		    
		    if(key[1].equalsIgnoreCase(user.getUsername())) {
		    	msg = "Il nome utente esiste già";
		    }
		    if(key[1].equalsIgnoreCase(user.getEmail())) {
		    	msg = "Email esiste già";
		    }
		    if(key[1].equalsIgnoreCase(user.getPhoneNumber())) {
		    	msg = "Il numero di telefono esiste già";
		    }
		    throw new DuplicateException(msg);
		    
		}
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "users", allEntries = true),
			@CacheEvict(cacheNames = "user", allEntries = true)
	})
	public void delete(Long id) {
		// TODO Auto-generated method stub
		repo.deleteById(id);
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "users", allEntries = true),
			@CacheEvict(cacheNames = "user", allEntries = true)
	})
	@Override
	public void update(DTOUser dtouser, String userLogged) throws DuplicateException {
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
		
		try {
			
			repo.save(user);
			
		} catch (DataIntegrityViolationException e) {
		    String[] key = e.getRootCause().toString().split("'");
		    
		    String msg = "Variabile '"+key[1]+ "' duplicata";
		    
		    if(key[1].equalsIgnoreCase(user.getUsername())) {
		    	msg = "Il nome utente esiste già";
		    }
		    if(key[1].equalsIgnoreCase(user.getEmail())) {
		    	msg = "Email esiste già";
		    }
		    if(key[1].equalsIgnoreCase(user.getPhoneNumber())) {
		    	msg = "Il numero di telefono esiste già";
		    }
		    throw new DuplicateException(msg);
		    
		}
		
		
	}
	
	private Map<String,Authority> mapListAuth(List<Authority> authorities) {
		Map<String,Authority> res = new HashMap<>();
        for(Authority authority : authorities) {
        	res.put(authority.getName(), authority);
        }
        return res;
    }

	@Cacheable(value="user", key ="#email")
	@Override
	public User loadUserByEmail(String email) {
		log.info("findEmail richiesta non cachata");
		return repo.findByEmail(email);
	}

	@Cacheable(value="user", key ="#phoneNumber")
	@Override
	public User loadUserByPhoneNumber(String phoneNumber) {
		log.info("findPhoneNumber richiesta non cachata");
		return repo.findByPhoneNumber(phoneNumber);
	}

	@Cacheable(value="user", key ="#id")
	@Override
	public User loadUserById(Long id) {
		// TODO Auto-generated method stub
		log.info("findId richiesta non cachata");
		return repo.getOne(id);
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "users", allEntries = true),
			@CacheEvict(cacheNames = "user", allEntries = true)
	})
	@Override
	public void updatePassword(JwtUser userLogged, String newPassword) {
		// TODO Auto-generated method stub
		User user = repo.getOne(userLogged.getId());
		user.setPassword(newPassword);
		user.setLastModifiedBy(userLogged.getUsername());
		repo.save(user);
	}

	@Override
	public void send(User user, String password) throws UnsupportedEncodingException, MessagingException {
		
		String toAddress = user.getEmail();
        String fromAddress = "danieletomasello.innovat@gmail.com";
        String senderName = "Innovat";
        String subject = "Info account";
        String content = "Ciao [[name]],<br>"
                + "ecco le tue nuove credenziali per accedere al portale innovat per il registro delle presenze:<br>"
                + "<h3>username: [[username]], password: [[password]]</h3>"
                + "Grazie,<br>"
                + "Innovat.";
         
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
         
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
         
        content = content.replace("[[name]]", user.getName()+" "+user.getSurname());
         
        content = content.replace("[[username]]", user.getUsername());
        
        content = content.replace("[[password]]", password);
         
        helper.setText(content, true);
         
        mailSender.send(message);
		
	}
	

        
}
