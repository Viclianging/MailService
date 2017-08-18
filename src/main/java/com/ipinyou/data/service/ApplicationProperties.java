package com.ipinyou.data.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import com.ipinyou.data.exception.WebMailServiceException;
import com.ipinyou.data.swing.InteractionInterface;
import com.ipinyou.data.utils.BaseUtils;
import com.ipinyou.data.utils.I18nResourceUtil;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

public class ApplicationProperties {

	private static final Properties application_settings = new Properties();
	private Properties settings = new Properties();
	private List<String> sheetTitles = new ArrayList<String>();
//	private static Properties en = new Properties();
//	private static Properties zh_CN = new Properties();

	public ApplicationProperties(boolean bool) {
		appInit();
		if (bool) {
			ApplicationProperties.webInit();
		} else {
			ApplicationProperties.disInit();
		}
	}

	public void appInit() {
		try {
			InputStream settingsResource = ApplicationProperties.class.getResourceAsStream("/settings/settings.properties");
			settings.load(BaseUtils.getReader(settingsResource));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void webInit() {
		try {
			InputStream applicationResource = ApplicationProperties.class.getResourceAsStream("/settings/application-settings.properties");
			application_settings.load(BaseUtils.getReader(applicationResource));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void disInit() {
		try {
			InputStream applicationResource = ApplicationProperties.class.getResourceAsStream("/settings/application-settings.properties");
			application_settings.load(BaseUtils.getReader(applicationResource));
			InteractionInterface.perform();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public static void init() {
//		try {
//			InputStream applicationResource = ApplicationProperties.class.getResourceAsStream("/settings/application-settings.properties");
//			InputStream settingsResource = ApplicationProperties.class.getResourceAsStream("/settings/settings.properties");
//			application_settings.load(BaseUtils.getReader(applicationResource));
//			settings.load(BaseUtils.getReader(settingsResource));
//			if (StringUtils.isNotBlank(settings.getProperty("mail.template.content.body"))) {
//
//				InputStream enResource = new FileInputStream(BaseUtils.getI18nSourceFilePath("properties_en.properties").substring(6));
//				InputStream zhResource = new FileInputStream(BaseUtils.getI18nSourceFilePath("properties_zh_CN.properties").substring(6));
//				en.load(BaseUtils.getReader(enResource));
//				zh_CN.load(BaseUtils.getReader(zhResource));
//
//				if (settings.getProperty("mail.template.content.body").equals(en.getProperty("mail.body"))
//						&& settings.getProperty("mail.template.content.body").equals(zh_CN.getProperty("mail.body"))) {
//					return;
//				}
//
//				FileOutputStream en_File = new FileOutputStream(new File(BaseUtils.getI18nSourceFilePath("properties_en.properties").substring(6)));
//				FileOutputStream zh_CN_File = new FileOutputStream(new File(BaseUtils.getI18nSourceFilePath("properties_zh_CN.properties").substring(6)));
//
//				en.setProperty("mail.body", settings.getProperty("mail.template.content.body"));
//				zh_CN.setProperty("mail.body", settings.getProperty("mail.template.content.body"));
//
//				en.store(en_File, null);
//				zh_CN.store(zh_CN_File, null);
//				en_File.flush();
//				zh_CN_File.flush();
//				en_File.close();
//				zh_CN_File.close();
//
//				Runtime.getRuntime().exec("cmd /c start ".concat(BaseUtils.getRootPath()).concat("bin/exec.cmd"));
//
////				Thread.sleep(2000);
//				System.gc();
//				System.exit(0);
////				BaseUtils.detect();
//				
////				JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(new File(BaseUtils.getJarPath())));
////				JarEntry jarEntry = new JarEntry("i18n/properties_en.properties");
////				jarOutputStream.setMethod(JarOutputStream.DEFLATED);
////				jarOutputStream.putNextEntry(jarEntry);
////				Enumeration<Object> keys = en.keys();
////				while(keys.hasMoreElements()) {
////					String key = String.valueOf(keys.nextElement());
////					jarOutputStream.write((key.concat(":").concat(en.getProperty(key)).concat("\n")).getBytes());
////				}
////				jarOutputStream.write(("mail.body".concat(":").concat(settings.getProperty("mail.template.content.body")).concat("\n")).getBytes());
////				jarOutputStream.flush();
////				jarOutputStream.finish();
////				jarOutputStream.close();
////				
////				JarOutputStream jarOutputStream2 = new JarOutputStream(new FileOutputStream(new File(BaseUtils.getJarPath())));
////				JarEntry jarEntry2 = new JarEntry("i18n/properties_zh_CN.properties");
////				jarOutputStream2.setMethod(JarOutputStream.DEFLATED);
////				jarOutputStream2.putNextEntry(jarEntry2);
////				Enumeration<Object> keys2 = zh_CN.keys();
////				while(keys2.hasMoreElements()) {
////					String key = String.valueOf(keys2.nextElement());
////					jarOutputStream2.write((key.concat(":").concat(zh_CN.getProperty(key)).concat("\n")).getBytes());
////				}
////				jarOutputStream2.write(("mail.body".concat(":").concat(settings.getProperty("mail.template.content.body")).concat("\n")).getBytes());
////				jarOutputStream2.flush();
////				jarOutputStream2.finish();
////				jarOutputStream2.close();
////				FileOutputStream en_File = new FileOutputStream(new File(ApplicationProperties.class.getResource("/i18n/properties_en.properties").getPath()));
////				FileOutputStream zh_CN_File = new FileOutputStream(new File(ApplicationProperties.class.getResource("/i18n/properties_zh_CN.properties").getPath()));
////				en.setProperty("mail.body", settings.getProperty("mail.template.content.body"));
////				zh_CN.setProperty("mail.body", settings.getProperty("mail.template.content.body"));
////				en.store(en_File, null);
////				zh_CN.store(zh_CN_File, null);
////				en_File.flush();
////				zh_CN_File.flush();
////				en_File.close();
////				zh_CN_File.close();
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public static String getDriverName() {
		return application_settings.getProperty("driver.name");
	}

	public static String getDbURL() {
		return application_settings.getProperty("db.url");
	}

	public static String getDbUsername() {
		return application_settings.getProperty("db.username");
	}

	public static String getDbPassword() {
		return application_settings.getProperty("db.password");
	}

	public Properties getSettings() {
		return settings;
	}

	public static String getCacheFilePath() {
		String filePathProperty = application_settings.getProperty("CacheFilePath");
		String envProperty = application_settings
				.getProperty("QuoteSystemEnvAsCacheFilePath");
		if (StringUtils.isNotBlank(envProperty) && StringUtils.isBlank(filePathProperty)) {
			envProperty = verifySystemJdkEnv(envProperty);
		}
		return (StringUtils.isNotBlank(filePathProperty) ? filePathProperty
				: envProperty).concat("/mail-resource-site-package");
	}

	public String getFileDir() {
		return settings.getProperty("file.dir");
	}

	public String getCacheFileName() {
		String fileNameProperty = settings.getProperty("CacheFileName");
		fileNameProperty = StringUtils.isNotBlank(fileNameProperty) ? fileNameProperty : "serviceReport.xls";
		return fileNameProperty.contains(".") ? fileNameProperty : fileNameProperty.concat(".xls");
	}

	public String getFileFullPath() {
		return getCacheFilePath().concat("/").concat(this.getFileDir()).concat("/").concat(getCacheFileName());
	}

	public static String getMailHost() {
		String mailHostProperty = application_settings.getProperty("mail.host");
		return StringUtils.isNotBlank(mailHostProperty) ? mailHostProperty
				: "smtp.qiye.163.com";
	}

	public static String getMailAccount() {
		String mailAccountProperty = application_settings.getProperty("mail.account");
		return StringUtils.isNotBlank(mailAccountProperty) ? mailAccountProperty
				: "data_warning@ipinyou.com";
	}

	public static String getMailAccountPassword() {
		String mailPasswordProperty = application_settings.getProperty("mail.password");
		return StringUtils.isNotBlank(mailPasswordProperty) ? mailPasswordProperty
				: "Data1qa2ws";
	}

	public static String getMailSender() {
		return application_settings.getProperty("mail.from");
	}

	public String getMailReceiver() {
		return settings.getProperty("mail.to");
	}

	public String getMailSubject() {
		return settings.getProperty("mail.subject");
	}

	public String getMailContent() {
		String contentProperty = settings.getProperty("mail.content");
		if (StringUtils.isNotBlank(contentProperty)) {
			return contentProperty;
		}
		return generateEmailRef("template.ftl");
	}

	public String handleSQL(String sql) {
		if (StringUtils.isBlank(sql)) {
			sql = settings.getProperty("sql");
		}
		return getFileContent("/sql/sql.sql", sql);
	}

	public String getSheetTitle(int index) {
		if (sheetTitles.size() <= index) {
			return "Report Data";
		}
		return sheetTitles.get(index).trim();
	}

	private void initSheetTitles() {
		String titles = settings.getProperty("mail.attachment.sheets");
		if (StringUtils.isNotBlank(titles)) {
			sheetTitles.clear();
			String[] titlesStr = titles.split("[,，]");
			for (String title : titlesStr) {
				sheetTitles.add(title.trim());
			}
		}
	}

	private void setSheetTitles(int index, String title) {
		if (sheetTitles.size() <= index) {
			sheetTitles.add(title);
		}
	}

	private String getFileContent(String file, String sql) {
		initSheetTitles();
		try {
			String cache = null;
			StringBuilder builder = new StringBuilder();
			if (StringUtils.isNotBlank(cache = sql)) {
				String[] split = cache.split("[\n]");
				operateSql(split, cache, builder, null);
				return builder.toString();
			}
			InputStream ip = ApplicationProperties.class.getResourceAsStream(file);
			InputStreamReader ips = new InputStreamReader(ip);
			BufferedReader reader = new BufferedReader(ips);
			operateSql(null, cache, builder, reader);
			reader.close();
			ips.close();
			ip.close();
			return builder.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private void operateSql(String[] split, String cache, StringBuilder builder, BufferedReader reader) throws IOException {
		for (int i = 0, j = 0; (!ObjectUtils.isEmpty(reader) && StringUtils.isNotBlank(cache = reader.readLine()))
				|| (!ObjectUtils.isEmpty(split) && j < split.length && StringUtils.isNotBlank(cache = split[j])); j++) {
			if (cache.startsWith("--")) {
				setSheetTitles(i++, cache.substring(2).trim().replaceAll("-", ""));
				continue;
			}
			if (cache.contains(";")) {
				builder.append(" ".concat(cache.substring(0, cache.lastIndexOf(";"))));
				builder.append("\n");
				continue;
			}
			builder.append(" ".concat(cache));
		}
	}

	public String getExcelTitles(int index) {
		if (settings.containsKey("TITLE")) {
			String[] split = (String[])settings.get("TITLE");
			return index >= split.length ? null : StringUtils.isNotBlank(split[index])
					? split[index] : null;
		}
		String property = settings.getProperty("excel.titles");
		if (StringUtils.isNotBlank(property)) {
			String[] split = property.split("[,，]");
			settings.put("TITLE", split);
			return index >= split.length ? null : StringUtils.isNotBlank(split[index])
					? split[index] : null;
		}
		return null;
	}

	private static String verifySystemJdkEnv(String key) {
		Map<String, String> sysenvs = System.getenv();
		if (!sysenvs.containsKey(key)) {
			throw new WebMailServiceException("[error:0] System is short of system-variable '" + key +"' for requirement.");
		}
		return sysenvs.get(key);
	}

	private String generateEmailRef(String templateFileName) {
		Configuration cfg = new Configuration(new Version("2.3"));
		cfg.setDefaultEncoding("utf-8");
		cfg.setObjectWrapper(new DefaultObjectWrapper(new Version("2.3")));
		cfg.setTemplateLoader(new ClassTemplateLoader(ApplicationProperties.class, "/template/"));
		StringWriter writer = new StringWriter();
		try {
			Template template = cfg.getTemplate(templateFileName);
			settings.put("address_sb", I18nResourceUtil.getResource("mail.address_sb"));
			settings.put("body", I18nResourceUtil.getResource("mail.body"));
			settings.put("tailer", I18nResourceUtil.getResource("mail.tailer"));
			settings.put("ending", I18nResourceUtil.getResource("mail.ending"));
			settings.put("link", I18nResourceUtil.getResource("mail.link"));
			template.process(settings, writer);
			return writer.toString();
		} catch (TemplateException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		return "";
	}

	public static String disHTMLMailContent(String content) {
		Configuration cfg = new Configuration(new Version("2.3"));
		cfg.setDefaultEncoding("utf-8");
		cfg.setObjectWrapper(new DefaultObjectWrapper(new Version("2.3")));
		cfg.setTemplateLoader(new ClassTemplateLoader(ApplicationProperties.class, "/template/"));
		StringWriter writer = new StringWriter();
		try {
			Template template = cfg.getTemplate("displayTemplate.ftl");
			HashMap<String,String> root = new HashMap<String, String>();
			root.put("disContent", content);
			template.process(root, writer);
			return writer.toString();
		} catch (TemplateException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		return "";
	}

	public void setProperties(String fileName, String mailTo, String subject, String titles, String content, String sql) {
    	if (StringUtils.isNotBlank(fileName) && !fileName.equals(I18nResourceUtil.getResource("text.attachmentfile.name"))) {
    		if (fileName.contains(".")) {
				fileName = fileName.substring(0, fileName.lastIndexOf("."));
			}
    		fileName.concat(".xls");
    		MessageFormat mf = new MessageFormat(fileName, new Locale("zh", "CN"));
    		settings.setProperty("CacheFileName", mf.toPattern());
		}
    	if (StringUtils.isNotBlank(titles) && !titles.startsWith("excel")) {
    		settings.setProperty("excel.titles", titles);
    	}
    	if (StringUtils.isNotBlank(content) && !content.equals(I18nResourceUtil.getResource("text.mail.content"))) {
    		String htmlText = BaseUtils.toHTMLText(content);
    		String disHTMLMailContent = ApplicationProperties.disHTMLMailContent(htmlText);
    		settings.setProperty("mail.template.content", disHTMLMailContent);
    	}
    	settings.setProperty("mail.to", mailTo);
    	settings.setProperty("mail.subject", subject);
    	settings.setProperty("sql", sql);
    	settings.setProperty("file.dir", String.valueOf(System.currentTimeMillis()));
	}

}
