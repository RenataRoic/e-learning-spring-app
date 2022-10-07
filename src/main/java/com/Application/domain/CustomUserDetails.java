package com.Application.domain;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

	private User user;
	
	public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().getRole()));
    }
	
	public CustomUserDetails(User user) {
        this.user = user;
    }
	
	public Long getId() {
		return user.getId();
	}
	
	public String getName() {
		return user.getName();
	}
	
	public String getSurname() {
		return user.getSurname();
	}
	
	public String getEmail() {
		return user.getEmail();
	}
	
	public String getPassword() {
		return user.getPassword();
	}
	
	public boolean isAccountNonLocked() {
		return true;
	}
	
	public boolean isAccountNonExpired() {
		return true;
	}
	
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}
}
