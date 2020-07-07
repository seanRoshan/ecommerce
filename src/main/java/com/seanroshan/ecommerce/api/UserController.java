package com.seanroshan.ecommerce.api;

import com.seanroshan.ecommerce.entity.Cart;
import com.seanroshan.ecommerce.entity.User;
import com.seanroshan.ecommerce.repository.CartRepository;
import com.seanroshan.ecommerce.repository.UserRepository;
import com.seanroshan.ecommerce.request.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public UserController(UserRepository userRepository, CartRepository cartRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        List<User> list = userRepository.findAll();
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            logger.error(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("USER NOT FOUND", "USER_NOT_FOUND") {{
                addField("DETAIL", "USER NOT FOUND");
                setAuthAction("BAD REQUEST");
            }}));
            return ResponseEntity.notFound().build();
        }
        User user = userOptional.get();
        logger.info(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("USER RETURNED", "USER_RETURNED") {{
            addField("DETAIL", user.toString());
            setAuthAction("OK");
        }}));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.error(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("USER NOT FOUND", "USER_NOT_FOUND") {{
                addField("DETAIL", "USER NOT FOUND");
                setAuthAction("BAD REQUEST");
            }}));
            return ResponseEntity.notFound().build();
        } else {
            logger.info(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("USER RETURNED", "USER_RETURNED") {{
                addField("DETAIL", user.toString());
                setAuthAction("OK");
            }}));
            return ResponseEntity.ok(user);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);
        logger.info(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("CART CREATED", "CART_CREATE_SUCCESS") {{
            addField("DETAIL", cart.toString());
            setAuthAction("OK");
        }}));
        if (createUserRequest.getPassword() == null ||
                createUserRequest.getConfirmPassword() == null ||
                createUserRequest.getPassword().length() < 7 ||
                !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            logger.error(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("INVALID PASSWORD", "USER_CREATE_INVALID_PASSWORD") {{
                addField("DETAIL", "PASSWORD MUST BE MORE THAN 7 CHARACTER AND MATCH WITH CONFIRM PASSWORD");
                setAuthAction("BAD REQUEST");
            }}));
            return ResponseEntity.badRequest().build();
        }
        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);
        logger.info(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("USER CREATED", "USER_CREATE_SUCCESS") {{
            addField("DETAIL", user.toString());
            setAuthAction("OK");
        }}));
        return ResponseEntity.ok(user);
    }

}
