package com.alicasts.generic_crud.repository;

import com.alicasts.generic_crud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    Optional<User> findByEmailIgnoreCase(String email);
}
