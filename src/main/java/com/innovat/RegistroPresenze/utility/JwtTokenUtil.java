package com.innovat.RegistroPresenze.utility;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovat.RegistroPresenze.dto.JwtUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_AUDIENCE = "audience";
    static final String CLAIM_KEY_CREATED = "iat";
    static final String CLAIM_KEY_AUTHORITIES = "roles";
    static final String CLAIM_KEY_IS_ENABLED = "isEnabled";


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration;

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public JwtUser getUserDetails(String token) {

        if(token == null){
            return null;
        }
        try {
            final Claims claims = getClaimsFromToken(token);
            List<SimpleGrantedAuthority> authorities = null;
            if (claims.get(CLAIM_KEY_AUTHORITIES) != null) {
                authorities = ((List<String>) claims.get(CLAIM_KEY_AUTHORITIES)).stream().map(role-> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
            }

            return new JwtUser(
                    claims.getSubject(),
                    "",
                    authorities,
                    (boolean) claims.get(CLAIM_KEY_IS_ENABLED)
            );
        } catch (Exception e) {
            return null;
        }

    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }


    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails) throws JsonProcessingException {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        List<String> auth =userDetails.getAuthorities().stream().map(role-> role.getAuthority()).collect(Collectors.toList());
        claims.put(CLAIM_KEY_AUTHORITIES, auth);
        claims.put(CLAIM_KEY_IS_ENABLED,userDetails.isEnabled());

        return generateToken(claims);
    }

    public String generateToken(Map<String, Object> claims) {
    	
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
    
    public String createRefreshToken(String subject) {
        if (subject==null||subject.equals("")) {
            throw new IllegalArgumentException("Cannot create JWT Token without username");
        }
        Claims claims = Jwts.claims().setSubject(subject);
        claims.put(CLAIM_KEY_AUTHORITIES, Arrays.asList("REFRESH_TOKEN"));
        
        return Jwts.builder()
          .setClaims(claims)
          .setIssuedAt(new Date(System.currentTimeMillis()))
          .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
          .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
    }

    public Boolean canTokenBeRefreshed(String token) {
        return  (!isTokenExpired(token));
    }

    public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public List<SimpleGrantedAuthority> getRolesFromToken(String token) {
		Claims claims = getClaimsFromToken(token);

		 List<SimpleGrantedAuthority> authorities = null;
         if (claims.get(CLAIM_KEY_AUTHORITIES) != null) {
             authorities = ((List<String>) claims.get(CLAIM_KEY_AUTHORITIES)).stream().map(role-> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
         }
		return authorities;

	}
    
    public boolean validateToken(String token) {
		try {
			final Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
			return true;
		} catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
			throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
		} catch (ExpiredJwtException ex) {
			throw ex;
		}
	}
 
}