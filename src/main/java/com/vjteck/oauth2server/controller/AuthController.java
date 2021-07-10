package com.vjteck.oauth2server.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vjteck.oauth2server.model.AccessToken;
import io.swagger.annotations.ApiOperation;

@RestController
public class AuthController {
	
	@Autowired
	private TokenEndpoint tokenEndpoint;
    
	@Autowired
	private TokenStore tokenStore;
	
	@ApiOperation(value = "revoke", notes = "登出时删除有效TOKEN")
	@RequestMapping(method = RequestMethod.POST, value = "/revoke/token")
	public void revokeToken(@RequestBody AccessToken accessToken) {
		String token = accessToken.getAccessToken();
        if(token!=null && token.trim()!=""){
            OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
            if(oAuth2AccessToken != null){
                tokenStore.removeAccessToken(oAuth2AccessToken);
            }
        }
	}
	
	@ApiOperation(value = "getToken", notes = "取得TOKEN")
	@RequestMapping(method = RequestMethod.POST, value = "/oauth/getToken")
	public String getToken() throws HttpRequestMethodNotSupportedException {
		String token = "";
		Map<String,String> param = new HashMap<>();
		param.put("grant_type", "client_credentials");
		
		Set<SimpleGrantedAuthority> set = new HashSet<SimpleGrantedAuthority>();
		set.add(new SimpleGrantedAuthority("ROLE_CLIENT"));
		User pricipal = new User("das", "das", true, true, true, true,set) ;
		
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(pricipal, null,set);
		tokenEndpoint.postAccessToken(usernamePasswordAuthenticationToken,param);
		Collection<OAuth2AccessToken> tokens= tokenStore.findTokensByClientId("das");
		for(OAuth2AccessToken tkn:tokens) {
			token = tkn.getValue();
		}
		return "bearer "+ token;
	}
	
}
