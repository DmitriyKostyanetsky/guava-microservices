package com.kostyanetskiy.deliveryservice.model;

import com.kostyanetskiy.deliveryservice.enums.CourierStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "courier")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    private String password;
    private String role;
    @Enumerated(EnumType.STRING)
    private CourierStatus status;
    @OneToMany(mappedBy = "courier")
    private List<Delivery> deliveries;

}
