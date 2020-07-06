package com.seanroshan.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seanroshan.ecommerce.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
