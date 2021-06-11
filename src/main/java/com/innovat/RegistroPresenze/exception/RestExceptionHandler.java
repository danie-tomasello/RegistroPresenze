package com.innovat.RegistroPresenze.exception;


import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



@ControllerAdvice
@RestController
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private ResourceBundleMessageSource msg;

	@ExceptionHandler(BindingException.class)
	public final ResponseEntity<?> exceptionBindingHandler(Exception ex) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		byte[] body = new ObjectMapper().writeValueAsBytes(Collections.singletonMap("cause", ex.getMessage()));
		return new ResponseEntity<>(body,headers,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ExpiredSessionException.class)
	public final ResponseEntity<?> expiredSessionHandler(Exception ex) throws JsonProcessingException{
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		byte[] body = new ObjectMapper().writeValueAsBytes(Collections.singletonMap("cause", msg.getMessage("exc.expiredSession",null, LocaleContextHolder.getLocale())));
		return new ResponseEntity<>(body,headers,HttpStatus.NOT_ACCEPTABLE);
	}
	@ExceptionHandler(BadCredentialsException.class)
	public final ResponseEntity<?> badCredencialsHandler(Exception ex) throws JsonProcessingException{
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		byte[] body = new ObjectMapper().writeValueAsBytes(Collections.singletonMap("cause", msg.getMessage("exc.badcredencials",null, LocaleContextHolder.getLocale())));
		return new ResponseEntity<>(body,headers,HttpStatus.UNAUTHORIZED);
	}
	@ExceptionHandler(DisabledException.class)
	public final ResponseEntity<?> disabledHandler(Exception ex) throws JsonProcessingException{
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		byte[] body = new ObjectMapper().writeValueAsBytes(Collections.singletonMap("cause", msg.getMessage("exc.disabled",null, LocaleContextHolder.getLocale())));
		return new ResponseEntity<>(body,headers,HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(NotFoundException.class)
	public final ResponseEntity<?> exceptionNotFoundHandler(Exception ex) throws JsonProcessingException{
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		byte[] body = new ObjectMapper().writeValueAsBytes(Collections.singletonMap("cause",ex.getMessage()));
		return new ResponseEntity<>(body,headers,HttpStatus.NOT_FOUND);
	}
	
	
	@ExceptionHandler(DuplicateException.class)
	public final ResponseEntity<?> exceptionDuplicateHandler(Exception ex) throws JsonProcessingException{
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		byte[] body = new ObjectMapper().writeValueAsBytes(Collections.singletonMap("cause",ex.getMessage()));
		return new ResponseEntity<>(body,headers,HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler(DateFormatException.class)
	public final ResponseEntity<?> exceptionDateFormatHandler(Exception ex) throws JsonProcessingException{
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		byte[] body = new ObjectMapper().writeValueAsBytes(Collections.singletonMap("cause",ex.getMessage()));
		return new ResponseEntity<>(body,headers,HttpStatus.NOT_ACCEPTABLE);
	}

}
