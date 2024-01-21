package com.example.insurance.repository;

import com.example.insurance.entity.ConfirmCode;
import com.example.insurance.entity.UserAccount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfirmCodeRepository extends CrudRepository<ConfirmCode,Long> {
    Optional<ConfirmCode> findByCode(String code);
    List<ConfirmCode> findAll();
    @Modifying
    void deleteByCode(String code);
    @Modifying
    void deleteAllByUserAccount(UserAccount userAccount);
}
