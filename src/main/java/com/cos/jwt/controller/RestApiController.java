package com.cos.jwt.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.jwt.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RestApiController {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/home")
	public String home() {
		System.out.println("HOMEEEEEEEEEEEEEE");
		return "<h1>home</h1>";
	}
	
	@PostMapping("/token")
	public String token() {
		return "<h1>token</h1>";
		
		
	}// 매니저 혹은 어드민이 접근 가능
	@GetMapping("/manager/reports")
	public String reports() {
		return "<h1>reports</h1>";
	}
	
	// 어드민이 접근 가능
	@GetMapping("/admin/users")
	public List<User> users(){
		return userRepository.findAll();
	}

	@PostMapping("/join")
	public String join(@RequestBody User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setRoles("ROLE_USER");
		userRepository.save(user);
		return "회원가입완료";
	}
	
	// 인증받은 사용자 접근가능
	@GetMapping("/api/v1/user")
	public String user(Authentication authentication) {
		PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("API/V1/USER");
		System.out.println("principal : "+principal.getUser().getId());
		System.out.println("principal : "+principal.getUser().getUsername());
		System.out.println("principal : "+principal.getUser().getPassword());
		return "user";
	}
	
	// manager, admin 권한만 접근 가능
	@GetMapping("/api/v1/manager")
	public String manager() {
		return "manager";
	}
	
	// admin 권한만 접근가능
	@GetMapping("/api/v1/admin")
	public String admin() {
		return "admin";
	}
}
