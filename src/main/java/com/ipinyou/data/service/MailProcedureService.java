package com.ipinyou.data.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;

import com.ipinyou.data.utils.MailUtils;

public class MailProcedureService {

	private Statement statement;

	public boolean autoConduct(String sql, ApplicationProperties properties) {
		if (StringUtils.isBlank(sql)) {
			return false;
		}
		Statement statement = getStatement();
		if (ObjectUtils.isEmpty(statement)) {
			return false;
		}
		String[] sqls = sql.split("[\n]");
		for (int i = 0; i < sqls.length; i++) {
			ResultSet result = getExeResult(statement, sqls[i]);
			if (ObjectUtils.isEmpty(result)) {
				return false;
			}
			MailUtils.prepare(result, i, properties);
		}
		MailUtils.send(properties);
		return true;
	}

	private Statement getStatement() {
		ApplicationProperties.webInit();
		if (!ObjectUtils.isEmpty(statement)) {
			return statement;
		}
		try {
			Class.forName(ApplicationProperties.getDriverName());
			Connection conn = DriverManager.getConnection(ApplicationProperties.getDbURL(), ApplicationProperties.getDbUsername(),
					ApplicationProperties.getDbPassword());
			statement = conn.createStatement();
			return statement;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static ResultSet getExeResult(Statement stmt, String sql) {
		try {
			return stmt.executeQuery(sql.trim());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet getExecutiveResult(String sql) {
		Statement statement = getStatement();
		String[] splitSQL = sql.replaceAll(";;", ";").split("[;]");
		try {
			for (int i = 0; i < splitSQL.length - 1; i++) {
				statement.execute(splitSQL[i]);
			}
			return statement.executeQuery(splitSQL[splitSQL.length - 1]);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
