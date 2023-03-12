package com.kostyanetskiy.deliveryservice.service.impl;

import com.kostyanetskiy.deliveryservice.model.Courier;
import com.kostyanetskiy.deliveryservice.repository.CourierRepository;
import com.kostyanetskiy.deliveryservice.security.PersonDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonDetailsService implements UserDetailsService {

    private final CourierRepository courierRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Courier> optionalCourier = courierRepository.findByName(username);

        if (optionalCourier.isEmpty())
            throw new UsernameNotFoundException("User not found");

        return new PersonDetails(optionalCourier.get());
    }
}
