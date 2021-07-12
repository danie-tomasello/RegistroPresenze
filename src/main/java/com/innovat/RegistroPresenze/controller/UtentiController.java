package com.innovat.RegistroPresenze.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.innovat.RegistroPresenze.dto.DTOUser;
import com.innovat.RegistroPresenze.dto.JwtUser;
import com.innovat.RegistroPresenze.dto.requestResponse.MessageResponse;
import com.innovat.RegistroPresenze.dto.requestResponse.SaveUserRequest;
import com.innovat.RegistroPresenze.dto.requestResponse.UpdateUserRequest;
import com.innovat.RegistroPresenze.exception.BindingException;
import com.innovat.RegistroPresenze.exception.DuplicateException;
import com.innovat.RegistroPresenze.exception.NotFoundException;
import com.innovat.RegistroPresenze.model.User;
import com.innovat.RegistroPresenze.service.UserService;
import com.innovat.RegistroPresenze.utility.JwtTokenUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.java.Log;

@RestController
@RequestMapping(value = "${gestioneUtenti.uri}")
@Api(value="service", tags="Controller operazioni di gestione dati utenti")
@Log
public class UtentiController {
	
	@Autowired
    private UserService service;
	
	@Autowired
	private ResourceBundleMessageSource msg;
	
	@Value("${jwt.header}")
    private String tokenHeader;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
    private JwtTokenUtil jwtTokenUtil;
	
	
    
    
	@ApiOperation(
		      value = "Salvataggio utente", 
		      notes = "Salvataggio un utente attivo nel db",
		      response = MessageResponse.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 201, message = "L'utente è stato creato con successo"),
	    @ApiResponse(code = 400, message = "Questo utente esiste già"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
	@RequestMapping(value = "${gestioneUtenti.save}", method = RequestMethod.POST)
	public ResponseEntity<?> save(@ApiParam("Dati registrazione utente") @Valid @RequestBody SaveUserRequest requestBody,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws  BindingException, DuplicateException {
		log.info("===========================Start service/save/=="+requestBody.toString()+"=============================");
		
		JwtUser userlogged = jwtTokenUtil.getUserDetails(request.getHeader(tokenHeader));		
		MessageResponse res = new MessageResponse();
		//Input validation
		if(bindingResult.hasErrors()) {
			String error = msg.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());			
			log.warning(error);
			throw new BindingException(error);
		}
		
		log.info("start registrazione utente");
		
		DTOUser dtouser = new DTOUser();
		dtouser.setName(requestBody.getName());
		dtouser.setSurname(requestBody.getSurname());
		dtouser.setPassword(passwordEncoder.encode(requestBody.getPassword()));
		dtouser.setEmail(requestBody.getEmail());
		dtouser.setPhoneNumber(requestBody.getPhoneNumber());
		dtouser.setAuthorities(requestBody.getAuthorities());
		
		
		if(service.loadUserByEmail(dtouser.getEmail())!=null) {
    		String errMsg = msg.getMessage("exc.duplicate.email", null, LocaleContextHolder.getLocale());
    		log.warning(errMsg);
    		throw new DuplicateException(errMsg);	    	 
	    }
		
		if(service.loadUserByPhoneNumber(dtouser.getPhoneNumber())!=null) {
    		String errMsg = msg.getMessage("exc.duplicate.number", null, LocaleContextHolder.getLocale());
    		log.warning(errMsg);
    		throw new DuplicateException(errMsg);	    	 
	    }
		
		service.save(dtouser, userlogged.getUsername()); 
		
		
		res.setCod(HttpStatus.CREATED.value());
		res.setMsg(msg.getMessage("success.save", null, LocaleContextHolder.getLocale()));
    	
		return ResponseEntity.ok(res);
	}
	
	
    
	
	@ApiOperation(
		      value = "Modifica utente", 
		      notes = "Modifica le informazioni di un utente",
		      response = MessageResponse.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 201, message = "L'utente è stato modificato con successo"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
    @RequestMapping(value = "${gestioneUtenti.update}", method = RequestMethod.POST)
    public ResponseEntity<?> update(@ApiParam("Dati utente") @Valid @RequestBody UpdateUserRequest requestBody,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws NotFoundException, BindingException, DuplicateException {
    	log.info("===========================Start service/update/=="+requestBody.toString()+"=============================");
    	JwtUser userlogged = jwtTokenUtil.getUserDetails(request.getHeader(tokenHeader));	
    	MessageResponse res = new MessageResponse();
		//Input validation
		if(bindingResult.hasErrors()) {
			String error = msg.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());	
			log.warning(error);
			throw new BindingException(error);
		}
    	
    	if(!service.isExist(requestBody.getId())) {
    		String errMsg = msg.getMessage("exc.notFound.user", null, LocaleContextHolder.getLocale());
    		log.warning(errMsg);
    		throw new NotFoundException(errMsg);
    	}
    	
    	
    	log.info("start modifica utente");
    	
    	DTOUser dtouser = new DTOUser();
    	dtouser.setId(requestBody.getId());
    	dtouser.setUsername(requestBody.getUsername());
		dtouser.setName(requestBody.getName());
		dtouser.setSurname(requestBody.getSurname());
		dtouser.setPassword(passwordEncoder.encode(requestBody.getPassword()));
		dtouser.setEmail(requestBody.getEmail());
		dtouser.setPhoneNumber(requestBody.getPhoneNumber());
		dtouser.setAuthorities(requestBody.getAuthorities());
    	
    	service.update(dtouser,userlogged.getUsername()); 
    	
    	
    	res.setCod(HttpStatus.CREATED.value());
    	res.setMsg(msg.getMessage("success.update", null, LocaleContextHolder.getLocale()));
		return ResponseEntity.ok(res);
    	
    }
	
	
    
	
	
	@ApiOperation(
		      value = "Elimina utente", 
		      notes = "Elimina le informazioni di un utente",
		      response = MessageResponse.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 201, message = "L'utente è stato eliminato con successo"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
    @RequestMapping(value = "${gestioneUtenti.delete}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@ApiParam("Id utente univoco") @PathVariable(value="userId") Long userId, HttpServletRequest request, HttpServletResponse response) throws NotFoundException {
    	
    	log.info("===========================Start service/delete/=="+userId+"=============================");
    	MessageResponse res = new MessageResponse();
    	if(!service.isExist(userId)) {
    		String errMsg = msg.getMessage("exc.notFound.user", null, LocaleContextHolder.getLocale());
    		log.warning(errMsg);
    		throw new NotFoundException(errMsg);
    	}
    	
    	service.delete(userId);  
    	res.setCod(HttpStatus.OK.value());
    	res.setMsg(msg.getMessage("success.delete", null, LocaleContextHolder.getLocale()));
    	return ResponseEntity.ok(res);
    }
	
	@ApiOperation(
		      value = "Info account", 
		      notes = "Invia una mail con le info dell'account",
		      response = MessageResponse.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 201, message = "Email inviata con successo"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
	@RequestMapping(value = "${gestioneUtenti.send}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> send(	@ApiParam("Id utente univoco") 
									@RequestParam(name = "userMail") String userMail, 
									@ApiParam("password utente") 
									@RequestParam(name = "password") String password, 
									HttpServletRequest request, 
									HttpServletResponse response) throws NotFoundException, UnsupportedEncodingException, MessagingException {
	
		log.info("===========================Start service/send/=="+userMail+"=========="+ password +"===================");
		MessageResponse res = new MessageResponse();
		
		
		User user = service.loadUserByEmail(userMail);
		if(user==null) {
			String errMsg = msg.getMessage("exc.notFound.user", null, LocaleContextHolder.getLocale());
			log.warning(errMsg);
			throw new NotFoundException(errMsg);
		}
		
		service.send(user, password);  
		res.setCod(HttpStatus.OK.value());
		res.setMsg(msg.getMessage("success.send", null, LocaleContextHolder.getLocale()));
	  	return ResponseEntity.ok(res);
	}
	

    
}
