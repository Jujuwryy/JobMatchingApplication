package com.george.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.george.Security.User;
import com.george.Security.UserRepo;
import com.george.Service.JWTService;

/**
 * REST Controller for handling user authentication and registration.
 * Provides endpoints for fetching users, registering a new user, and logging in.
 */
@RestController
public class UserController {
	
	
	@Autowired
	UserRepo repo;
	
	@Autowired
	AuthenticationManager authManager;
	
	@Autowired
	JWTService jwtService;
	
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
	 /**
     * Retrieves a list of all registered users.
     * 
     * @return List of {@link User} objects
     */
	@GetMapping("/users")
	public List<User> getUsers(){
		
		return repo.findAll();
	}
	 /**
     * Registers a new user by hashing their password before storing it in the database.
     * 
     * @param user The user details from the request body
     * @return The registered {@link User} object
     */
	@PostMapping("register")
	public User registerUser(@RequestBody User user) {
		
		user.setPassword(encoder.encode(user.getPassword()));
		return repo.save(user);
	}
	
	 /**
     * Authenticates a user and generates a JWT token upon successful authentication.
     * 
     * @param user The login credentials from the request body
     * @return JWT token if authentication is successful, else an error response
     */
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody User user) {   //verify user
	   
		try {
	        Authentication authentication = authManager.authenticate(
	            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
	        );

	        if (authentication.isAuthenticated()) {
	        	String token = jwtService.generateToken(user.getUsername());
	            return ResponseEntity.ok("Your token is: \n" + token);
	        }
	    } catch (AuthenticationException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("failure authenticating");
	    }

	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("failure authenticating");
	}
	
	

	
//	public ResponseEntity<String> verifyUser(User user) {
//	    try {
//	        Authentication authentication = authManager.authenticate(
//	            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
//
//	        if (authentication.isAuthenticated()) {
//	            return ResponseEntity.ok("success");
//	        }
//	    } catch (AuthenticationException e) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid credentials");
//	    }
//
//	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Authentication failed");
//	}


}
