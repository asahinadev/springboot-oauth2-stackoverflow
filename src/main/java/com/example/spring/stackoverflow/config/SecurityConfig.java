package com.example.spring.stackoverflow.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.CustomUserTypesOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DelegatingOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestTemplate;

import com.example.spring.oauth2.CustomOAuth2AccessTokenResponseHttpMessageConverter;
import com.example.spring.oauth2.CustomOAuth2UserRequestEntityConverter;
import com.example.spring.oauth2.LoggingClientHttpRequestInterceptor;
import com.example.spring.stackoverflow.oauth2.user.StackoverflowUser;

@Configuration
@EnableWebSecurity
public class SecurityConfig
		extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web)
			throws Exception {
		super.configure(web);

		web.ignoring().antMatchers(
				// webjars
				"/webjars/**",
				// CSS ファイル
				"/css/**",
				// JavaScriptファイル
				"/js/**",
				// 画像ファイル
				"/img/**",
				// サウンドファイル
				"/sound/**",
				// WEB フォント
				"/font/**",
				"/fonts/**",
				// 外部ライブラリ
				"/exlib/**"
		/**/
		);
	}

	@Override
	protected void configure(HttpSecurity http)
			throws Exception {
		super.configure(http);

		http.formLogin().disable();
		http.logout().disable();

		http.httpBasic().disable();

		http.csrf().disable();

		http.oauth2Login()

				// アクセストークンエンドポイント
				.tokenEndpoint()
				.accessTokenResponseClient(accessTokenResponseClient())
				.and()

				// ユーザー情報エンドポイント
				.userInfoEndpoint()
				.userService(oauth2UserService());

	}

	OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
		DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();

		RestTemplate restTemplate = new RestTemplate(Arrays.asList(
				new FormHttpMessageConverter(),
				new CustomOAuth2AccessTokenResponseHttpMessageConverter()));

		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		restTemplate.setInterceptors(Arrays.asList(new LoggingClientHttpRequestInterceptor()));

		client.setRestOperations(restTemplate);

		return client;

	}

	OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		restTemplate.setInterceptors(Arrays.asList(new LoggingClientHttpRequestInterceptor()));

		OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;

		Map<String, Class<? extends OAuth2User>> customUserTypes = new HashMap<>();
		customUserTypes.put("stackoverflow", StackoverflowUser.class);

		List<OAuth2UserService<OAuth2UserRequest, OAuth2User>> userServices = new ArrayList<>();

		CustomUserTypesOAuth2UserService customUserService = new CustomUserTypesOAuth2UserService(customUserTypes);
		customUserService.setRequestEntityConverter(new CustomOAuth2UserRequestEntityConverter());
		customUserService.setRestOperations(restTemplate);

		DefaultOAuth2UserService defaultUserService = new DefaultOAuth2UserService();
		defaultUserService.setRequestEntityConverter(new CustomOAuth2UserRequestEntityConverter());
		defaultUserService.setRestOperations(restTemplate);

		userServices.add(customUserService);
		userServices.add(defaultUserService);

		oauth2UserService = new DelegatingOAuth2UserService<>(userServices);

		return oauth2UserService;
	}
}
