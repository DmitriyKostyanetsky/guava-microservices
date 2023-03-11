package com.kostyanetskiy.orderservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.time.ZonedDateTime;

@Component
public class JwtUtil {

    @Value("${jwt_secret}")
    private String secret;

    public String generateToken(String username) {
        Date expireDate = Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());
        return JWT.create()
                .withSubject("User details")
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withIssuer("order-service")
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateToken(String token) throws JWTVerificationException {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("order-service")
                .build();

        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return decodedJWT.getClaim("username").asString();
    }

}
