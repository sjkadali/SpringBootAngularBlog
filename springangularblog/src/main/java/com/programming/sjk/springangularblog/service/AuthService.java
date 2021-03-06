package com.programming.sjk.springangularblog.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.programming.sjk.springangularblog.dto.LoginRequest;
import com.programming.sjk.springangularblog.dto.RegisterRequest;
import com.programming.sjk.springangularblog.exception.SpringBlogException;
import com.programming.sjk.springangularblog.model.User;
import com.programming.sjk.springangularblog.repository.UserRepository;
import com.programming.sjk.springangularblog.security.JwtProvider;

@Service
public class AuthService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtProvider jwtProvider;
	
	public void signup(RegisterRequest registerRequest) {
		User user = new User();
		user.setUserName(registerRequest.getUsername());
		user.setPassword(encodePassword(registerRequest.getPassword()));
		user.setEmail(registerRequest.getEmail());
		userRepository.save(user);
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}
	
	public AuthenticationResponse login(LoginRequest loginRequest) throws SpringBlogException {
		Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
				loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authenticate);
		String authenticationToken = jwtProvider.generateToken(authenticate);
		AuthenticationResponse authResponse = new AuthenticationResponse();
		authResponse.setAuthenticationToken(authenticationToken);
		authResponse.setUsername(loginRequest.getUsername());
        //return  AuthenticationResponse(authenticationToken, loginRequest.getUsername());
		System.out.println("*************"+authResponse.getAuthenticationToken() + " " + authResponse.getUsername());
		return authResponse;
	}

	public Optional<org.springframework.security.core.userdetails.User> getCurrentUser() {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder.
				getContext().getAuthentication().getPrincipal();
		return Optional.of(principal);
	}

}
