package com.hexacore.tayo.car.model;

import com.hexacore.tayo.car.model.CarEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "Image")
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private CarEntity car;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "idx", nullable = false)
    private Integer index;

    @PrePersist
    private void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = new Date();
    }
}
