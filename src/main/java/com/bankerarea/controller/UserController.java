package com.bankerarea.controller;

import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.HttpResource;

import com.bankerarea.config.LoginManagementService;
import com.bankerarea.mapper.UserMapper;
import com.bankerarea.vo.UserVO;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UserMapper mapper;
	
	@Autowired
	JavaMailSender javaMailSender;
	
	@PostMapping("/account/signin")
	public String signIn(@RequestBody UserVO vo, HttpServletResponse response, HttpServletRequest request) {
		
		System.out.println("로그인시도아이디 : '"+vo.getId()+"'"+", 로그인시도비번 : '"+vo.getPassword()+"'");
		UserVO user = mapper.signInUser(vo);
		if(user == null) { // 로그인 정보를 받았는데 db에 값이 없을 때
			response.setStatus(HttpStatus.BAD_REQUEST.value()); //400
			System.out.println("400찍음");
			return null;
		} else { // 로그인정보 받고 db에 값도 있을 때 ==> 로그인 성공
			System.out.println("로그인 된 정보 : " + user);
			String jwt = LoginManagementService.createJWT(user);
			Cookie cookie = new Cookie("jwt",jwt);
			cookie.setMaxAge(60*60*24*9); // 만료시간 9일
			cookie.setPath("/");
			cookie.setHttpOnly(true);
			response.addCookie(cookie);
			response.setStatus(HttpStatus.OK.value()); //200
		}
		return user.getId();
	}
	
	
	@PostMapping("/account/signup")
	public void signUp(@Valid @RequestBody UserVO vo, HttpServletResponse response, HttpServletRequest request) {
		System.out.println("회원가입시도 : " + vo);
		try {
			mapper.insertUser(vo);
			response.setStatus(HttpStatus.OK.value()); //200
			System.out.println("가입 성공!");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("이미 있는 아이디!!");
			response.setStatus(HttpStatus.FORBIDDEN.value()); //403
		}
	}
	
	@PostMapping("/account/signup/reqsecret")
	public String reqSecretCode(@Valid @RequestBody UserVO user) throws MessagingException {
		System.out.println(user.getEmail());
		String secretCode = LoginManagementService.numberGen(6, 2);
		MimeMessage msg = javaMailSender.createMimeMessage();
		String message = "Hello! We Are BankerArea Team<br/>" +
		"Ur Secret Code is " + secretCode + " :)<br/>" +
		"Have A Nice Day! THX! :)";
		msg.setContent(message, "text/html");
		msg.setSubject("Hello, We Are BankerArea Team!");
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));

	
		javaMailSender.send(msg);
		
		return secretCode;
	}
	@PostMapping("/account/logout")
	public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
		String banker_id = LoginManagementService.signInCheck(request, response);
		if(banker_id == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401
		} else {
			Cookie[] cookies = request.getCookies();
			Cookie accessCookie = null;
			
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("jwt")) {
					accessCookie = cookie;
					break;
				}
			}
			accessCookie.setMaxAge(0); 
			Cookie newCookie = new Cookie("jwt", "kill");
			newCookie.setMaxAge(0);
			newCookie.setPath("/");
			newCookie.setHttpOnly(true);
			response.addCookie(newCookie);
			response.setStatus(HttpStatus.OK.value()); //200
			System.out.println(banker_id + "로그아웃함^^");
		}
	}
	
	
	@GetMapping("/userlist")
	public List<UserVO> getUserList() {
		System.out.println("유저리스트출력");
		return mapper.getUserList();
	}
	
	@GetMapping("/cookieTest")
	public void cookieTest(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("쿠키테스트");
		Cookie cookie = new Cookie("test2", "testValue2");
		response.addCookie(cookie);
	}
	
	@GetMapping("/cookieTestRes")
	public void cookieTestRes(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		for(int i = 0; i < cookies.length; ++i) {
			System.out.println("cookieName : " + cookies[i].getName());
			System.out.println("cookieValue : " + cookies[i].getValue());
		}
	}
	
}
