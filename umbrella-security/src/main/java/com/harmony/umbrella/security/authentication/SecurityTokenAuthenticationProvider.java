package com.harmony.umbrella.security.authentication;

import com.harmony.umbrella.security.SecurityToken;
import com.harmony.umbrella.security.userdetails.SecurityTokenUserDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ValueConstants;

/**
 * @author wuxii
 */
public class SecurityTokenAuthenticationProvider implements AuthenticationProvider {

	private SecurityTokenUserDetailsService securityTokenUserDetailsService;

	public SecurityTokenAuthenticationProvider() {
	}

	public SecurityTokenAuthenticationProvider(SecurityTokenUserDetailsService securityTokenUserDetailsService) {
		this.securityTokenUserDetailsService = securityTokenUserDetailsService;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return SecurityTokenAuthenticationToken.class.isAssignableFrom(authentication);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		SecurityTokenAuthenticationToken authRequest = (SecurityTokenAuthenticationToken) authentication;
		SecurityToken securityToken = authRequest.getSecurityToken();
		UserDetails userDetails = securityTokenUserDetailsService.loadUserBySecurityToken(securityToken);

		SecurityTokenAuthenticationToken authResult = new SecurityTokenAuthenticationToken(
				userDetails.getUsername(),
				userDetails.getPassword(),
				securityToken,
				userDetails.getAuthorities());

		authResult.setAuthenticated(true);
		authResult.setDetails(authRequest.getDetails());

		return authResult;
	}

	public void setSecurityTokenUserDetailsService(SecurityTokenUserDetailsService securityTokenUserDetailsService) {
		this.securityTokenUserDetailsService = securityTokenUserDetailsService;
	}

}
