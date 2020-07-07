package com.seanroshan.ecommerce.api;


import com.seanroshan.ecommerce.entity.Cart;
import com.seanroshan.ecommerce.entity.Item;
import com.seanroshan.ecommerce.entity.User;
import com.seanroshan.ecommerce.entity.UserOrder;
import com.seanroshan.ecommerce.repository.OrderRepository;
import com.seanroshan.ecommerce.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    private final User adminUser;
    private final UserOrder order;
    private final List<UserOrder> orderHistory;

    public OrderControllerTest() {

        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword("test_pass_2020");

        Item iphone = new Item();
        iphone.setId(1L);
        iphone.setName("IPHONE");
        iphone.setDescription("GIVE ME YOUR MONEY, NOW!");
        iphone.setPrice(new BigDecimal(1400));
        ArrayList<Item> items = new ArrayList<>();
        items.add(iphone);

        order = new UserOrder();
        order.setId(1L);
        order.setItems(items);

        orderHistory = new ArrayList<>();
        orderHistory.add(order);


        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(adminUser);
        cart.addItem(iphone);

        adminUser.setCart(cart);
    }

    @Before
    public void setUp() {
        orderController = new OrderController(userRepository, orderRepository);
    }

    @Test
    public void getOrdersForUser() {

        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(adminUser);
        when(userRepository.findByUsername("sean")).thenReturn(null);
        when(orderRepository.findByUser(adminUser)).thenReturn(orderHistory);


        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(adminUser.getUsername());

        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> orderHistory = response.getBody();
        assertNotNull(orderHistory);
        assertEquals(orderHistory.get(0).getId(), order.getId());


        response = orderController.getOrdersForUser("sean");
        assertEquals(404, response.getStatusCodeValue());
        orderHistory = response.getBody();
        assertNull(orderHistory);
    }

    @Test
    public void submit() {

        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(adminUser);
        when(userRepository.findByUsername("sean")).thenReturn(null);
        when(orderRepository.findByUser(adminUser)).thenReturn(orderHistory);

        ResponseEntity<UserOrder> response = orderController.submit(adminUser.getUsername());

        assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        assertNotNull(order);


        response = orderController.submit("sean");

        assertEquals(404, response.getStatusCodeValue());
        order = response.getBody();
        assertNull(order);

    }


}
