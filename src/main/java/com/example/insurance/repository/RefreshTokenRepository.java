package com.example.insurance.repository;

import com.example.insurance.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);
}
