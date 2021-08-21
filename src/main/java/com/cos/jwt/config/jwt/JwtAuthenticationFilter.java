package com.cos.jwt.config.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가 있음.
// 언제 동작? => /login 요청해서 username, password 전송하면 (post)
// UsernamePasswordAuthenticationFilter 동작을 함
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	
	// 받은 AuthenticationManager를 가지고 로그인을 시도하면 되는데, 로그인을 하는 함수는 해당함수이고,
	// 동작 타이밍은 : /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("JwtAuthenticationFilter : 로그인 시도중");
		
		// 1. username, password 받아서
		
		// 2. 정상인지 로그인 시도를 해보는 거에요. authenticationManager로 로그인 시도를 하면!!
		// PrincipalDetails가 호출 loadUserByUsername() 함수 실행됨.
		
		// 3. PrincipalDetails를 세션에 담고 (JWT를 쓰면서 세션을 왜 쓰냐? => Spring Security 페이지 접근 권한관리를 위함)
		
		// 4. JWT 토큰을 만들어서 응답해주면 됨.
		return super.attemptAuthentication(request, response);
	}
}
