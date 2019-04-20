package com.example.spring.stackoverflow.oauth2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties("app.oauth2.stackoverflow")
public class StackoverflowProperties {

	String key;
	String order;
	String sort;
	String site;

}
