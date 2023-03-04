package com.kostyanetskiy.orderservice.repository;

import com.kostyanetskiy.orderservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
