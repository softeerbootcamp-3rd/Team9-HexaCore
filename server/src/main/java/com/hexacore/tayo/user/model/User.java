package com.hexacore.tayo.user.model;

import com.hexacore.tayo.common.BaseTime;
import com.hexacore.tayo.reservation.model.Reservation;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "profile_img_url", columnDefinition = "text")
    private String profileImgUrl;

    @Column(name = "customer_key", nullable = false)
    private String customerKey;

    @Column(name = "billing_key")
    private String billingKey;

    @OneToMany(mappedBy = "guest")
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted;

    @Column(name = "average_rate")
    @Builder.Default
    private Double averageRate = 0.0;

    @PrePersist
    protected void onCreate() {
        this.customerKey = UUID.randomUUID().toString();
    }
}
