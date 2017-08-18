package com.ipinyou.data.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ipinyou.data.utils.AuthorizationUtils;
import com.ipinyou.data.utils.I18nResourceUtil;

@Controller
@RequestMapping(value = "/login")
public class LoginController {

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String preLogin(HttpServletRequest request) {
		this.foundamentalLabel(request);
		return "index.jsp";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String login(HttpServletRequest request, @RequestParam(value  = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password) {
		if (!AuthorizationUtils.authentication(username, password)) {
			request.setAttribute("loginFailed", I18nResourceUtil.getResource("prompt.login.failed"));
			return "./index";
		}
		HttpSession session = request.getSession(true);
		Object o_list = session.getAttribute("listed");
		if (!ObjectUtils.isEmpty(o_list)) {
			ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>)o_list;
			for (HashMap<String, Object> map : list) {
				if (map.containsKey(username)) {
					session.setAttribute("username", username);
					return "redirect:../mail/pre-service";
				}
			}
		}
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		HashMap<String, Object> userBlockInterspace = new HashMap<String, Object>();
		hashMap.put(username, userBlockInterspace);
		hashMap.put(UserSecure._security_.name(), username.concat("/").concat(password));
		hashMap.put(UserSecure._status_.name(), "unActive");
		hashMap.put(UserSecure.login_time.name(), String.valueOf(System.currentTimeMillis()));
		session.setAttribute("login", hashMap);
		return "redirect:../mail/pre-service";
	}

	private void foundamentalLabel(HttpServletRequest request) {
		request.setAttribute("label_username", I18nResourceUtil.getResource("label.username"));
		request.setAttribute("label_password", I18nResourceUtil.getResource("label.password"));
		request.setAttribute("label_login", I18nResourceUtil.getResource("label.login"));
		request.setAttribute("title", I18nResourceUtil.getResource("title.login"));
	}

	public static enum UserSecure {
		_security_,
		_status_,
		login_time,
		username
	}

}
