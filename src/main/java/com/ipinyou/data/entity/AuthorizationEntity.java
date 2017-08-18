package com.ipinyou.data.entity;

import java.util.Map;

public class AuthorizationEntity {

	private Map<String, String> auths;

	public Map<String, String> getAuths() {
		return this.auths;
	}

	public void setAuths(Map<String, String> auths) {
		this.auths = auths;
	}

//	public void setAuths(String...auths) {
//		if (ObjectUtils.isEmpty(auths)) {
//			return;
//		}
//		for (String auth : auths) {
//			if (StringUtils.isBlank(auth)) {
//				continue;
//			}
//			if (!auth.contains("@")) {
//				auth = auth.trim().concat("@ipinyou.com");
//			}
//			this.auths.add(auth.trim());
//		}
//	}

}
