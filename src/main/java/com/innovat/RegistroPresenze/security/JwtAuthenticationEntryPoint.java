package com.innovat.RegistroPresenze.security;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovat.RegistroPresenze.exception.ExpiredSessionException;

import lombok.extern.java.Log;

@Component
@Log
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
    	   	
    	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		Exception exception = (Exception) request.getAttribute("exception");
		
		String message = authException.getMessage();

		
		if(exception!=null) {	
			if(exception instanceof ExpiredSessionException) {
				log.info("================= ExpiredSessionException exception ==================");
				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
				message = "La sessione è scaduta, si prega di effettuare il login";
				
			}
		}
		if(authException instanceof BadCredentialsException) {
			log.info("================= BadCredentialsException eccezione ==================");
			message = "Il token utilizzato non è valido.";
		}
		if(authException instanceof DisabledException) {
			log.info("================= DisabledException exception ==================");
			message = "Questo utente è disabilitato, contatta un amministratore per maggiori informazioni.";
			
		}
				
		byte[] body = new ObjectMapper().writeValueAsBytes(Collections.singletonMap("cause", message));

		response.getOutputStream().write(body);
    }
    
}