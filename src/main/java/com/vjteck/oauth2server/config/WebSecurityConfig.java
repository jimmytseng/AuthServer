package com.vjteck.oauth2server.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * <p>Title: WebSecurityConfig</p>
 *
 * <p>Description: mvc configuration</p>
 *
 * <p>Copyright: Copyright (c) jimmytseng 2021</p>
 *
 * <p>Company: VJTeck</p>
 *
 * @author zouxuanmin
 * @version 1.0
 * @date 2018/9/25 14:44
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private DataSource dataSource;

    public WebSecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @Bean("userDetailsService")
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean(); //default
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(this.dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("SELECT  custId as username, pwd AS password , 'true' as enabled  FROM CUST where custId= ?")
                .authoritiesByUsernameQuery("select ? AS username, 'ROLE_USER' as authority FROM CUST where 1=1");
        
    }
    
    @Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatchers()
		       .antMatchers("/revoke/token")
		       .and()
		       .authorizeRequests().anyRequest().permitAll()
		       .and()
			   .httpBasic()
			   .and()
			   .csrf().disable();
	}
    
    public PasswordEncoder passwordEncoder(){
    	PasswordEncoder encoder =  NoOpPasswordEncoder.getInstance();
//    	switch("noop"){
//        	case "noop":
//        		encoder = NoOpPasswordEncoder.getInstance();
//    		    break;
//    		default :	
//    			encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder() ;
//    	}
        return encoder;
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
