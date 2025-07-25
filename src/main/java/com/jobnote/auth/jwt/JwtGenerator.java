package com.jobnote.auth.jwt;

import com.jobnote.common.api.ResponseCode;
import com.jobnote.common.exception.JobNoteException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.jobnote.common.Constants.*;

@Component
public class JwtGenerator {

    private final SecretKey secretKey;

    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    public JwtGenerator(@Value("${jwt.secret}") final String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String generateAccessToken(final long userId, final String role) {
        return Jwts.builder()
                .claim(CLAIM_NAME_TOKEN, CLAIM_VALUE_ACCESS_TOKEN)
                .claim(CLAIM_NAME_USER_ID, userId)
                .claim(CLAIM_NAME_ROLE, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken() {
        return Jwts.builder()
                .claim(CLAIM_NAME_TOKEN, CLAIM_VALUE_REFRESH_TOKEN)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(secretKey)
                .compact();
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
