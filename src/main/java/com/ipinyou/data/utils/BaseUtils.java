package com.ipinyou.data.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import com.ipinyou.data.exception.WebMailServiceException;

public class BaseUtils {

	public static BufferedReader getReader(InputStream stream) {
		return new BufferedReader(new InputStreamReader(stream));
	}

	public static String getRootPath() {
		String jarPath = getJarPath();
		return jarPath.substring(0, jarPath.indexOf("mail"));
	}

	public static String getJarPath() {
		URL resource = BaseUtils.class.getResource("/i18n");
		String path = resource.getPath();
		int lastIndexOf = path.lastIndexOf("!/i18n");
		String jarPath = path.substring(0, lastIndexOf);
		return jarPath;
	}

	public static String getI18nSourceFilePath(String fileName) {
		return getRootPath().concat("i18n/").concat(fileName);
	}

	public static void detect() {
		int count = 0;
		try {
			while (count++ < 6) {
				Thread.sleep(12000);
				File file = new File(getRootPath().concat("complete"));
				if (file.exists()) {
					file.delete();
					count = -1;
					break;
				}
			}
			if (count > 6) {
				throw new WebMailServiceException("[error:0] System has occured a malfunction as it runs the shell/cmd script-text which assigned by developer.");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String toHTMLText(String content) {
		if (StringUtils.isBlank(content)) {
			return "";
		}
		return content.replaceAll("\n", "<br/>").replaceAll("\40", "&nbsp;").replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
	}

	public static SystemC getSystemType() {
		String property = System.getProperty("os.name");
		if (property.toLowerCase().startsWith("win")) {
			return SystemC.Windows;
		} else if (property.toLowerCase().startsWith("mac")) {
			return SystemC.MacOs;
		} else if (property.toLowerCase().contains("cent")
				|| property.toLowerCase().contains("ubuntu")
				|| property.toLowerCase().contains("linux")) {
			return SystemC.Linux;
		} else {
			return SystemC.Unknow;
		}
	}

	public static enum SystemC {
		Windows, Linux, MacOs, Unknow
	}

}
