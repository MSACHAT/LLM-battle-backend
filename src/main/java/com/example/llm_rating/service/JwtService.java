package com.example.llm_rating.service;

import javax.crypto.SecretKey;

import com.example.llm_rating.model.UserEntity;
import org.springframework.stereotype.Service;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;


@Service
public class JwtService {

    private SecretKey key = Jwts.SIG.HS256.key().build();

    public String setToken(UserEntity user) {
        return Jwts
                .builder()
                .subject(user.getId().toString())
                .signWith(key)
                .compact();
    }

    public String decodeJwt(String token) throws JwtException {
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}