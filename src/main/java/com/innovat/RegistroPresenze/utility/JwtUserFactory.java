package com.innovat.RegistroPresenze.utility;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.innovat.RegistroPresenze.dto.DTOUser;
import com.innovat.RegistroPresenze.dto.JwtUser;


public final class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(DTOUser user) {
        return new JwtUser(
        		user.getId(),
                user.getUsername(),
                user.getPassword(),
                mapToGrantedAuthorities(user.getAuthorities()),
                true
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<String> authorities) {
        return authorities.stream()        		
                .map(authority -> new SimpleGrantedAuthority(authority))
                .collect(Collectors.toList());
    }
}


