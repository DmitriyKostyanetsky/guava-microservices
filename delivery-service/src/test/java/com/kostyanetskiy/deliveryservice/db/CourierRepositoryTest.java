package com.kostyanetskiy.deliveryservice.db;

import com.kostyanetskiy.deliveryservice.DeliveryServiceApplication;
import com.kostyanetskiy.deliveryservice.config.H2TestProfileJPAConfig;
import com.kostyanetskiy.deliveryservice.enums.CourierStatus;
import com.kostyanetskiy.deliveryservice.model.Courier;
import com.kostyanetskiy.deliveryservice.repository.CourierRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        DeliveryServiceApplication.class,
        H2TestProfileJPAConfig.class})
public class CourierRepositoryTest {

    @Autowired
    private CourierRepository courierRepository;

    @Test
    void findByName() {
        Courier courier = new Courier();
        courier.setName("Sam");
        courier.setRole("ROLE_COURIER");
        courier.setPassword("pass1");
        courier.setCode("COUR2");
        courier.setStatus(CourierStatus.FREE);

        courierRepository.save(courier);
        Optional<Courier> optionalCourier = courierRepository.findByName(courier.getName());
        assertTrue(optionalCourier.isPresent());
        Courier courierFromRepository = optionalCourier.get();
        assertEquals(courier, courierFromRepository);
    }

    @Test
    void findByCode() {
        Courier courier = new Courier();
        courier.setName("Will");
        courier.setRole("ROLE_COURIER");
        courier.setPassword("pass");
        courier.setCode("COUR1");
        courier.setStatus(CourierStatus.FREE);
        courier.setDeliveries(Collections.emptyList());

        courierRepository.save(courier);
        Optional<Courier> optionalCourier = courierRepository.findByCode(courier.getCode());
        assertTrue(optionalCourier.isPresent());
        Courier courierFromRepository = optionalCourier.get();
        assertEquals(courier, courierFromRepository);
    }

    @Test
    void findAllByStatus() {
        Courier courier = new Courier();
        courier.setName("Andrey");
        courier.setRole("ROLE_COURIER");
        courier.setPassword("pass2");
        courier.setCode("COUR3");
        courier.setStatus(CourierStatus.FREE);

        courierRepository.save(courier);
        List<Courier> couriers = courierRepository.findAllByStatus(CourierStatus.FREE);
        assertFalse(couriers.isEmpty());
        couriers.forEach(cr -> assertEquals(CourierStatus.FREE, cr.getStatus()));
    }
}
