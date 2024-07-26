package com.hrm.books.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import static com.hrm.books.utilities.constants.Constants.SECRET_KEY;
import static com.hrm.books.utilities.constants.Constants.TIME_EXPIRATION;

@Service
public class JWTService {
//    @Value("${secret.key}")
//    private String SECRET_KEY_PROP;
    private final SecretKey secretKey;
    /**
     * Two ways generate secret key:<br/>
     * 1) Use Jwts.SIG.HS512.key().build();<br/>
     * 2) Use exist string secret key
     */
    public JWTService() {
        //solution 1:
//        SecretKey key = Jwts.SIG.HS512.key().build();
//        String hexBinary = DatatypeConverter.printHexBinary(key.getEncoded());
//        System.out.printf("$1 Key = %s", hexBinary);
//        String encodedKey = Base64.getEncoder().encodeToString(SECRET_KEY_PROP.getBytes());
        String encodedKey = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
        byte[] bytes = Base64.getDecoder().decode(encodedKey);
        secretKey = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * create token
     */
    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(TIME_EXPIRATION)))
//                .signWith(secretKey)
                .signWith(secretKey, Jwts.SIG.HS384)
                .compact();
    }

    //tao refresh token, éo xài
//    public String refreshToken(Map<String, Object> claims, UserDetails userDetails) {
//        return Jwts.builder()
//                .claims(claims)
//                .subject(userDetails.getUsername())
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(key)
//                .compact();
//    }

    public String extractUsername(String token) {
        return extractClaims(token, claims -> claims.getSubject());
    }

    /**
     * @Description
     * Từ token và khóa bí mật
     * láy thông tin user đăng nhập
     */
    private <T> T extractClaims(String token, Function<Claims, T> claimsFunction) {
        return claimsFunction.apply(
                Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        );
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     xem thu token het han chua
     */
    public boolean isTokenExpired(String token) {
//        return extractClaims(token, Claims::getExpiration)
//                .before(Date.from(Instant.now()));
        return extractClaims(token, claims -> {
            return claims.getExpiration().before(Date.from(Instant.now()));
        });
    }
}
