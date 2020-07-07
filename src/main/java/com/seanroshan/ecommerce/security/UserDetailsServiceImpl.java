package com.seanroshan.ecommerce.security;

import com.seanroshan.ecommerce.entity.User;
import com.seanroshan.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User applicationUser = userRepository.findByUsername(username);
        if (applicationUser == null) {
            logger.error(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("USER NOT FOUND", "USER NOT FOUND") {{
                addField("DETAIL", "USER NOT FOUND");
                setAuthAction("NOT FOUND");
            }}));
            throw new UsernameNotFoundException(username);
        }
        return new org.springframework.security.core.userdetails.User(
                applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
    }
}
