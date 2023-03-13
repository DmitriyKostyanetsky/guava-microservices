package com.kostyanetskiy.deliveryservice.service.impl;

import com.kostyanetskiy.deliveryservice.dto.*;
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
import com.kostyanetskiy.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final CourierRepository courierRepository;
    private final KafkaTemplate<String, OrderReceiveEvent> kafkaTemplate;

    @KafkaListener(topics = "orderSend1",
            properties = {"spring.json.value.default.type=com.kostyanetskiy.deliveryservice.event.OrderPlaceEvent"})
    @Override
    public void create(OrderPlaceEvent orderPlaceEvent) {
        String lUUID = String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));

        Delivery delivery = new Delivery();
        delivery.setTrackNo(String.format("DLVR%s", lUUID));
        delivery.setOrderCode(orderPlaceEvent.getOrderCode());
        delivery.setStatus(DeliveryStatus.CREATED);

        deliveryRepository.save(delivery);

        List<Courier> couriers = courierRepository.findAllByStatus(CourierStatus.FREE);
        if (couriers.isEmpty()) {
            kafkaTemplate.send("orderReceive3", createReceiveEvent(delivery));
            return;
        }

        Courier courier = couriers.stream()
                .min(Comparator.comparingInt(o -> o.getDeliveries().size()))
                .get();

        delivery.setCourier(courier);
        delivery.setStatus(DeliveryStatus.ON_DELIVERY);

        deliveryRepository.save(delivery);

        List<Delivery> deliveries = courier.getDeliveries();
        deliveries.add(delivery);
        courier.setDeliveries(deliveries);
        courierRepository.save(courier);

        kafkaTemplate.send("orderReceive3", createReceiveEvent(delivery));
    }

    private OrderReceiveEvent createReceiveEvent(Delivery delivery) {
        return new OrderReceiveEvent(
                delivery.getTrackNo(),
                delivery.getOrderCode(),
                delivery.getCourier() == null ? "" : delivery.getCourier().getName(),
                delivery.getStatus().name()
        );
    }

    @Override
    public DeliveryResponse change(DeliveryChangeRequest deliveryRequest) {
        Delivery delivery = findDeliveryByTrackNo(deliveryRequest.getTrackNo());
        delivery.setStatus(deliveryRequest.getStatus());

        deliveryRepository.save(delivery);

        return buildDeliveryResponse(delivery);
    }

    @Override
    public DeliveryResponse details(String trackNo) {
        Delivery delivery = findDeliveryByTrackNo(trackNo);
        return buildDeliveryResponse(delivery);
    }

    @Override
    public DeliveryResponse assignOnCourier(DeliveryAssignRequest deliveryChangeRequest) {
        Courier courier = findCourierByCode(deliveryChangeRequest.getCourierCode());
        Delivery delivery = findDeliveryByTrackNo(deliveryChangeRequest.getDeliveryCode());

        delivery.setCourier(courier);
        delivery.setStatus(DeliveryStatus.ON_DELIVERY);

        deliveryRepository.save(delivery);

        return buildDeliveryResponse(delivery);
    }

    @Override
    public List<DeliveryResponse> allDeliveriesByCourier(String courierCode) {
        Courier courier = findCourierByCode(courierCode);

        return courier.getDeliveries().stream()
                .map(this::buildDeliveryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryResponse> getAll() {
        return deliveryRepository.findAll().stream()
                .map(this::buildDeliveryResponse)
                .collect(Collectors.toList());
    }

    private Delivery findDeliveryByTrackNo(String trackNo) {
        Optional<Delivery> optionalDelivery = deliveryRepository.findByTrackNo(trackNo);
        if (optionalDelivery.isEmpty()) {
            throw new DeliveryNotFoundException("Не найдена доставка по номеру " + trackNo);
        }

        return optionalDelivery.get();
    }

    private Courier findCourierByCode(String courierCode) {
        Optional<Courier> optionalCourier = courierRepository.findByCode(courierCode);

        if (optionalCourier.isEmpty())
            throw new CourierNotFoundException("Не найден курьер по номеру " + courierCode);

        return optionalCourier.get();
    }

    private DeliveryResponse buildDeliveryResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .deliveryCode(delivery.getTrackNo())
                .status(delivery.getStatus())
                .orderCode(delivery.getOrderCode())
                .courierName(delivery.getCourier() == null ? "" : delivery.getCourier().getName())
                .build();
    }
}
