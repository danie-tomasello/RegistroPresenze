package com.innovat.RegistroPresenze.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.innovat.RegistroPresenze.dto.AccountUpdateRequest;
import com.innovat.RegistroPresenze.dto.ChangePasswordRequest;
import com.innovat.RegistroPresenze.dto.DTOEvent;
import com.innovat.RegistroPresenze.dto.DTOUser;
import com.innovat.RegistroPresenze.dto.JwtUser;
import com.innovat.RegistroPresenze.dto.requestResponse.MessageResponse;
import com.innovat.RegistroPresenze.dto.requestResponse.SearchEventRequest;
import com.innovat.RegistroPresenze.exception.BindingException;
import com.innovat.RegistroPresenze.exception.DateFormatException;
import com.innovat.RegistroPresenze.exception.DuplicateException;
import com.innovat.RegistroPresenze.exception.NotFoundException;
import com.innovat.RegistroPresenze.model.User;
import com.innovat.RegistroPresenze.service.EventService;
import com.innovat.RegistroPresenze.service.UserService;
import com.innovat.RegistroPresenze.utility.DTOUserFactory;
import com.innovat.RegistroPresenze.utility.JwtTokenUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.java.Log;


@RestController
@RequestMapping(value = "${accountManager.uri}")
@Api(value="service", tags="Controller operazioni di gestione account")
@Log
public class AccountManagerController {
	
	@Autowired
    private UserService service;
	
	@Autowired
    private EventService eventService;
	
	@Autowired
	private ResourceBundleMessageSource msg;
	
	@Value("${jwt.header}")
    private String tokenHeader;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
    private JwtTokenUtil jwtTokenUtil;
	
	@ApiOperation(
		      value = "Modifica account", 
		      notes = "Modifica le informazioni di un utente",
		      response = MessageResponse.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 201, message = "L'utente è stato modificato con successo"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
  @RequestMapping(value = "${accountManager.update}", method = RequestMethod.POST)
  public ResponseEntity<?> update(@ApiParam("Dati utente") @Valid @RequestBody AccountUpdateRequest requestBody,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws NotFoundException, BindingException, DuplicateException {
  	log.info("===========================Start account/update/=="+requestBody.toString()+"=============================");
  	
  	JwtUser userlogged = jwtTokenUtil.getUserDetails(request.getHeader(tokenHeader));	
  	MessageResponse res = new MessageResponse();
		//Input validation
		if(bindingResult.hasErrors()) {
			String error = msg.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());	
			log.warning(error);
			throw new BindingException(error);
		}
  	
  	
  	log.info("start modifica utente");
  	
  	DTOUser dtouser = new DTOUser();
  	dtouser.setId(userlogged.getId());
  	dtouser.setUsername(requestBody.getUsername());
	dtouser.setName(requestBody.getName());
	dtouser.setSurname(requestBody.getSurname());
	dtouser.setPassword(userlogged.getPassword());
	dtouser.setEmail(requestBody.getEmail());
	dtouser.setPhoneNumber(requestBody.getPhoneNumber());
	dtouser.setAuthorities(mapToStringAuthorities(userlogged.getAuthorities()));
		
		
  	service.update(dtouser,userlogged.getUsername()); 
  	res.setCod(HttpStatus.CREATED.value());
  	res.setMsg(msg.getMessage("success.update", null, LocaleContextHolder.getLocale()));
		return ResponseEntity.ok(res);
  	
  }
	
	@ApiOperation(
		      value = "Modifica account", 
		      notes = "Modifica le informazioni di un utente",
		      response = MessageResponse.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 201, message = "L'utente è stato modificato con successo"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
	@RequestMapping(value = "${accountManager.changePassword}", method = RequestMethod.POST)
	public ResponseEntity<?> changePassword(@ApiParam("Dati cambio password") @Valid @RequestBody ChangePasswordRequest requestBody,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws NotFoundException, BindingException, DuplicateException {
		log.info("===========================Start account/update/=="+requestBody.toString()+"=============================");
		
		JwtUser userlogged = jwtTokenUtil.getUserDetails(request.getHeader(tokenHeader));	
		MessageResponse res = new MessageResponse();
			//Input validation
			if(bindingResult.hasErrors()) {
				String error = msg.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());	
				log.warning(error);
				throw new BindingException(error);
			}
			
			if(!passwordEncoder.matches(requestBody.getOldPassword(),userlogged.getPassword() )) {
				String error = msg.getMessage("exc.confirmPass",null, LocaleContextHolder.getLocale());	
				log.warning(error);
				throw new BindingException(error);
			}
		
		
		log.info("start modifica password");
			
			
		service.updatePassword(userlogged,passwordEncoder.encode(requestBody.getNewPassword())); 
		res.setCod(HttpStatus.CREATED.value());
		res.setMsg(msg.getMessage("success.changePassword", null, LocaleContextHolder.getLocale()));
			return ResponseEntity.ok(res);
		
	}
	
	@ApiOperation(
		      value = "Caricamento dati utente", 
		      notes = "Carica i dati dell'utente loggato",
		      response = DTOUser.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "L'utente è stato trovato"),
	    @ApiResponse(code = 404, message = "Nessun utente trovato"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
		})
	@RequestMapping(value = "${accountManager.get}", method = RequestMethod.GET)
	public ResponseEntity<?> getByUsername(HttpServletRequest request) throws NotFoundException {
	log.info("===========================Start /account/get======");
	
	JwtUser userlogged = jwtTokenUtil.getUserDetails(request.getHeader(tokenHeader));
	User user = service.loadUserById(userlogged.getId());
	
	if(user==null) {
		String errMsg = msg.getMessage("exc.notFound.search", null, LocaleContextHolder.getLocale());
			log.warning(errMsg);
			throw new NotFoundException(errMsg);
		}
		
		return ResponseEntity.ok(DTOUserFactory.createDTOUser(user));
	}
	
	
	
	@ApiOperation(
		      value = "Cerca timbrature", 
		      notes = "Cerca timbrature",
		      response = MessageResponse.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 201, message = "Ricerca effettuata con successo"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
	@RequestMapping(value = "${accountManager.search}", method = RequestMethod.POST)
	public ResponseEntity<?> searchEvent(@ApiParam("Richiesta ricerca timbrature") @RequestBody SearchEventRequest requestBody,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws NotFoundException, DateFormatException {
		
		log.info("===========================Start account/event/search/=="+requestBody.toString()+"=============================");
	  	
		JwtUser userlogged = jwtTokenUtil.getUserDetails(request.getHeader(tokenHeader));
		
	  	List<DTOEvent> eventList = new ArrayList<>();
		try {
			eventList = eventService.getByDateRange(userlogged.getId(),requestBody.getStartDate(),requestBody.getEndDate());
		} catch (ParseException e) {
			String errMsg = msg.getMessage("exc.dateFormat", null, LocaleContextHolder.getLocale());
	  		log.warning(errMsg);
			throw new DateFormatException();
		}
	  	
	  	
	  	if(eventList.isEmpty()) {
	  		String errMsg = msg.getMessage("exc.notFound.search.event", null, LocaleContextHolder.getLocale());
	  		log.warning(errMsg);
	  		throw new NotFoundException(errMsg);
	  	}
	  	
	  	return ResponseEntity.ok(eventList);
	}
	
	private List<String> mapToStringAuthorities(Collection<? extends GrantedAuthority> collection) {
        return collection.stream()        		
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
    }

}
