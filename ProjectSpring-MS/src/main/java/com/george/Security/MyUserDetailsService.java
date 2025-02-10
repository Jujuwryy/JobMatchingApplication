package com.george.Security;

import com.george.Exception.UserNotFoundException;
import com.george.Security.User;
import com.george.Security.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    private UserRepo repo; // Injecting UserRepo to access the database

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Attempt to retrieve the user from the database by username
        Optional<User> userOptional = Optional.ofNullable(repo.findByUsername(username));

        // Map the user to UserPrincipal (implements UserDetails)
        // If user is not found, throw UserNotFoundException with a helpful message and log the warning
        return userOptional.map(UserPrincipal::new)
                .orElseThrow(() -> {
                    logger.warn("User with username '{}' not found", username); // Logging failed lookup
                    return new UserNotFoundException("User with username '" + username + "' not found.");
                });
    }
}
