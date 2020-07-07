package com.seanroshan.ecommerce.api;


import com.seanroshan.ecommerce.entity.User;
import com.seanroshan.ecommerce.repository.CartRepository;
import com.seanroshan.ecommerce.repository.UserRepository;
import com.seanroshan.ecommerce.request.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() throws Exception {
        userController = new UserController(userRepository, cartRepository, bCryptPasswordEncoder);
    }

    @Test
    public void create_user() throws Exception {

        when(bCryptPasswordEncoder.encode("test_pass_2020")).thenReturn("thisIsHashed");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("admin");
        request.setPassword("test_pass_2020");
        request.setConfirmPassword("test_pass_20201");

        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

}
