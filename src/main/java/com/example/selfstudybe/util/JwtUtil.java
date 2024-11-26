package com.example.selfstudybe.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

public class JwtUtil {
    @Value("${jwt.key}")
    public static String KEY;

    @Value("${jwt.issuer}")
    public static String ISSUER;

    public static JwtDecoder getJwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(KEY.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    public static String generateAccessToken(UUID userId, String username, String role) throws JOSEException {
        Date now = new Date();
        long duration = 15*60*1000;
        Date exp = new Date(now.getTime() + duration);

        // Create claims
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("role",role)
                .claim("type","access")
                .issuer(ISSUER)
                .issueTime(now)
                .expirationTime(exp)
                .build();

        //Create header and payload
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JWSObject jwsObject = new JWSObject(header, new Payload(claims.toJSONObject()));
        MACSigner signer = new MACSigner(KEY.getBytes());
        jwsObject.sign(signer);

        return jwsObject.serialize();
    }

    public static String generateRefreshToken(UUID userId) throws JOSEException {
        Date now = new Date();
        long duration = 7L*24*60*60*1000;
        Date exp = new Date(now.getTime() + duration);

        //Create claims
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userId.toString())
                .claim("type", "refresh")
                .issuer(ISSUER)
                .issueTime(now)
                .expirationTime(exp)
                .build();

        //Create header and payload
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JWSObject jwsObject = new JWSObject(header, new Payload(claims.toJSONObject()));
        MACSigner signer = new MACSigner(KEY.getBytes());
        jwsObject.sign(signer);

        return jwsObject.serialize();
    }

    public static String extractAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null)
            for(Cookie cookie : cookies)
                if(cookie.getName().equals("access_token"))
                    return cookie.getValue();
        return null;
    }

    public static String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null)
            for (Cookie cookie : cookies)
                if(cookie.getName().equals("refresh_token"))
                    return cookie.getValue();
        return null;
    }

    public static boolean validateAccessToken(String token) {
        try {
            JwtDecoder jwtDecoder = getJwtDecoder();
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaimAsString("type").equals("access");
        }
        catch (JwtValidationException e) {
            return false;
        }
    }

    public static boolean validateRefreshToken(String token) {
        try {
            JwtDecoder jwtDecoder = getJwtDecoder();
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaimAsString("type").equals("refresh");
        }
        catch (JwtValidationException e) {
            return false;
        }
    }

    public static Cookie generateCookie(String name, String token)
    {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(86400); // 1 day
        cookie.setPath("/"); // Cookie valid with all end point
        return cookie;
    }
}
