package com.cos.jwt.config.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

// PrincipalDetails를 세션에 담고 (JWT를 쓰면서 세션을 왜 쓰냐? => Spring Security 페이지 접근 권한관리를 위함)


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
		try {
//			BufferedReader br = request.getReader();
//			System.out.println(br.readLine());
//			
//			String input = null;
//			while((input = br.readLine()) != null) {
//				System.out.println(input);			// username=kanu&password=1234
//			}
			ObjectMapper om = new ObjectMapper();
			User user = om.readValue(request.getInputStream(), User.class);
			//System.out.println(user);		// User(id=0, username=kanu, password=1234, roles=null)
			
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
			
			// PrincipalDetailsService의 loadUserByUsername() 함수가 실행된 후 정상이면 => authentication이 리턴됨
			// 실행될때 얘는 username만 받는다. password는 spring이 DB와 함께 알아서 처리한다.
			// 리턴값으로 로그인한 정보가 담긴다.
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			
			// authentication 객체가 session 영역에 저장됨 => 로그인이 되었다는 뜻
			PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
			System.out.println("로그인 시도 유저 : "+principalDetails.getUser().getUsername());
			
			// authentication 객체가 session 영역에 저장을 해야하고 그 방법이 return 해주면 됨
			// 리턴의 이유는 권한 관리를 security가 대신 해주기 때문에 편하려고 하는 것이다.
			//  굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없다. 근데 단지 권한 처리때문에 session에 넣어줍니다.
			return authentication;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	// attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행되요.
	// JWT 토큰을 만들어서 request요청한 사용자에게 JWT토큰을 response 해주면 됨
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		System.out.println("successfulAuthentication 함수 실행되었습니다! ");

		PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
		
		String jwtToken = JWT.create()
				.withSubject("kanu토큰")														// 토큰이름
				.withExpiresAt(new Date(System.currentTimeMillis()+(60000*10)))		// 토큰 만료 시간
				.withClaim("id", principalDetails.getUser().getId())
				.withClaim("username", principalDetails.getUser().getUsername())
				.sign(Algorithm.HMAC512("kanu"));											// 서버만 아는 고유한 값 (secret)
		
		response.addHeader("Authorization","Bearer "+jwtToken);
		
		//super.successfulAuthentication(request, response, chain, authResult);
	}
}
