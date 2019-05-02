package com.example.spring.oauth2;

import java.net.URI;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class CustomOAuth2UserRequestEntityConverter
		extends OAuth2UserRequestEntityConverter {

	@Override
	public RequestEntity<?> convert(OAuth2UserRequest userRequest) {
		ClientRegistration clientRegistration = userRequest.getClientRegistration();
		AuthenticationMethod authenticationMethod = clientRegistration.getProviderDetails().getUserInfoEndpoint()
				.getAuthenticationMethod();

		HttpMethod httpMethod = HttpMethod.GET;
		if (AuthenticationMethod.FORM.equals(authenticationMethod)) {
			httpMethod = HttpMethod.POST;
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		URI uri = UriComponentsBuilder
				.fromUriString(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
				.build()
				.toUri();

		RequestEntity<?> request;
		if (AuthenticationMethod.FORM.equals(authenticationMethod)) {
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> formParameters = new LinkedMultiValueMap<>();
			formParameters.add(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue());

			request = new RequestEntity<>(formParameters, headers, httpMethod, uri);
		} else if (AuthenticationMethod.QUERY.equals(authenticationMethod)) {
			uri = UriComponentsBuilder
					.fromUri(uri)
					.queryParam(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue())
					.build().toUri();

			request = new RequestEntity<>(headers, httpMethod, uri);
		} else {
			headers.setBearerAuth(userRequest.getAccessToken().getTokenValue());
			request = new RequestEntity<>(headers, httpMethod, uri);
		}

		return request;
	}
}