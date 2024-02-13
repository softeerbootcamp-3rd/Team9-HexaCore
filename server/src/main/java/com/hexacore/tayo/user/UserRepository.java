package com.hexacore.tayo.user;

import com.hexacore.tayo.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
