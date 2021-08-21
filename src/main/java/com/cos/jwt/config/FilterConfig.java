package com.cos.jwt.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cos.jwt.filter.MyFilter1;

// 이 파일은 SecurityFilterChain에 filter를 걸어주는 방식이 아니라 
// 내가 Filter를 만들어 주는 방식이다. (꼭 필터에 복잡하게 걸어주지 않아도 된다.)
// Request가 오면 작동한다.
@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<MyFilter1> filter1(){
		FilterRegistrationBean<MyFilter1> bean = new FilterRegistrationBean<>(new MyFilter1());
		bean.addUrlPatterns("/*");
		bean.setOrder(0);	// 낮은 번호가 필터중에서 가장 먼저 실행됨
		return bean;
	}
}
