package com.seanroshan.ecommerce.api;


import com.seanroshan.ecommerce.entity.User;
import com.seanroshan.ecommerce.repository.CartRepository;
import com.seanroshan.ecommerce.repository.UserRepository;
import com.seanroshan.ecommerce.request.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    private final User adminUser;

    public UserControllerTest() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword("test_pass_2020");
        this.adminUser = adminUser;
    }


    @Before
    public void setUp() throws Exception {
        userController = new UserController(userRepository, cartRepository, bCryptPasswordEncoder);
    }


    @Test
    public void create_user() throws Exception {

        when(bCryptPasswordEncoder.encode("test_pass_2020")).thenReturn("thisIsHashed");

        // 1. TEST FOR VALID USER
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(adminUser.getUsername());
        request.setPassword(adminUser.getPassword());
        request.setConfirmPassword("test_pass_2020");

        ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());

        //2. TEST FOR INVALID PASSWORD

        request = new CreateUserRequest();
        request.setUsername("admin");
        request.setPassword("test_pass_2020");
        request.setConfirmPassword("test_pass_2021");

        response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());


        //3. TEST FO EMPTY PASSWORD
        request = new CreateUserRequest();
        request.setUsername("admin");
        response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

    }

    @Test
    public void getUsernameTest() throws Exception {

        when(userRepository.findByUsername("admin")).thenReturn(adminUser);
        when(userRepository.findByUsername("sean")).thenReturn(null);

        //4. TEST FOR VALID USER SEARCH
        ResponseEntity<User> response = userController.findByUserName(adminUser.getUsername());
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(user.getUsername(), adminUser.getUsername());

        //4. TEST FOR INVALID USER SEARCH
        response = userController.findByUserName("sean");
        assertEquals(404, response.getStatusCodeValue());
        user = response.getBody();
        assertNull(user);
    }

    @Test
    public void findById() throws Exception {

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(adminUser));
        when(userRepository.findById(0L)).thenReturn(java.util.Optional.empty());

        //4. TEST FOR VALID USER SEARCH
        assert adminUser != null;
        ResponseEntity<User> response = userController.findById(adminUser.getId());
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(user.getUsername(), adminUser.getUsername());

        //4. TEST FOR INVALID USER SEARCH
        response = userController.findById(0L);
        assertEquals(404, response.getStatusCodeValue());
        user = response.getBody();
        assertNull(user);
    }


}
