package net.mmp.center.webapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private WebAuthenticationProvider provider;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(provider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests()
//				.antMatchers("/static/**").permitAll()
//				.antMatchers("/**").hasAnyRole("ADMIN", "USER")
//				.anyRequest().authenticated()
		
		http.authorizeRequests()
			.antMatchers("/**").hasAnyRole("ADMIN")
			.anyRequest().authenticated()
		.and()
			.formLogin()
				.loginPage("/login")
				.loginProcessingUrl("/authenticate")
				.failureUrl("/login?error")
				.defaultSuccessUrl("/")
				.usernameParameter("id")
				.passwordParameter("pwd")
				.permitAll()
		.and()
			.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login")
				.permitAll()
		.and()
			.csrf()
				.disable();
	}
	
	@Bean // Role의 Prefix 설정. 기본값 `ROLE_` 제거
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults("");
	}

}
