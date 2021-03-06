package org.ddangelorb.jauthservice.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.ddangelorb.jauthservice.exception.CustomException;
import org.ddangelorb.jauthservice.model.Users;
import org.ddangelorb.jauthservice.repository.UsersRepository;
import org.ddangelorb.jauthservice.security.JwtTokenProvider;

@Service
public class UsersService {
	  @Autowired
	  private UsersRepository userRepository;

	  @Autowired
	  private PasswordEncoder passwordEncoder;

	  @Autowired
	  private JwtTokenProvider jwtTokenProvider;

	  @Autowired
	  private AuthenticationManager authenticationManager;
	  
	  public boolean existsByUsername(String username) {
		  return userRepository.existsByUsername(username);
	  }

	  public String signin(String username, String password) {
	    try {
	      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	      return jwtTokenProvider.createToken(username, userRepository.findByUsername(username).getRoles());
	    } catch (AuthenticationException e) {
	      throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
	    }
	  }

	  public String signup(Users user) {
	    if (!existsByUsername(user.getUsername())) {
	      user.setPassword(passwordEncoder.encode(user.getPassword()));
	      userRepository.save(user);
	      return jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
	    } else {
	      throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
	    }
	  }

	  public void activate(String username) {
	    //userRepository.activateByUsername(username);
	  }

	  public Users search(String username) {
	    Users user = userRepository.findByUsername(username);
	    if (user == null) {
	      throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
	    }
	    return user;
	  }

	  public Users whoami(HttpServletRequest req) {
	    return userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
	  }

	  public String refresh(String username) {
	    return jwtTokenProvider.createToken(username, userRepository.findByUsername(username).getRoles());
	  }
}
