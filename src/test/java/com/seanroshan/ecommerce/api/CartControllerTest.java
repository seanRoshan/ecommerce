package com.seanroshan.ecommerce.api;


import com.seanroshan.ecommerce.entity.Cart;
import com.seanroshan.ecommerce.entity.Item;
import com.seanroshan.ecommerce.entity.User;
import com.seanroshan.ecommerce.repository.CartRepository;
import com.seanroshan.ecommerce.repository.ItemRepository;
import com.seanroshan.ecommerce.repository.UserRepository;
import com.seanroshan.ecommerce.request.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);


    private final User adminUser;
    private final Item iphone;


    public CartControllerTest() {
        iphone = new Item();
        iphone.setId(1L);
        iphone.setName("IPHONE");
        iphone.setDescription("GIVE ME YOUR MONEY, NOW!");
        iphone.setPrice(new BigDecimal(1400));

        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword("test_pass_2020");
        adminUser.setCart(new Cart());
    }

    @Before
    public void setUp() {
        cartController = new CartController(userRepository, cartRepository, itemRepository);
    }

    @Test
    public void addToCart() {

        when(userRepository.findByUsername("admin")).thenReturn(adminUser);
        when(userRepository.findByUsername("sean")).thenReturn(null);
        when(itemRepository.findById(iphone.getId())).thenReturn(java.util.Optional.of(iphone));
        when(itemRepository.findById(0L)).thenReturn(java.util.Optional.empty());

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(adminUser.getUsername());
        modifyCartRequest.setItemId(iphone.getId());
        modifyCartRequest.setQuantity(1);
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);


        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(adminUser.getUsername());
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(1);
        response = cartController.addToCart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
        cart = response.getBody();
        assertNull(cart);


        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("sean");
        modifyCartRequest.setItemId(iphone.getId());
        modifyCartRequest.setQuantity(1);
        response = cartController.addToCart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
        cart = response.getBody();
        assertNull(cart);


    }


    @Test
    public void removeFromCart() {

        when(userRepository.findByUsername("admin")).thenReturn(adminUser);
        when(userRepository.findByUsername("sean")).thenReturn(null);
        when(itemRepository.findById(iphone.getId())).thenReturn(java.util.Optional.of(iphone));
        when(itemRepository.findById(0L)).thenReturn(java.util.Optional.empty());

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(adminUser.getUsername());
        modifyCartRequest.setItemId(iphone.getId());
        modifyCartRequest.setQuantity(1);
        ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);


        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(adminUser.getUsername());
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(1);
        response = cartController.removeFromCart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
        cart = response.getBody();
        assertNull(cart);


        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("sean");
        modifyCartRequest.setItemId(iphone.getId());
        modifyCartRequest.setQuantity(1);
        response = cartController.removeFromCart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
        cart = response.getBody();
        assertNull(cart);


    }

}
