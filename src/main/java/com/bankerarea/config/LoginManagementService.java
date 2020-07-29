package com.bankerarea.config;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.bankerarea.vo.UserVO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class LoginManagementService {
	
	private static String JWTkey;

	@Value("${jwtkey}")
	public void setJWTkey(String jWTkey) {
		JWTkey = jWTkey;
	}

	public LoginManagementService() {
		// TODO Auto-generated constructor stub
		System.out.println("[Login Management Service] 생성 완료");
	}
	
	// JWT발급
	public static String createJWT(UserVO vo) {
			
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");
		Map<String, Object> payloads = new HashMap<String, Object>();
		long expiredTime = 1000*60*60*24*7;	// 유효기간 7일
		Date now = new Date();
		now.setTime(now.getTime() + expiredTime);
		System.out.println("입력만료시간" + now);
		payloads.put("exp", now);
					
		payloads.put("id",vo.getId());
				
		String jwt = Jwts.builder()
							.setHeader(headers)
							.setClaims(payloads)
							.signWith(SignatureAlgorithm.HS256, LoginManagementService.JWTkey.getBytes())
							.compact();
		return jwt;
	}
		
	// JWT 디코딩
	public static String getIdFromJwtString(String jwtTokenString, HttpServletRequest request, HttpServletResponse response) {
		String user_id = null;
		try {
			System.out.println("왜안들어와 : "+LoginManagementService.JWTkey);
			Claims claims = Jwts.parser()
							.setSigningKey(LoginManagementService.JWTkey.getBytes())
							.parseClaimsJws(jwtTokenString)
							.getBody();
		
			Date expiration = claims.get("exp", Date.class);
			expiration.setTime(expiration.getTime()/1000); // 이상하게 입력할때 밀리초를 초로 받음 그래서 나올땐 또 1000을 곱해서 나옴...
			System.out.println("받은만료시간 : " + expiration);
			Date now = new Date();
			user_id = claims.get("id", String.class);
			// 기간이 만료되면....?? null 을 리턴  ==> 쿠키삭제
			if(expiration.getTime() < now.getTime()) {
				System.out.println("기간이 만료된 쿠키입니다");
				user_id =  null;
			}
			// 기간이 얼마 안남았을 때
			else if(expiration.getTime()-1000*60*60*24 < now.getTime()) {
				System.out.println("토큰기간이 얼마 안남아서 리프레쉬함");
				System.out.println("원래 jwt : " + jwtTokenString);
				UserVO vo = new UserVO();
				vo.setId(claims.get("id", String.class));
				String newJWT = LoginManagementService.createJWT(vo);
				System.out.println("바꾼 jwt : " + newJWT);
				// 쿠키도 바꿔야함
				Cookie newCookie = new Cookie("jwt", newJWT);
				newCookie.setMaxAge(60*60*24*10);
				newCookie.setPath("/");
				newCookie.setHttpOnly(true);
				response.addCookie(newCookie);
				user_id = getIdFromJwtString(newJWT, request, response);
			}
		} catch(JwtException e) {
			System.out.println("토큰변조위험!!");
			Cookie newCookie = new Cookie("jwt", "kill");
			newCookie.setMaxAge(0);
			newCookie.setPath("/");
			newCookie.setHttpOnly(true);
			response.addCookie(newCookie);
			user_id = null;						// null값으로 넘겨줘서 다시 로그인하도록
		}
		// 기간 빵빵하면 그냥 아이디 리턴
		return user_id;
	}
		
	// 로그인 체크
	public static String signInCheck(HttpServletRequest request, HttpServletResponse response) {
		String signInID = null;
		Cookie[] reqCookies = request.getCookies();
		if(reqCookies != null) {
			for(int i = 0; i < reqCookies.length; ++i) {
				if(reqCookies[i].getName().equals("jwt")) {
					String jwt = reqCookies[i].getValue();		// jwt넣고
					// jwt 디코딩해서 정보 확인
					signInID = LoginManagementService.getIdFromJwtString(jwt, request, response);
						
					// 만료 확인
					if(signInID == null) { // 쿠키 지우고
						System.out.println("만료확인 쿠키 지우기");
						reqCookies[i].setMaxAge(0); 
						Cookie newCookie = new Cookie("jwt", "kill");
						newCookie.setMaxAge(0);
						newCookie.setPath("/");
						newCookie.setHttpOnly(true);
						response.addCookie(newCookie);
					}
					break;
				}
			}
		}
		return signInID;
	}
	
	public static String numberGen(int len, int dupCd) {
		Random rand = new Random();
		String numStr = "";
		for(int i=0;i<len;i++) {
			String ran = Integer.toString(rand.nextInt(10));
			if(dupCd==1) {
				numStr += ran;
			} else if(dupCd==2) {
				if(!numStr.contains(ran)) {
					numStr += ran;
				} else {
					i -= 1;
				}
			}
		}
		return numStr;
	}
}
