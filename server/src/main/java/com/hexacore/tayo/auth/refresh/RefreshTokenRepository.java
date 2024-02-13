package com.hexacore.tayo.auth.refresh;

import com.hexacore.tayo.auth.refresh.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

}
