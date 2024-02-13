package com.hexacore.tayo.user;

import com.hexacore.tayo.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<UserEntity> findByEmail(String email);

}
