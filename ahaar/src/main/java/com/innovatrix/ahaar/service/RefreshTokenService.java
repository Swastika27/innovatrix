package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.RefreshToken;
import com.innovatrix.ahaar.repository.RefreshTokenRepository;
import com.innovatrix.ahaar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;

    private final long timeout = 1000 * 60 * 5; // 5 minute
    @Autowired
    private UserService userService;

    public RefreshToken createRefreshToken(String username) {
        Optional<ApplicationUser> user = userRepository.findByUserName(username);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Optional<RefreshToken> previousToken = refreshTokenRepository.findByUser(user.get());

        if(previousToken.isPresent() && previousToken.get().getExpiryDate().isAfter(Instant.now())) {
            return previousToken.get();
        }

        if(previousToken.isPresent() && previousToken.get().getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(previousToken.get());
        }
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findByUserName(username).get())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(timeout))//10
                .build();
        return refreshTokenRepository.save(refreshToken);
    }


    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

}

