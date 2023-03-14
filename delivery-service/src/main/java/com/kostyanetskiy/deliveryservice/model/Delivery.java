package com.kostyanetskiy.deliveryservice.model;

import com.kostyanetskiy.deliveryservice.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "delivery")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderCode;
    private String trackNo;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", referencedColumnName = "id")
    private Courier courier;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Delivery delivery = (Delivery) o;

        if (orderCode != null ? !orderCode.equals(delivery.orderCode) : delivery.orderCode != null) return false;
        return trackNo != null ? trackNo.equals(delivery.trackNo) : delivery.trackNo == null;
    }

    @Override
    public int hashCode() {
        int result = orderCode != null ? orderCode.hashCode() : 0;
        result = 31 * result + (trackNo != null ? trackNo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "id=" + id +
                ", orderCode='" + orderCode + '\'' +
                ", trackNo='" + trackNo + '\'' +
                ", status=" + status +
                '}';
    }
}
