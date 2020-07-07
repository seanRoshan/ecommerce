package com.seanroshan.ecommerce.api;

import com.seanroshan.ecommerce.entity.User;
import com.seanroshan.ecommerce.entity.UserOrder;
import com.seanroshan.ecommerce.repository.OrderRepository;
import com.seanroshan.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public OrderController(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }


    @PostMapping("/submit/{username}")
    public ResponseEntity<UserOrder> submit(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (!checkUserExistence(user))
            return ResponseEntity.notFound().build();
        UserOrder order = UserOrder.createFromCart(user.getCart());
        orderRepository.save(order);
        logger.info(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("ORDER SAVED", "ORDER_SAVED") {{
            addField("DETAIL", order.toString());
            setAuthAction("OK");
        }}));
        return ResponseEntity.ok(order);
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (!checkUserExistence(user))
            return ResponseEntity.notFound().build();
        List<UserOrder> orderHistory = orderRepository.findByUser(user);
        logger.info(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("GET ORDER HISTORY", "GET_ORDER_HISTORY") {{
            addField("DETAIL", orderHistory.toString());
            setAuthAction("OK");
        }}));
        return ResponseEntity.ok(orderHistory);
    }

    private boolean checkUserExistence(User user) {
        if (user == null) {
            logger.error(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("USER NOT FOUND", "USER_NOT_FOUND") {{
                addField("DETAIL", "USER NOT FOUND");
                setAuthAction("BAD REQUEST");
            }}));
            return false;
        }
        return true;
    }
}
