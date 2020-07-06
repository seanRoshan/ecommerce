package com.seanroshan.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seanroshan.ecommerce.entity.Cart;
import com.seanroshan.ecommerce.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUser(User user);
}
