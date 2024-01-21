package com.example.insurance.service;

import com.example.insurance.entity.ConfirmCode;
import com.example.insurance.entity.UserAccount;
import com.example.insurance.repository.ConfirmCodeRepository;
import com.example.insurance.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@Service
public class ConfirmCodeService {
    private final ConfirmCodeRepository confirmCodeRepository;
    private final UserAccountRepository userAccountRepository;


    @Autowired
    public ConfirmCodeService(ConfirmCodeRepository confirmCodeRepository, UserAccountRepository userAccountRepository) {
        this.confirmCodeRepository = confirmCodeRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public Optional<ConfirmCode> findByCode(String code)
    {
        return confirmCodeRepository.findByCode(code);
    }

    @Transactional
    public void deleteByCode(String code)
    {
        confirmCodeRepository.deleteByCode(code);
    }

    @Transactional
    public void deleteAllByUserAccount(UserAccount userAccount)
    {
        confirmCodeRepository.deleteAllByUserAccount(userAccount);
    }
    public ConfirmCode createConfirmCode(String email) {
        ConfirmCode confirmCode = new ConfirmCode();
        Optional<UserAccount> userAccount = userAccountRepository.findByEmail(email);
        if(userAccount.isPresent())
        {
            SecureRandom random = new SecureRandom();
            int code = 100000 + random.nextInt(900000); // Tạo mã ngẫu nhiên từ 100000 đến 999999
            confirmCode.setUserAccount(userAccount.get());
            confirmCode.setExpiryDate(Instant.now().plusMillis(1000*60*5));
            confirmCode.setCode(String.valueOf(code));
            confirmCode = confirmCodeRepository.save(confirmCode);
            return confirmCode;
        }
        else {
            throw new UsernameNotFoundException("Account does not exist with email = " + email);
        }

    }

    public boolean isExpiredCode(ConfirmCode code)
    {
        return code.getExpiryDate().compareTo(Instant.now()) < 0;
    }

}
