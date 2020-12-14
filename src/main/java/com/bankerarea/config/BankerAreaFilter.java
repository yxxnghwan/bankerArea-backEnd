package com.bankerarea.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseCookie;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@Configuration
public class BankerAreaFilter implements Filter {
	private List<String> unauth_allow_api = new ArrayList<String>(
			Arrays.asList( 
					"/users/account/signin" , "/users/account/signup" ,
					"/idea/list", "/idea/detail", "/idea/list/search", "/idea/list/likeyTop10"
				));
	//@Autowired
	//private LoginManagementService loginManagementService;
	
	public BankerAreaFilter() {
		// TODO Auto-generated constructor stub
		System.out.println("[BankerArea Service Filter] 생성 완료");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req= (HttpServletRequest) request;
		String URI = req.getRequestURI();
		String method = req.getMethod();
		System.out.println(req.getMethod());
		System.out.println(URI);
		
		res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
		res.setHeader("Access-Control-Allow-Credentials", "true");
		res.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
		res.setHeader("Access-Control-Allow-Max-Age","3600");
		res.setHeader("Access-Control-Allow-Headers","X-Requested-With, Content-Type, "
				+ "Authorization, Origin, Accept, Access-Control-Request-Method, Access-Control-Requests-Headers");
		
		Enumeration enumm = req.getParameterNames();
		while ( enumm.hasMoreElements() ){
			String name = (String) enumm.nextElement();
			String[] values = request.getParameterValues(name);
			for (String value : values) {
				System.out.println("name=" + name + ",value=" + value);
			}
		}
		
		Cookie[] cookies = req.getCookies();
		
		if(!method.equals("OPTIONS")) {
			if(cookies == null) {
				System.out.println("쿠키없쪄헤헤");
				// Code 205 . NoContent 상태라서 요청자의 document view의 reset 요청
				// 쿠키가 없는 and 로그인 기록이 있는 사용자 ( 쿠키 재 생성 및 새 jwt 토큰 발급 글 쓰다가 쿠키가 만료된 경우를 생각 )
				if(!unauth_allow_api.contains(URI)) {
					
					System.out.println("[BankerArea Service Filter] 비로그인 사용자에게 허용이 안된 API 입니다.");
					res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}
				else {
					System.out.println("[BankerArea Service Filter] 비로그인 사용자에게도 허용된 API 입니다.");
					chain.doFilter(request, response);
				}
			} 
			else {
				System.out.println("쿠키 있음");
				if(!unauth_allow_api.contains(URI)) {
					for(Cookie cookie : cookies) {
						System.out.println("쿠키이름"+cookie.getName());
						System.out.println("쿠키밸류"+cookie.getValue());
					}
					//String accessKey = null;
					
					String signInID = LoginManagementService.signInCheck(req, res);
					
					if(signInID == null) { // 만료된 경우 로그인에러처리
						System.out.println("[BankerArea Service Filter] 비로그인 사용자에게 허용이 안된 API 입니다.");
						res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					} else { // 로그인성공
						System.out.println("[BankerArea Service Filter] (사용자)" + signInID + "검증완료");
						chain.doFilter(request, response);
					}
					System.out.println("[BankerArea Service Filter] 비로그인 사용자에게 허용이 안된 API 입니다.");
					res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}
				else {
					System.out.println("[BankerArea Service Filter] 비로그인 사용자에게도 허용된 API 입니다.");
					chain.doFilter(request, response);
				}
				
				
				/*
				try {
					// 정상토큰인 사용자
					String authId = (String)loginManagementService.getIdByToken(accessKey);
					System.out.println("[BankerArea Service Filter] (사용자)" + authId + "검증완료");
					chain.doFilter(request, response);
				} catch (ExpiredJwtException e) {
					// 쿠키가 있는 and 토큰이 만료된 사용자 ( 쿠키 및 재설정 토큰 재설정 )
					
				} catch (JwtException e) {
					
				} catch (Exception e) {
					
				}*/
			}
		}
	}
}
