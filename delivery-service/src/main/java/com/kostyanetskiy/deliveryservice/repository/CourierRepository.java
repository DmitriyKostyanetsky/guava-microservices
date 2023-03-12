package com.kostyanetskiy.deliveryservice.repository;

import com.kostyanetskiy.deliveryservice.enums.CourierStatus;
import com.kostyanetskiy.deliveryservice.model.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourierRepository extends JpaRepository<Courier, Long> {
    List<Courier> findAllByStatus(CourierStatus status);
    Optional<Courier> findByCode(String code);
    Optional<Courier> findByName(String name);
}
