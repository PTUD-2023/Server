package com.example.insurance.service;

import com.example.insurance.entity.RefreshToken;
import com.example.insurance.entity.UserAccount;
import com.example.insurance.exception.CustomException;
import com.example.insurance.repository.RefreshTokenRepository;
import com.example.insurance.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserAccountRepository userAccountRepository;
    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserAccountRepository userAccountRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(String email) {
        RefreshToken refreshToken = new RefreshToken();
        Optional<UserAccount> userAccount = userAccountRepository.findByEmail(email);
        if(userAccount.isPresent())
        {
            refreshToken.setUserAccount(userAccount.get());
            refreshToken.setExpiryDate(Instant.now().plusMillis(1000*60*60*72));
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken = refreshTokenRepository.save(refreshToken);
            return refreshToken;
        }
        else{
            throw new CustomException(HttpStatus.BAD_REQUEST.value(), "NotFound","User not found");
        }

    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new CustomException(HttpStatus.BAD_REQUEST.value(), "RefreshTokenIsExpired", "Please log in again");
        }

        return token;
    }
}
