package com.kostyanetskiy.deliveryservice.db;

import com.kostyanetskiy.deliveryservice.DeliveryServiceApplication;
import com.kostyanetskiy.deliveryservice.config.H2TestProfileJPAConfig;
import com.kostyanetskiy.deliveryservice.enums.CourierStatus;
import com.kostyanetskiy.deliveryservice.enums.DeliveryStatus;
import com.kostyanetskiy.deliveryservice.model.Courier;
import com.kostyanetskiy.deliveryservice.model.Delivery;
import com.kostyanetskiy.deliveryservice.repository.CourierRepository;
import com.kostyanetskiy.deliveryservice.repository.DeliveryRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        DeliveryServiceApplication.class,
        H2TestProfileJPAConfig.class})
public class DeliveryRepositoryTest {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private CourierRepository courierRepository;

    @Test
    void findByName() {
        String trackNo = "DLVR1";

        Courier courier = new Courier();
        courier.setName("Sam");
        courier.setRole("ROLE_COURIER");
        courier.setPassword("pass1");
        courier.setCode("COUR2");
        courier.setStatus(CourierStatus.FREE);

        Delivery delivery = new Delivery();
        delivery.setStatus(DeliveryStatus.ON_DELIVERY);
        delivery.setCourier(courier);
        delivery.setOrderCode("CODE123");
        delivery.setTrackNo(trackNo);

        courierRepository.save(courier);
        deliveryRepository.save(delivery);

        Optional<Delivery> optionalDelivery = deliveryRepository.findByTrackNo(trackNo);

        assertTrue(optionalDelivery.isPresent());
        Delivery deliveryFromRep = optionalDelivery.get();
        assertEquals(delivery, deliveryFromRep);
        assertEquals(courier, delivery.getCourier());
    }

}
