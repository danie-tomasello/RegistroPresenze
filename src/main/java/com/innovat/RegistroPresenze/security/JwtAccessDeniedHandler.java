package com.innovat.RegistroPresenze.security;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		// TODO Auto-generated method stub
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		byte[] body = new ObjectMapper().writeValueAsBytes(Collections.singletonMap("cause", "Non hai i permessi per accedere a queste informazioni."));

		response.getOutputStream().write(body);
	}

}
