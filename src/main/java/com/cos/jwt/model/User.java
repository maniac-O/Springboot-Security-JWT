package com.cos.jwt.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String username;
	private String password;
	private String roles; // USER, ADMIN
	
	public List<String> getRoleList(){
		System.out.println(this.roles.length());
		if(this.roles.length() > 0) {
			return Arrays.asList(this.roles.split(","));
		}
		return new ArrayList<>();
	}
	
	/*
        user.getRoleList().forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                authorities.add(new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return s;
                    }
                });
            }
        });
        */
}
