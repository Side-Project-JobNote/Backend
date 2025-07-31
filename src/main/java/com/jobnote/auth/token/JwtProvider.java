package com.jobnote.auth.token;

import com.jobnote.global.common.ResponseCode;
import com.jobnote.global.exception.JobNoteException;
import com.jobnote.global.config.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.jobnote.global.common.Constants.*;

@Component
class JwtProvider {

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
                .claim(CLAIM_NAME_TOKEN_TYPE, CLAIM_VALUE_REFRESH_TOKEN)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken() {
        return generateRefreshToken(jwtProperties.refreshToken().expirationTime());
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
