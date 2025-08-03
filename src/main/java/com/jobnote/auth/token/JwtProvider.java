package com.jobnote.auth.token;

import com.jobnote.global.common.ResponseCode;
import com.jobnote.global.exception.JobNoteException;
import com.jobnote.global.config.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static com.jobnote.global.common.Constants.*;
import static com.jobnote.global.common.ResponseCode.INVALID_TOKEN_TYPE;

@Component
class JwtProvider {

    @Getter
    private final JwtProperties jwtProperties;

    private final SecretKey secretKey;

    JwtProvider(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = new SecretKeySpec(jwtProperties.secret().getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String generateAccessToken(final TokenClaim tokenClaim, final long expirationTime) {
        return Jwts.builder()
                .claim(CLAIM_NAME_TOKEN_TYPE, CLAIM_VALUE_ACCESS_TOKEN)
                .claim(CLAIM_NAME_EMAIL, tokenClaim.email())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String generateAccessToken(final TokenClaim tokenClaim) {
        return generateAccessToken(tokenClaim, jwtProperties.accessToken().expirationTime());
    }

    public String generateRefreshToken(final long expirationTime) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claim(CLAIM_NAME_TOKEN_TYPE, CLAIM_VALUE_REFRESH_TOKEN)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken() {
        return generateRefreshToken(jwtProperties.refreshToken().expirationTime());
    }

    public Claims validateAndGetTokenPayload(final String token, final String expectedTokenType) {
        try {
            Claims payload = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
            String tokenType = payload.get(CLAIM_NAME_TOKEN_TYPE, String.class);
            if (!expectedTokenType.equals(tokenType)) {
                throw new JobNoteException(INVALID_TOKEN_TYPE);
            }
            return payload;
        } catch (SignatureException e) {
            throw new JobNoteException(ResponseCode.INVALID_TOKEN_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new JobNoteException(ResponseCode.EXPIRED_TOKEN);
        } catch (MalformedKeyException e) {
            throw new JobNoteException(ResponseCode.MALFORMED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new JobNoteException(ResponseCode.UNSUPPORTED_TOKEN);
        } catch (JwtException e) {
            throw new JobNoteException(ResponseCode.INVALID_TOKEN);
        }
    }
}
