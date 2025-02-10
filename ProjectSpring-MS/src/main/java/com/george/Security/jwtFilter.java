package com.george.Security;

import java.io.IOException;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.george.Service.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class jwtFilter extends OncePerRequestFilter {
    
    @Autowired
    JWTService jwtService; // Injects the JWTService to handle token operations.
    
    @Autowired
    ApplicationContext context; // Allows access to the Spring context to retrieve the MyUserDetailsService.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization"); // Retrieve the Authorization header from the request
        String token = null;
        String username = null;
        
        // Check if the Authorization header exists and starts with "Bearer "
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            
            token = authHeader.substring(7); // Extract token from the header by removing the "Bearer " prefix
            username = jwtService.extractUserName(token); // Extract username from the token using the JWTService
        }
        
        // If the token and username are valid and no authentication is already set in the security context
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Load the user details from the MyUserDetailsService
            UserDetails userDetails = ((BeanFactory) context).getBean(MyUserDetailsService.class).loadUserByUsername(username);
            
            // Validate the token and the user details
            if(jwtService.validateToken(token, userDetails)) {
                
                // Create an Authentication token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                // Set details of the authentication (such as remote address, session ID)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Set the authentication token in the SecurityContextHolder to allow Spring Security to manage it
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // Continue with the filter chain to process the request
        filterChain.doFilter(request, response);
    }
}
