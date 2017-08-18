package com.ipinyou.data.controller;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipinyou.data.controller.LoginController.UserSecure;
import com.ipinyou.data.service.ApplicationProperties;
import com.ipinyou.data.service.MailProcedureService;
import com.ipinyou.data.utils.I18nResourceUtil;
import com.ipinyou.data.utils.MailTaskJobRobot;
import com.ipinyou.data.utils.MailUtils;

@Controller
@RequestMapping(value = "/mail")
public class MailHandlerController {

	@RequestMapping(value = "/pre-service", method = RequestMethod.GET)
	public String preview(HttpServletRequest request, HttpServletResponse response) {
		if (!this.authentication(request, response)) {
			return "redirect:../login/index";
		}
		MailProcedureService procedure = new MailProcedureService();
		ResultSet resultSet = procedure.getExecutiveResult("show databases");
		LinkedHashMap<Integer,ArrayList<String>> processResultSet = MailUtils.processResultSet(resultSet);
		request.setAttribute("databases", processResultSet.get(1));
		this.foundamentalLabel(request);
		return "main.jsp";
	}

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/", method = RequestMethod.POST, produces = "text/plain; charset=utf-8")
	public String mailTaskProcedure(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "attachment", required = true)String attachment,
			@RequestParam(value = "receiver", required = true)String receiver,
			@RequestParam(value = "subject", required = true)String subject,
			@RequestParam(value = "content", required = true)String content,
			@RequestParam(value = "taskDate", required = true)String taskDate,
			@RequestParam(value = "date_frequency", required = true)String date_frequency,
			@RequestParam(value = "date_repeat", required = true)String date_repeat,
			@RequestParam(value = "frequency", required = true)String frequency,
			@RequestParam(value = "repeat", required = true)String repeat,
			@RequestParam(value = "sql", required = true)String sql) {

		HashMap<String,Object> userSecure = this.getUserSecure(request);
		if (ObjectUtils.isEmpty(userSecure)) {
			return I18nResourceUtil.getResource("prompt.task.fail");
		}
		String username = String.valueOf(userSecure.get(UserSecure._security_.name())).split("/")[0];
		HashMap<String, Object> blockInterspace = (HashMap<String, Object>)userSecure.get(username);
		if (!blockInterspace.containsKey("taskList")) {
			blockInterspace.put("taskList", new ArrayList<LinkedHashMap<String, Object>>());
		}
		ArrayList<LinkedHashMap<String, Object>> taskList = (ArrayList<LinkedHashMap<String, Object>>)blockInterspace.get("taskList");
		if (taskList.size() == 5) {
			return I18nResourceUtil.getResource("prompt.task.fail.out_limit");
		}
		ApplicationProperties properties = new ApplicationProperties(true);
		LinkedHashMap<String, Object> task = new LinkedHashMap<String, Object>();
		taskList.add(task);
		properties.setProperties(attachment, receiver, subject, null, content, sql);
		MailTaskJobRobot robot = (MailTaskJobRobot)blockInterspace.get("robot");
		if (ObjectUtils.isEmpty(robot)) {
			robot = new MailTaskJobRobot();
			blockInterspace.put("robot", robot);
		}
		MailProcedureService procedure = new MailProcedureService();
		Long poolDateTask_no = 0L;
		String handleSQL = properties.handleSQL(null);
		if (StringUtils.isNotBlank(taskDate) && StringUtils.isNotBlank(date_frequency)) {
			robot.setRunnable(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					procedure.autoConduct(handleSQL, properties);
				}
			});
			if ("y".equals(date_repeat)) {
				poolDateTask_no = robot.poolDateTask(taskDate, date_frequency);
			} else {
				poolDateTask_no = robot.poolOnce(taskDate);
			}
			String format = MailUtils.numberFormat(MailUtils.calculateEquation(date_frequency));
			String frequency_name = date_frequency;
			if (isEquation(date_frequency)) {
				frequency_name = "[".concat(date_frequency).concat("]=").concat(format);
			}
			task.put(UserBlockInterspaceStruction.task_date.name(), taskDate);
			task.put(UserBlockInterspaceStruction.date_frequency.name(),
					frequency_name.concat("(" + I18nResourceUtil.getResource("label.suffix.date.frequency") + ")"));
			task.put(UserBlockInterspaceStruction.date_repeat.name(), date_repeat);
			task.put(UserBlockInterspaceStruction.frequency.name(), "-");
			task.put(UserBlockInterspaceStruction.repeat.name(), "-");
		} else if (StringUtils.isNotBlank(frequency)) {
			robot.setRunnable(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					procedure.autoConduct(handleSQL, properties);
				}
			});
			if ("y".equals(repeat)) {
				poolDateTask_no = robot.poolFrequencyTask(frequency);
			} else {
				poolDateTask_no = robot.poolOnce(Long.valueOf(frequency));
			}
			String format = MailUtils.numberFormat(MailUtils.calculateEquation(frequency));
			String frequency_name = frequency;
			if (isEquation(frequency)) {
				frequency_name = "[".concat(frequency).concat("]=").concat(format);
			}
			task.put(UserBlockInterspaceStruction.task_date.name(), "-");
			task.put(UserBlockInterspaceStruction.date_frequency.name(), "-");
			task.put(UserBlockInterspaceStruction.date_repeat.name(), "-");
			task.put(UserBlockInterspaceStruction.frequency.name(),
					frequency_name.concat("(" + I18nResourceUtil.getResource("label.suffix.frequency") + ")"));
			task.put(UserBlockInterspaceStruction.repeat.name(), repeat);
		} else {
			return I18nResourceUtil.getResource("prompt.task.fail");
		}
		task.put(UserBlockInterspaceStruction.task_no.name(), poolDateTask_no);
		task.put(UserBlockInterspaceStruction.task_name.name(), subject);
		task.put(UserBlockInterspaceStruction.task_title.name(), handleSQL.replaceAll("\n", ";\n"));
		return I18nResourceUtil.getResource("prompt.task.succ");
	}

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/remove", method = RequestMethod.GET)
	public String removeTask(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "taskNo", required = true) Long taskNo) {
		HashMap<String,Object> userSecure = this.getUserSecure(request);
		String username = String.valueOf(userSecure.get(UserSecure._security_.name())).split("/")[0];
		if (ObjectUtils.isEmpty(userSecure)) {
			return "N";
		}
		HashMap<String, Object> blockInterspace = (HashMap<String, Object>)userSecure.get(username);
		MailTaskJobRobot robot = (MailTaskJobRobot)blockInterspace.get("robot");
		if (ObjectUtils.isEmpty(robot)) {
			return "N";
		}
		robot.removeTask(taskNo);
		ArrayList<LinkedHashMap<String, Object>> taskList = (ArrayList<LinkedHashMap<String, Object>>)blockInterspace.get("taskList");
		LinkedHashMap<String, Object> linkedHashMap = null;
		for (LinkedHashMap<String, Object> lhm : taskList) {
			if (Long.valueOf(String.valueOf(lhm.get(UserBlockInterspaceStruction.task_no.name()))) == taskNo) {
				linkedHashMap = lhm;
				break;
			}
		}
		taskList.remove(linkedHashMap);
		return "Y";
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String,Object> userSecure = this.getUserSecure(request);
		if (ObjectUtils.isEmpty(userSecure)) {
			return "redirect:../login/index";
		}
		String username = String.valueOf(userSecure.get(UserSecure._security_.name())).split("/")[0];
		response.addCookie(new Cookie(UserSecure.username.name(), username));
		request.setAttribute("title", I18nResourceUtil.getResource("title.task.view"));
		return "task.jsp";
	}

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/viewTask", method = RequestMethod.POST, produces = "text/plain; charset=utf-8")
	public String viewTask(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String,Object> userSecure = this.getUserSecure(request);
		String username = String.valueOf(userSecure.get(UserSecure._security_.name())).split("/")[0];
		HashMap<String, Object> blockInterspace = (HashMap<String, Object>)userSecure.get(username);
		ArrayList<LinkedHashMap<String, Object>> taskList = (ArrayList<LinkedHashMap<String, Object>>)blockInterspace.get("taskList");
		return JSONObject.toJSONString(taskList);
	}

	@ResponseBody
	@RequestMapping(value = "/tables", method = RequestMethod.POST, produces = "text/plain; charset=utf-8")
	public String tables(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "db", required = true) String db) {
		MailProcedureService procedure = new MailProcedureService();
		ResultSet resultSet = procedure.getExecutiveResult("use ".concat(db).concat("; show tables"));
		LinkedHashMap<Integer,ArrayList<String>> processResultSet = MailUtils.processResultSet(resultSet);
		return String.valueOf(processResultSet.get(1));
	}

	@ResponseBody
	@RequestMapping(value = "/columns", method = RequestMethod.POST, produces = "text/plain; charset=utf-8")
	public String columns(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "table", required = true) String table) {
		MailProcedureService procedure = new MailProcedureService();
		ResultSet resultSet = procedure.getExecutiveResult("desc ".concat(table));
		LinkedHashMap<Integer,ArrayList<String>> processResultSet = MailUtils.processResultSet(resultSet);
		return String.valueOf(processResultSet.get(1)).concat(String.valueOf(processResultSet.get(2)));
	}

	private static enum UserBlockInterspaceStruction {
		task_date,
		date_frequency,
		date_repeat,
		frequency,
		repeat,
		task_no,
		task_name,
		task_title
	}

	private void foundamentalLabel(HttpServletRequest request) {
		request.setAttribute("title", I18nResourceUtil.getResource("title.service"));
		request.setAttribute("labelDB", I18nResourceUtil.getResource("label.databases"));
		request.setAttribute("labelTB", I18nResourceUtil.getResource("label.tables"));
		request.setAttribute("labelSQL", I18nResourceUtil.getResource("label.sql.text"));
		request.setAttribute("labelSEL", I18nResourceUtil.getResource("label.select.default.name"));
		request.setAttribute("labelTkst", I18nResourceUtil.getResource("label.task.setting"));
		request.setAttribute("labelDate", I18nResourceUtil.getResource("label.date.setting"));
		request.setAttribute("labelFrequency", I18nResourceUtil.getResource("label.frequency"));
		request.setAttribute("labelRepeat", I18nResourceUtil.getResource("label.repeat"));
		request.setAttribute("valYes", I18nResourceUtil.getResource("label.repeat.val.yes"));
		request.setAttribute("valNo", I18nResourceUtil.getResource("label.repeat.val.no"));
		request.setAttribute("attachment", I18nResourceUtil.getResource("label.attachment.filename"));
		request.setAttribute("suffixDF", I18nResourceUtil.getResource("label.suffix.date.frequency"));
		request.setAttribute("suffixf", I18nResourceUtil.getResource("label.suffix.frequency"));
		request.setAttribute("mailRec", I18nResourceUtil.getResource("label.mail.reveiver"));
		request.setAttribute("mailCon", I18nResourceUtil.getResource("label.mail.content"));
		request.setAttribute("mailSubj", I18nResourceUtil.getResource("label.mail.subject"));
		request.setAttribute("button_review", I18nResourceUtil.getResource("button.label.task.review"));
		request.setAttribute("button_submit", I18nResourceUtil.getResource("button.label.submit"));
	}

	@SuppressWarnings("unchecked")
	private boolean authentication(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		Object obj = session.getAttribute("login");
		String user = String.valueOf(session.getAttribute("username"));
		Cookie[] cookies = request.getCookies();
		for (int i = 0; !ObjectUtils.isEmpty(cookies) && i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if (UserSecure.username.name().equals(cookie.getName())) {
				String username = cookie.getValue();
				Object l_obj = session.getAttribute("listed");
				if (ObjectUtils.isEmpty(l_obj)) {
					break;
				}
				ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>)l_obj;
				for (HashMap<String, Object> hashMap : list) {
					if (hashMap.containsKey(username)
							&& this.judgeTime(String.valueOf(hashMap.get(UserSecure.login_time.name())), -1)
							&& "activity".equals(String.valueOf(hashMap.get(UserSecure._status_.name())))) {
						hashMap.put(UserSecure.login_time.name(), String.valueOf(System.currentTimeMillis()));
						return true;
					} else if (StringUtils.isNotBlank(user)
							&& hashMap.containsKey(username)) {
						session.removeAttribute("username");
						hashMap.put(UserSecure._status_.name(), "activity");
						return true;
					} else if (hashMap.containsKey(username)) {
						hashMap.put(UserSecure._status_.name(), "unActive");
					}
				}
			}
		}
		if (ObjectUtils.isEmpty(obj)) {
			return false;
		}
		HashMap<String, Object> hashMap = (HashMap<String, Object>)obj;
		hashMap.put(UserSecure._status_.name(), "activity");
		if (ObjectUtils.isEmpty(session.getAttribute("listed"))) {
			ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
			list.add(hashMap);
			session.setAttribute("listed", list);
		}
		 ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>)session.getAttribute("listed");
		 list.add(hashMap);
		 session.removeAttribute("login");
		 String username = String.valueOf(hashMap.get(UserSecure._security_.name())).split("/")[0];
		 response.addCookie(new Cookie(UserSecure.username.name(), username));
		if (!this.judgeTime(String.valueOf(hashMap.get(UserSecure.login_time.name())), -1)) {
			hashMap.put(UserSecure._status_.name(), "unActive");
			return false;
		}
		return true;
	}

	private boolean judgeTime(String time, long interval) {
		return !(System.currentTimeMillis() - Long.valueOf(time) > (interval < 0 ? 1000 * 60 * 60 * 24 : interval));
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getUserSecure(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (UserSecure.username.name().equals(cookie.getName())) {
				String username = cookie.getValue();
				ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>)session.getAttribute("listed");
				if (ObjectUtils.isEmpty(list)) {
					return null;
				}
				for (HashMap<String, Object> hashMap : list) {
					if (hashMap.containsKey(username)) {
						return hashMap;
					}
				}
			}
		}
		return null;
	}

	private boolean isEquation(String item) {
		return item.contains("+") || item.contains("-") || item.contains("*") || item.contains("/")
				|| item.contains("(") || item.contains(")");
	}

}
