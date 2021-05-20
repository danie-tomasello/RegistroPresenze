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
	public ResponseEntity<?> save(@ApiParam("Dati registrazione utente") @Valid @RequestBody DTOUser dtouser,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws  BindingException, DuplicateException {
		log.info("===========================Start service/save/=="+dtouser.toString()+"=============================");
		
		JwtUser userlogged = jwtTokenUtil.getUserDetails(request.getHeader(tokenHeader));		
		MessageResponse res = new MessageResponse();
		//Input validation
		if(bindingResult.hasErrors()) {
			String error = msg.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());			
			log.warning(error);
			throw new BindingException(error);
		}
		if(service.loadUserByUsername(dtouser.getUsername())!=null) {
    		String errMsg = "Questo utente esiste già";
    		log.warning(errMsg);
    		throw new DuplicateException(errMsg);	    	 
	    }
		
		log.info("start registrazione utente");
		dtouser.setPassword(passwordEncoder.encode(dtouser.getPassword()));
		log.info("password criptata====== "+dtouser.getPassword());
    	service.save(dtouser, userlogged.getUsername()); 
		
		
		res.setCod(HttpStatus.CREATED.value());
		res.setMsg(msg.getMessage("save.success", null, LocaleContextHolder.getLocale()));
    	
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
    public ResponseEntity<?> update(@ApiParam("Dati utente") @Valid @RequestBody DTOUser dtouser,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws NotFoundException, BindingException {
    	log.info("===========================Start service/update/=="+dtouser.toString()+"=============================");
    	JwtUser userlogged = jwtTokenUtil.getUserDetails(request.getHeader(tokenHeader));	
    	MessageResponse res = new MessageResponse();
		//Input validation
		if(bindingResult.hasErrors()) {
			String error = msg.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());	
			log.warning(error);
			throw new BindingException(error);
		}
    	
    	if(!service.isExist(dtouser.getId())) {
    		String errMsg = "Utente inesistente";
    		log.warning(errMsg);
    		throw new NotFoundException(errMsg);
    	}
    	
    	service.update(dtouser,userlogged.getUsername()); 
    	res.setCod(HttpStatus.CREATED.value());
    	res.setMsg("Utente modificato con successo");
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
    		String errMsg = "Utente inesistente";
    		log.warning(errMsg);
    		throw new NotFoundException(errMsg);
    	}
    	
    	service.delete(userId);  
    	res.setCod(HttpStatus.OK.value());
    	res.setMsg("Utente eliminato con successo");
    	return ResponseEntity.ok(res);
    }
	

    
}
