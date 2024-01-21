package com.example.insurance.repository;

import com.example.insurance.entity.UserAccount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount,Long> {
    Optional<UserAccount> findByEmail(String email);
    @Modifying
    void deleteUserAccountsByStatusAndTimeCreatedBefore(String status, Date timeCreated);
    @Modifying
    @Query("UPDATE UserAccount ua SET ua.status = :status WHERE ua.email = :email")
    void updateStatusByEmail(@Param("email") String email, String status);
}
