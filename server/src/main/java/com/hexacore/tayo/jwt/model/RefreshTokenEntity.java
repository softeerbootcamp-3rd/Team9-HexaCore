package com.hexacore.tayo.jwt.model;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "Token")
public class RefreshTokenEntity {

    @Id
    private Long id;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;
}
