package com.vjteck.oauth2server.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * <p>Title: Oauth2AuthorizationServerConfig</p>
 *
 * <p>Description: oauth2 config</p>
 *
 * <p>Copyright: Copyright (c) jimmytseng 2021</p>
 *
 * <p>Company: vjteck</p>
 *
 * @author jimmytseng
 * @version 1.0
 * @date 2021/07/10 
 */
@Configuration
@EnableAuthorizationServer
public class Oauth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    private AuthenticationManager authenticationManager;

    private RedisConnectionFactory factory;
    
    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    public Oauth2AuthorizationServerConfig(AuthenticationManager authenticationManager, RedisConnectionFactory factory) {
        this.authenticationManager = authenticationManager;
        this.factory = factory;
    }

    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(factory);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("vjteck")
                .resourceIds(Oauth2ResourceConfig.RESOURCE_ID)
                .secret("{noop}secret")
                .scopes("read,write")
                .authorizedGrantTypes("client_credentials","password", "refresh_token")
                .authorities("ROLE_CLIENT")
                .accessTokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(100))
                .refreshTokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(100 * 2));
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(authenticationManager)
        		.accessTokenConverter(accessTokenConverter())
                .tokenStore(tokenStore())
        		.userDetailsService(userDetailsService);
    }
    
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(Oauth2ResourceConfig.SIGNINGKEY);
        return converter;
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }
}