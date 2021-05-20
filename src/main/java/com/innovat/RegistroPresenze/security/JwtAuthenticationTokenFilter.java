package com.innovat.RegistroPresenze.security;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.innovat.RegistroPresenze.exception.ExpiredSessionException;
import com.innovat.RegistroPresenze.utility.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.java.Log;

@Log
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {



    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String tokenHeader;
    
    @Value("${jwt.refreshHeader}")
    private String tokenRefreshHeader;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authToken = request.getHeader(tokenHeader);
        
        log.info(authToken+"========================");
        UserDetails userDetails = null;
        try {
	        if(authToken != null && jwtTokenUtil.validateToken(authToken)){
	            userDetails = jwtTokenUtil.getUserDetails(authToken);
	        }
	
	        if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	
	            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	            SecurityContextHolder.getContext().setAuthentication(authentication);
	            
	        }
	    }
        catch(ExpiredJwtException ex)
		{
        	log.warning("============================ token scaduto =========================");
			String refreshToken = request.getHeader(tokenRefreshHeader);
			String requestURL = request.getRequestURL().toString();
			// allow for Refresh Token creation if following conditions are true.
			try {
				if (refreshToken != null && jwtTokenUtil.validateToken(refreshToken) && requestURL.contains("refresh")) {
					log.info("==================il tokenRefresh è valido==============");
					// create a UsernamePasswordAuthenticationToken with null values.
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							null, null, null);
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
			}
			catch(ExpiredJwtException exc){
				log.info("====================== tokenRefresh non valido sessione scaduta =======================");
				request.setAttribute("exception", new ExpiredSessionException());
			}
		}

        chain.doFilter(request, response);
    }
    
}