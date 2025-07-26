package com.jobnote.auth.token;

import com.jobnote.common.api.ResponseCode;
import com.jobnote.common.exception.JobNoteException;
import com.jobnote.common.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.jobnote.common.Constants.*;

@Component
class JwtProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    JwtProvider(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = new SecretKeySpec(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String generateAccessToken(final long userId, final String role, final long expirationTime) {
        return Jwts.builder()
                .claim(CLAIM_NAME_TOKEN_TYPE, CLAIM_VALUE_ACCESS_TOKEN)
                .claim(CLAIM_NAME_USER_ID, userId)
                .claim(CLAIM_NAME_ROLE, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String generateAccessToken(final long userId, final String role) {
        return generateAccessToken(userId, role, jwtProperties.getAccessToken().getExpirationTime());
    }

    public String generateRefreshToken(final long expirationTime) {
        return Jwts.builder()
                .claim(CLAIM_NAME_TOKEN_TYPE, CLAIM_VALUE_REFRESH_TOKEN)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken() {
        return generateRefreshToken(jwtProperties.getRefreshToken().getExpirationTime());
    }

    public Claims getTokenPayload(final String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        } catch (SignatureException e) {
            throw new JobNoteException(ResponseCode.INVALID_TOKEN_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new JobNoteException(ResponseCode.EXPIRED_ACCESS_TOKEN);
        } catch (MalformedKeyException e) {
            throw new JobNoteException(ResponseCode.MALFORMED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new JobNoteException(ResponseCode.UNSUPPORTED_TOKEN);
        } catch (JwtException e) {
            throw new JobNoteException(ResponseCode.INVALID_ACCESS_TOKEN);
        }
    }
}
