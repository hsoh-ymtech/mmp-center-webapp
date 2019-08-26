package net.mmp.center.webapp.security;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
        .csrf()
        .disable()
        .antMatcher("/**")
        .authorizeRequests()
//        .antMatchers("/", "/main","/login", "/resources/static/**", "/resources/public/**", 
//        																									"/runtime.js",
//        																									"/polyfills.js",
//        																									"/styles.js",
//        																									"/vendor.js",
//        																									"/main.js",
//        																									"/assets/**",
//        																									"/Roboto-Medium.ttf",
//        																									"/Roboto-Bold.ttf",
//        																									"/icon_login.png",
//        																									"/favicon.ico")
        .antMatchers("/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .logout()
        	.logoutUrl("/logout")
            .logoutSuccessUrl("/")
        .invalidateHttpSession(true)
        .clearAuthentication(true)
        .permitAll();
		

	}
	

}
