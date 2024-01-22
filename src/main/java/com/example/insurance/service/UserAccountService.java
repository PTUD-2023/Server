package com.example.insurance.service;

import com.example.insurance.common.MapEntityToDTO;
import com.example.insurance.dto.UserAccountDTO;
import com.example.insurance.entity.UserAccount;
import com.example.insurance.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserAccountService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger("Insurance Server");

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository,@Lazy PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserAccountDTO getUserProfileByEmail(String email) {
        MapEntityToDTO mapEntityToDTO = MapEntityToDTO.getInstance();
        Optional<UserAccount> userAccount = userAccountRepository.findByEmail(email);
        return userAccount.map(mapEntityToDTO::mapUserAccountToDTO).orElse(null);
    }

    public Optional<UserAccount> getUserByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }



    public UserAccount signUpNewAccount(UserAccount userAccount)
    {
        String encodePassword = passwordEncoder.encode(userAccount.getPassword());
        userAccount.setPassword(encodePassword);
        return userAccountRepository.save(userAccount);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserAccount> userAccounts = userAccountRepository.findByEmail(email);
        String password;
        List<GrantedAuthority> authorities;
        if(userAccounts.isEmpty()) {
            throw new UsernameNotFoundException("Account does not exist with email = " + email);
        }
        email = userAccounts.get().getEmail();
        password = userAccounts.get().getPassword();
        authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userAccounts.get().getRole()));

        return new User(email,password,authorities);
    }
    @Transactional
    public void updateStatusByEmail(String email,String status)
    {
        userAccountRepository.updateStatusByEmail(email,status);
    }

    @Transactional
    public void deleteNotConfirmedAccount()
    {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        Date cutoffDate = calendar.getTime();
        userAccountRepository.deleteUserAccountsByStatusAndTimeCreatedBefore("not_activated",cutoffDate);
    }

    public boolean updateUserInforByEmail(UserAccountDTO userAccountDTO) {
        UserAccount userAccount = userAccountRepository.findByEmail(userAccountDTO.getEmail()).orElse(null);
        if(userAccount != null)
        {
            userAccount.setFirstName(userAccountDTO.getFirstName());
            userAccount.setLastName(userAccountDTO.getLastName());
            userAccount.setPhone(userAccountDTO.getPhone());
            userAccount.setBirthday(userAccountDTO.getBirthday());
            userAccount.setGender(userAccountDTO.getGender());
            userAccount.setCmnd(userAccountDTO.getCmnd());
            userAccount.setAddress(userAccountDTO.getAddress());
            userAccountRepository.save(userAccount);
            return true;
        }
        else{
            return false;
        }
    }

    public boolean updatePasswordByEmail(String email,String newPassword) {
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElse(null);
        if(userAccount != null)
        {
            userAccount.setPassword(passwordEncoder.encode(newPassword));
            userAccountRepository.save(userAccount);
            return true;
        }
        else{
            return false;
        }
    }

    @Scheduled( initialDelay = 1000*60*30,fixedRate = 1000*60*60*2)
    @Transactional
    public void cleanupExpiredAccount() {

        deleteNotConfirmedAccount();
        logger.info("Cleaned up all unconfirmed accounts in the database");
    }


}
