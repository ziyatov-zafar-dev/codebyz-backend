package uz.codebyz.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import uz.codebyz.user.entity.UserRole;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtProperties props;
    private final SecretKey key;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(
                props.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    // ================= ACCESS TOKEN =================
    public String generateAccessToken(
            UUID userId,
            String email,
            UserRole role
    ) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getAccessTokenMinutes() * 60L);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .claim("role", "ROLE_" + role.name()) // 🔥 MUHIM
                .claim("type", "access")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    // ================= REFRESH TOKEN =================
    public String generateRefreshToken(UUID userId, String email) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getRefreshTokenDays() * 24L * 3600L);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .claim("type", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    // ================= PARSE TOKEN =================
    public JwtUser parse(String token) {

        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        UUID userId = UUID.fromString(claims.getSubject());
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);
        String type = claims.get("type", String.class);

        return new JwtUser(userId, email, role, type);
    }
}
