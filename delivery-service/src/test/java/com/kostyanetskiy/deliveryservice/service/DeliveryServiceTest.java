package com.kostyanetskiy.deliveryservice.service;

import com.kostyanetskiy.deliveryservice.dto.DeliveryAssignRequest;
import com.kostyanetskiy.deliveryservice.dto.DeliveryChangeRequest;
import com.kostyanetskiy.deliveryservice.dto.DeliveryResponse;
import com.kostyanetskiy.deliveryservice.enums.CourierStatus;
import com.kostyanetskiy.deliveryservice.enums.DeliveryStatus;
import com.kostyanetskiy.deliveryservice.event.OrderPlaceEvent;
import com.kostyanetskiy.deliveryservice.event.OrderReceiveEvent;
import com.kostyanetskiy.deliveryservice.exception.CourierNotFoundException;
import com.kostyanetskiy.deliveryservice.exception.DeliveryNotFoundException;
import com.kostyanetskiy.deliveryservice.model.Courier;
import com.kostyanetskiy.deliveryservice.model.Delivery;
import com.kostyanetskiy.deliveryservice.repository.CourierRepository;
import com.kostyanetskiy.deliveryservice.repository.DeliveryRepository;
import com.kostyanetskiy.deliveryservice.service.impl.DeliveryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DeliveryServiceImpl.class)
public class DeliveryServiceTest {

    @Autowired
    private DeliveryService deliveryService;

    @MockBean
    private DeliveryRepository deliveryRepository;

    @MockBean
    private CourierRepository courierRepository;

    @MockBean
    private KafkaTemplate<String, OrderReceiveEvent> kafkaTemplate;

    private final String COURIER_CODE = "COUR1";
    private final String TRACK_NO = "DLVR01";

    @Test
    void change_delivery_not_found_exception() {
        DeliveryChangeRequest deliveryChangeRequest = DeliveryChangeRequest.builder()
                .trackNo(TRACK_NO)
                .status(DeliveryStatus.ON_DELIVERY)
                .build();

        when(deliveryRepository.save(new Delivery())).thenReturn(null);

        DeliveryNotFoundException deliveryNotFoundException = assertThrows(DeliveryNotFoundException.class, () ->
                deliveryService.change(deliveryChangeRequest)
        );

        assertEquals("Не найдена доставка по номеру DLVR01", deliveryNotFoundException.getMessage());
    }

    @Test
    void change() {
        DeliveryChangeRequest deliveryChangeRequest = DeliveryChangeRequest.builder()
                .trackNo(TRACK_NO)
                .status(DeliveryStatus.ON_DELIVERY)
                .build();

        Courier courier = getCourier();
        Delivery delivery = getDelivery(courier);

        when(deliveryRepository.findByTrackNo(TRACK_NO)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(new Delivery())).thenReturn(null);

        DeliveryResponse deliveryResponse = deliveryService.change(deliveryChangeRequest);

        assertEquals(TRACK_NO, deliveryResponse.getDeliveryCode());
        assertEquals(DeliveryStatus.ON_DELIVERY.name(), deliveryResponse.getStatus());
        assertNotNull(deliveryResponse.getOrderCode());
    }

    @Test
    void assignOnCourier() {
        String courierAssignCode = "CODEPETYA";
        DeliveryAssignRequest deliveryAssignRequest = DeliveryAssignRequest.builder()
                .deliveryCode(TRACK_NO)
                .courierCode(courierAssignCode)
                .build();

        Courier courier = getCourier();
        Delivery delivery = getDelivery(courier);

        Courier assignCourier = new Courier();
        assignCourier.setName("Petya");
        assignCourier.setCode(courierAssignCode);

        when(courierRepository.findByCode(courierAssignCode)).thenReturn(Optional.of(assignCourier));
        when(deliveryRepository.findByTrackNo(TRACK_NO)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(new Delivery())).thenReturn(null);

        DeliveryResponse deliveryResponse = deliveryService.assignOnCourier(deliveryAssignRequest);

        assertEquals(TRACK_NO, deliveryResponse.getDeliveryCode());
        assertEquals(DeliveryStatus.ON_DELIVERY.name(), deliveryResponse.getStatus());
        assertEquals("Petya", deliveryResponse.getCourierName());
        assertNotNull(deliveryResponse.getOrderCode());
    }

    @Test
    void assignOnCourier_courier_not_found() {
        DeliveryAssignRequest deliveryAssignRequest = DeliveryAssignRequest.builder()
                .deliveryCode(TRACK_NO)
                .courierCode("1")
                .build();

        CourierNotFoundException exception = assertThrows(CourierNotFoundException.class, () ->
                deliveryService.assignOnCourier(deliveryAssignRequest)
        );

        assertEquals("Не найден курьер по номеру 1", exception.getMessage());
    }

    @Test
    void assignOnCourier_delivery_not_found() {
        DeliveryAssignRequest deliveryAssignRequest = DeliveryAssignRequest.builder()
                .deliveryCode(TRACK_NO)
                .courierCode(COURIER_CODE)
                .build();

        Courier courier = getCourier();

        when(courierRepository.findByCode(COURIER_CODE)).thenReturn(Optional.of(courier));

        DeliveryNotFoundException exception = assertThrows(DeliveryNotFoundException.class, () ->
                deliveryService.assignOnCourier(deliveryAssignRequest)
        );

        assertEquals("Не найдена доставка по номеру " + TRACK_NO, exception.getMessage());
    }

    @Test
    void create_all_couriers_are_busy() {
        OrderPlaceEvent event = new OrderPlaceEvent();
        event.setAddress("Address 11");
        event.setItemName("item test");
        event.setOrderCode("ORDR1");
        event.setReceiver("Mary");

        deliveryService.create(event);
    }

    @Test
    void create_assign_on_courier() {
        OrderPlaceEvent event = new OrderPlaceEvent();
        event.setAddress("Address 11");
        event.setItemName("item test");
        event.setOrderCode("ORDR1");
        event.setReceiver("Mary");

        List<Courier> freeCouriers = getFreeCouriers();

        when(courierRepository.findAllByStatus(CourierStatus.FREE)).thenReturn(freeCouriers);

        deliveryService.create(event);
    }

    private List<Courier> getFreeCouriers() {
        List<Courier> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Courier courier = new Courier();
            courier.setStatus(CourierStatus.ON_DELIVERY);
            courier.setCode("CODE" + i);
            courier.setPassword("pass" + i);
            courier.setName("Alex");
            courier.setRole("ROLE_COURIER");

            Delivery delivery = new Delivery();
            delivery.setTrackNo("DLVR" + i);
            delivery.setOrderCode("ORDR" + i);
            delivery.setCourier(courier);
            delivery.setStatus(DeliveryStatus.ON_DELIVERY);

            List<Delivery> deliveries = new ArrayList<>();
            deliveries.add(delivery);
            courier.setDeliveries(deliveries);

            list.add(courier);
        }
        return list;
    }

    private Delivery getDelivery(Courier courier) {
        Delivery delivery = new Delivery();
        delivery.setStatus(DeliveryStatus.CREATED);
        delivery.setOrderCode("ORDR1");
        delivery.setTrackNo(TRACK_NO);
        delivery.setCourier(courier);
        courier.setDeliveries(Collections.singletonList(delivery));
        return delivery;
    }

    private Courier getCourier() {
        Courier courier = new Courier();
        courier.setName("Vasia");
        courier.setCode(COURIER_CODE);
        return courier;
    }
}
