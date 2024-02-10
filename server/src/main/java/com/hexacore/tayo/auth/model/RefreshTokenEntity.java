package com.hexacore.tayo.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Token")
public class RefreshTokenEntity {

    @Id
    private Long id;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;
}
