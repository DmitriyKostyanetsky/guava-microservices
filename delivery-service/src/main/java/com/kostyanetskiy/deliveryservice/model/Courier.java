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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Courier courier = (Courier) o;

        if (code != null ? !code.equals(courier.code) : courier.code != null) return false;
        return name != null ? name.equals(courier.name) : courier.name == null;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Courier{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", status=" + status +
                '}';
    }
}
