package com.example.spring.stackoverflow.oauth2.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StackoverflowUser
		implements OAuth2User {

	@JsonProperty("items")
	List<Map<String, Object>> items = new ArrayList<>();

	@JsonAnySetter
	Map<String, Object> extraParameters = new HashMap<>();

	@Override
	@JsonIgnore
	public String getName() {
		return String.valueOf(items.get(0).get("account_id"));
	}

	@JsonIgnore
	public String getEmail() {
		return getName() + "@exchange.com";
	}

	@Override
	@JsonIgnore
	public List<GrantedAuthority> getAuthorities() {
		return Arrays.asList(
				new OAuth2UserAuthority("USER", getAttributes()),
				new SimpleGrantedAuthority("USER"));
	}

	@Override
	@JsonIgnore
	public Map<String, Object> getAttributes() {
		return this.getItems().get(0);
	}

	public String toString() {
		return this.getItems().toString();
	}

}
