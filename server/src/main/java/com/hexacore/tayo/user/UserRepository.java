package com.hexacore.tayo.user;

import com.hexacore.tayo.user.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  
    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    boolean existsByEmailAndIsDeletedFalse(String email);

    Optional<User> findFirstByEmailAndIsDeletedFalse(String email);
}
