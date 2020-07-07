package com.seanroshan.ecommerce.api;

import com.seanroshan.ecommerce.entity.Cart;
import com.seanroshan.ecommerce.entity.Item;
import com.seanroshan.ecommerce.entity.User;
import com.seanroshan.ecommerce.repository.CartRepository;
import com.seanroshan.ecommerce.repository.ItemRepository;
import com.seanroshan.ecommerce.repository.UserRepository;
import com.seanroshan.ecommerce.request.ModifyCartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    public CartController(UserRepository userRepository, CartRepository cartRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (!checkUserExistence(user)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            logger.error(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("ITEM NOT FOUND", "ITEM NOT FOUND") {{
                addField("DETAIL", "ITEM NOT FOUND");
                setAuthAction("NOT FOUND");
            }}));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.addItem(item.get()));
        cartRepository.save(cart);
        logger.info(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("ADD TO CART", "ADD_TO_CART") {{
            addField("DETAIL", cart.toString());
            setAuthAction("OK");
        }}));
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (!checkUserExistence(user)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            logger.error(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("ITEM NOT FOUND", "ITEM NOT FOUND") {{
                addField("DETAIL", "ITEM NOT FOUND");
                setAuthAction("NOT FOUND");
            }}));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.removeItem(item.get()));
        cartRepository.save(cart);
        logger.info(String.valueOf(new com.splunk.logging.SplunkCimLogEvent("REMOVE FROM CART", "REMOVE_FROM_CART") {{
            addField("DETAIL", cart.toString());
            setAuthAction("OK");
        }}));
        return ResponseEntity.ok(cart);
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
