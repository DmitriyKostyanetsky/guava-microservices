package com.kostyanetskiy.deliveryservice.repository;

import com.kostyanetskiy.deliveryservice.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByTrackNo(String trackNo);
}
