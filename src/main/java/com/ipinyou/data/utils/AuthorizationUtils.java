package com.ipinyou.data.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.ObjectUtils;

import com.ipinyou.data.entity.AuthorizationEntity;

public class AuthorizationUtils {

	private static AuthorizationEntity auth = null;

	private static void initAuthoritySource() {
		if (ObjectUtils.isEmpty(auth)) {
			auth = new ClassPathXmlApplicationContext("auth/authority.xml")
				.getBean(AuthorizationEntity.class);
		}
	}

	public static AuthorizationEntity getAuths() {
		initAuthoritySource();
		return auth;
	}

	public static boolean authentication(String username, String password) {
		initAuthoritySource();
		return StringUtils.isNotBlank(username)
				&& StringUtils.isNotBlank(password)
				&& auth.getAuths().containsKey(username)
				&& auth.getAuths().get(username).equals(password);
	}

}
