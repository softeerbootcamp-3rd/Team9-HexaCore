package com.hexacore.tayo.auth;

import com.hexacore.tayo.auth.model.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

}
