package com.ipinyou.data.utils;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import com.ipinyou.data.service.ApplicationProperties;
import com.ipinyou.data.service.ExcelFilerMaker;

public class MailUtils {

    public static void prepare(ResultSet result, int index, ApplicationProperties properties) {
    	ExcelFilerMaker.createExcel(result, index, properties);
    }

    @SuppressWarnings("restriction")
	public static void send(ApplicationProperties properties) {
    	ExcelFilerMaker.close();
    	Properties props = new Properties();

        props.put("mail.smtp.host", ApplicationProperties.getMailHost());
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);

        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(ApplicationProperties.getMailSender()));
            message.addRecipients(Message.RecipientType.TO, getAddresses(properties.getMailReceiver()));
            message.setSubject(properties.getMailSubject());

            Multipart multipart = new MimeMultipart();

            BodyPart contentPart = new MimeBodyPart();
            String content = properties.getMailContent();
            String mailContent = properties.getSettings().getProperty("mail.template.content");
            if (StringUtils.isNotBlank(mailContent)) {
				content = mailContent;
			}
            contentPart.setContent(content, "text/html;charset=utf-8");
            multipart.addBodyPart(contentPart);
            BodyPart messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(properties.getFileFullPath());
            messageBodyPart.setDataHandler(new DataHandler(source));
            sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
            messageBodyPart.setFileName("=?utf-8?B?"
                    + enc.encode(properties.getCacheFileName().getBytes()) + "?=");
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            message.saveChanges();
            Transport transport = session.getTransport("smtp");
            transport.connect(ApplicationProperties.getMailHost(), ApplicationProperties.getMailAccount(), ApplicationProperties.getMailAccountPassword());
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            File file = new File(ApplicationProperties.getCacheFilePath().concat("/").concat(properties.getFileDir()));
            if (file.exists()) {
				deleteFiles(file);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteFiles(File file) {
    	if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (File file2 : listFiles) {
				deleteFiles(file2);
			}
		}
    	file.delete();
    }

    private static Address[] getAddresses(String addresses) {
	    try {
    		if (!addresses.contains(",")
    				&& !addresses.contains("，")
    				&& !addresses.contains("\n")
    				&& !addresses.contains("\40")
    				&& !addresses.contains("\t")) {
    			if (!addresses.contains("@")) {
					addresses = addresses.concat("@ipinyou.com");
				}
				return new Address[]{new InternetAddress(addresses)};
			}
    		String[] split = addresses.split("[,，\n\40\t]");
    		Address[] addrs = new Address[split.length];
    		for (int i = 0; i < split.length; i++) {
				String address = split[i];
				if (!address.contains("@")) {
					address = address.concat("@ipinyou.com");
				}
				addrs[i] = new InternetAddress(address);
			}
    		return addrs;
	    } catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;
    }

    public static LinkedHashMap<Integer, ArrayList<String>> processResultSet(ResultSet result) {
    	LinkedHashMap<Integer, ArrayList<String>> collection = new LinkedHashMap<Integer, ArrayList<String>>();
		try {
			ResultSetMetaData metaData = result.getMetaData();
			int columnCount = metaData.getColumnCount();
			while(result.next()) {
				for (int i = 1; i <= columnCount; i++) {
					if (collection.containsKey(i)) {
						collection.get(i).add(String.valueOf(result.getObject(i)));
						continue;
					}
					ArrayList<String> arrayList = new ArrayList<String>();
					arrayList.add(String.valueOf(result.getObject(i)));
					collection.put(i, arrayList);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return collection;
    }

    public static String calculateEquation(String equation) {
    	if (StringUtils.isBlank(equation) || equation.indexOf(".") != equation.lastIndexOf(".")) {
			return "";
		}
    	String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’，、？]";
    	Pattern p = Pattern.compile(regEx);
    	Matcher m = p.matcher(equation);
    	if (!m.find()) {
			return equation;
		}
    	ScriptEngine se = new ScriptEngineManager().getEngineByName("JavaScript");
		try {
			Object eval = se.eval(equation.trim());
	    	return String.valueOf(new BigDecimal(String.valueOf(eval)));
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
    }

    public static String numberFormat(String num) {
    	char[] charArray = num.toCharArray();
    	StringBuilder builder = new StringBuilder();
    	boolean hasPoint = false;
    	int pos = 0;
    	StringBuilder last = new StringBuilder("0.");
    	for (char c : charArray) {
			if (c == '.') {
				hasPoint = true;
			} else if (hasPoint && c != '0') {
				pos++;
				
			} else if (hasPoint && pos > 0) {
				pos++;
			}
			if (hasPoint && c != '.') {
				last.append("0");
			}
			if (pos == 5) {
				last.delete(last.length() - 2, last.length());
				if (Integer.valueOf(String.valueOf(c)) > 4) {
					last.append("1");
				}
				break;
			}
			builder.append(c);
		}
    	if (pos == 5) {
    		BigDecimal add = new BigDecimal(builder.toString()).add(new BigDecimal(last.toString()));
    		return String.valueOf(add);
    	}
    	return builder.toString();
    }
public static void main(String[] args) {
	String equation = calculateEquation("5000/(1000*60*60*24)");
	System.out.println(equation);
	String format = numberFormat(equation);
	System.out.println(format);
}
}