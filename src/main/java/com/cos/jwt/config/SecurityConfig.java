package com.cos.jwt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

import com.cos.jwt.config.jwt.JwtAuthenticationFilter;
import com.cos.jwt.filter.MyFilter3;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	private final CorsFilter corsFilter;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 해당 SecurityFilter가 다 실행되고나서 -> FilterConfig이 실행된다.
		//http.addFilterBefore(new MyFilter1(), BasicAuthenticationFilter.class); // https://velog.io/@sa833591/Spring-Security-5-Spring-Security-Filter-%EC%A0%81%EC%9A%A9
		http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)	// 세션을 사용하지 않는 stateless로 사용하겠다.
			.and()
			.addFilter(corsFilter)			// 모든 요청은 CorsConfig을 타서 필터를 거친다.			// @CrossOrigin(인증X), 시큐리티 필터에 등록 인증(O)
			.formLogin().disable()			// formLogin 안씀
			.httpBasic().disable()			// 기본적인 로그인 방식도 안씀
			// /login url 방식을 쓰기 위해 새로운 필터를 적용해준다. 
			// 사용하려면 파라미터하나를 꼭 던져줘야 하는데, AuthenticationManager이다. 이유 : JwtAuthenticationFilter 얘가 로그인을 진행하는 필터이기 때문에 AuthenticationManager를 통해 진행한다.
			// 하지만 파라미터를 던질 공간이 없네? -> SecurityConfig가 상속받은 WebSecurityConfigurerAdapter가 들고있다
			// JwtAuthenticationFilter 에서 생성자를 만들고 받아만 주면 되는데, @RequiredArgsConstructor
			
			// !!!! 이렇게 해주는 이유는 .formLogin()을 사용하지 않는다 										-> UsernamePasswordAuthenticationFilter 를 상속하는 녀석을 호출하여 AuthenticationManager를 호출해야한다.
			// AuthenticationManager를 호출하는 이유는? (시큐리티에게 로그인을 위임하는 이유는?) 	-> 시큐리티의 권한을 사용하기 위함이다. 아래의 페이지의 권한을 사용하려면 세션등록을 해야한다.
			// StateLess를 하면서 세션등록을 굳이 해주는 이유 													-> JWT를 사용하면서 동시에, 아래의 필터를 사용하기 위함이다.
			.addFilter(new JwtAuthenticationFilter(authenticationManager()))		
			.authorizeRequests()			
			.antMatchers("/api/v1/user/**")	// 이쪽으로 들어오면?
			.access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")	// 이런 사용자들이 접근 가능
			.antMatchers("/api/v1/manager/**")	// 이쪽으로 들어오면?
			.access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")	// 이런 사용자들이 접근 가능
			.antMatchers("/api/v1/admin/**")	// 이쪽으로 들어오면?
			.access("hasRole('ROLE_ADMIN')")	// 이런 사용자들이 접근 가능
			.anyRequest().permitAll();
			
	}
}
