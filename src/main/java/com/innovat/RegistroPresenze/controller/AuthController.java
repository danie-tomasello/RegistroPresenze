package com.innovat.RegistroPresenze.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.innovat.RegistroPresenze.dto.requestResponse.JwtAuthenticationRequest;
import com.innovat.RegistroPresenze.dto.requestResponse.JwtAuthenticationResponse;
import com.innovat.RegistroPresenze.exception.ExpiredSessionException;
import com.innovat.RegistroPresenze.utility.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.java.Log;

@Log
@RestController
@Api(value="auth", tags="Controller operazioni di autenticazione")
@RequestMapping(value = "${sicurezza.uri}")
public class AuthController {

    @Value("${jwt.header}")
    private String tokenHeader;
    
    @Value("${jwt.refreshHeader}")
    private String tokenRefreshHeader;   

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private Map<String, String> tokenSessionMap;
    
    
    
    @ApiOperation(
		      value = "Login utente", 
		      notes = "Restituisce il nome utente e i suoi ruoli",
		      response = JwtAuthenticationResponse.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "Login effettuato con successo"),
	    @ApiResponse(code = 401, message = "Username o password errate/utente disabilitato")
	})
    @RequestMapping(value = "${sicurezza.signin}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@ApiParam("Username e password utente") @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response,HttpServletRequest request) throws Exception {
    	
    	log.info("===========================Start auth/signin/=="+authenticationRequest.toString()+"=============================");
    	
        	
    	// Effettuo l autenticazione
	        final Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(
	                        authenticationRequest.getUsername(),
	                        authenticationRequest.getPassword()
	                )
	        );
	        
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        
        
        

        // Genero Token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        final String refreshToken = jwtTokenUtil.createRefreshToken(userDetails.getUsername());
        
        tokenSessionMap.put(userDetails.getUsername(), refreshToken);
		
		
        // Ritorno il token        
        response.addHeader(tokenHeader, token);
        response.addHeader(tokenRefreshHeader, refreshToken);
        return ResponseEntity.ok(new JwtAuthenticationResponse(userDetails.getUsername(),userDetails.getAuthorities()));
    }
    
    
    
    @ApiOperation(
		      value = "Logout utente", 
		      notes = "Effettua il logout eliminando la sessione utente", 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "Refresh effettuato con successo"),
	    @ApiResponse(code = 401, message = "Token invalido")
	})
    @RequestMapping(value="${sicurezza.logout}",method=RequestMethod.GET)
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("===========================Start auth/logout/===============================");
		
		String refreshToken=request.getHeader(tokenRefreshHeader);
		String userId= jwtTokenUtil.getUsernameFromToken(refreshToken);
		
		tokenSessionMap.remove(userId);
		
		HttpSession session= request.getSession(false);
	    SecurityContextHolder.clearContext();
	        if(session != null) {
	            session.invalidate();
	        }
	        
	    return ResponseEntity.ok().body(null);
			
	}
    
    
    @ApiOperation(
		      value = "Refresh token di accesso", 
		      notes = "Effettua il refresh del token di accesso", 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "Refresh effettuato con successo"),
	    @ApiResponse(code = 401, message = "Token refresh invalido")
	})
    @RequestMapping(value = "${sicurezza.refresh}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request, HttpServletResponse response) throws ExpiredSessionException {
    	log.info("===========================Start auth/refresh/===============================");
    	
    	DefaultClaims claims = null;
    	String token = request.getHeader(tokenHeader);
    	String refreshToken=request.getHeader(tokenRefreshHeader);
    	try {
    		jwtTokenUtil.validateToken(token);
    	}
    	catch (ExpiredJwtException ex){
    		claims = (DefaultClaims) ex.getClaims();
    	}
    	if (claims!=null) {
	    	if(!tokenSessionMap.containsKey(claims.getSubject()) || !tokenSessionMap.containsValue(refreshToken)) {
	    		throw new ExpiredSessionException(); 
	    	}
    	
        
            String refreshedToken = jwtTokenUtil.generateToken(claims);
            log.info("claims ============================ "+claims.getSubject());
            
            response.setHeader(tokenRefreshHeader, jwtTokenUtil.createRefreshToken(claims.getSubject()));
            response.setHeader(tokenHeader,refreshedToken);
            
           
            tokenSessionMap.put(claims.getSubject(), refreshedToken);
            

            return ResponseEntity.ok().body(null);
        }
    	else {
        
    		return ResponseEntity.badRequest().body(null);
    	}
        
    }
    
    
     

}
