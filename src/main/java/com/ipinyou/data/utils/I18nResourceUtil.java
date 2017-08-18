package com.ipinyou.data.utils;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.ObjectUtils;

public class I18nResourceUtil {
	private static MessageSource messageSource = null;

	private static void initMessageSource() {
		if (ObjectUtils.isEmpty(messageSource)) {
			messageSource = new ClassPathXmlApplicationContext("spring/i18nresource.xml")
						.getBean(ResourceBundleMessageSource.class);
		}
	}

	public static String getResource(String code, Locale locale) {
		initMessageSource();
		if (!ObjectUtils.isEmpty(messageSource)) {
			return messageSource.getMessage(code, null, locale);
		} else {
			return null;
		}
	}

	public static String getResource(String code, Object[] args, Locale locale) {
		initMessageSource();
		if (!ObjectUtils.isEmpty(messageSource)) {
			return messageSource.getMessage(code, args, locale);
		} else {
			return null;
		}
	}

	public static String getResource(String code, Locale locale, Object... args) {
		return getResource(code, args, locale);
	}

	public static String getResource(String code, Locale locale,
			String defaultValue) {
		initMessageSource();
		if (!ObjectUtils.isEmpty(messageSource)) {
			return messageSource.getMessage(code, null, defaultValue, locale);
		} else {
			return defaultValue;
		}
	}

	public static String getResource(String code, Object[] args, Locale locale,
			String defaultValue) {
		initMessageSource();
		if (!ObjectUtils.isEmpty(messageSource)) {
			return messageSource.getMessage(code, args, defaultValue, locale);
		} else {
			return defaultValue;
		}
	}

	public static String getResource(String code) {
		initMessageSource();
		if (!ObjectUtils.isEmpty(messageSource)) {
			return messageSource.getMessage(code, null,
					Locale.SIMPLIFIED_CHINESE);
		} else {
			return null;
		}
	}

	public static String getResource(String code, Object... args) {
		initMessageSource();
		if (!ObjectUtils.isEmpty(messageSource)) {
			return messageSource.getMessage(code, args,
					Locale.SIMPLIFIED_CHINESE);
		} else {
			return null;
		}
	}

	public static String getResource(String code, String defaultValue) {
		initMessageSource();
		if (!ObjectUtils.isEmpty(messageSource)) {
			return messageSource.getMessage(code, null, defaultValue,
					Locale.SIMPLIFIED_CHINESE);
		} else {
			return defaultValue;
		}
	}

	public static String getResource(String code, Object[] args,
			String defaultValue) {
		initMessageSource();
		if (!ObjectUtils.isEmpty(messageSource)) {
			return messageSource.getMessage(code, args, defaultValue,
					Locale.SIMPLIFIED_CHINESE);
		} else {
			return defaultValue;
		}
	}

}
