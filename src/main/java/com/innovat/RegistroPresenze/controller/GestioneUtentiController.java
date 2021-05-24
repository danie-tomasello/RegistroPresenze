package com.innovat.RegistroPresenze.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.innovat.RegistroPresenze.dto.DTOUser;
import com.innovat.RegistroPresenze.dto.JwtUser;
import com.innovat.RegistroPresenze.dto.MessageResponse;
import com.innovat.RegistroPresenze.dto.SaveRequest;
import com.innovat.RegistroPresenze.dto.UpdateRequest;
import com.innovat.RegistroPresenze.exception.BindingException;
import com.innovat.RegistroPresenze.exception.DuplicateException;
import com.innovat.RegistroPresenze.exception.NotFoundException;
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
public class GestioneUtentiController {
	
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
	public ResponseEntity<?> save(@ApiParam("Dati registrazione utente") @Valid @RequestBody SaveRequest requestBody,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws  BindingException, DuplicateException {
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
    public ResponseEntity<?> update(@ApiParam("Dati utente") @Valid @RequestBody UpdateRequest requestBody,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws NotFoundException, BindingException, DuplicateException {
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
    	
    	if(service.loadUserByUsername(requestBody.getUsername())!=null) {
    		String errMsg = msg.getMessage("exc.duplicate.username", null, LocaleContextHolder.getLocale());
    		log.warning(errMsg);
    		throw new DuplicateException(errMsg);
    	}
    	
    	if(service.loadUserByEmail(requestBody.getUsername())!=null) {
    		String errMsg = msg.getMessage("exc.duplicate.email", null, LocaleContextHolder.getLocale());
    		log.warning(errMsg);
    		throw new DuplicateException(errMsg);
    	}
    	
    	if(service.loadUserByPhoneNumber(requestBody.getUsername())!=null) {
    		String errMsg = msg.getMessage("exc.duplicate.phoneNumber", null, LocaleContextHolder.getLocale());
    		log.warning(errMsg);
    		throw new DuplicateException(errMsg);
    	}
    	
    	log.info("start registrazione utente");
    	
    	DTOUser dtouser = new DTOUser();
    	dtouser.setId(requestBody.getId());
    	dtouser.setUsername(requestBody.getUsername());
		dtouser.setName(requestBody.getName());
		dtouser.setSurname(requestBody.getSurname());
		dtouser.setPassword(passwordEncoder.encode(requestBody.getPassword()));
		dtouser.setEmail(requestBody.getEmail());
		dtouser.setPhoneNumber(requestBody.getPhoneNumber());
    	
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
	

    
}
