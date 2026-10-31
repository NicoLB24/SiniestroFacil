package cl.duoc.siniestrofacil.auth.service;

import cl.duoc.siniestrofacil.auth.dto.TokenValidacionDTO;
import cl.duoc.siniestrofacil.auth.exception.UnauthorizedException;
import cl.duoc.siniestrofacil.auth.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/** Emite y valida los tokens JWT firmados con HMAC-SHA256. */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expiracionSegundos;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-seconds}") long expiracionSegundos) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracionSegundos = expiracionSegundos;
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getEmail())
                .issuer("ms-auth-usuarios")
                .claim("uid", usuario.getId())
                .claim("rol", usuario.getRol().getNombre())
                .claim("rut", usuario.getRut())
                .claim("tallerId", usuario.getTallerId())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusSeconds(expiracionSegundos)))
                .signWith(key)
                .compact();
    }

    public TokenValidacionDTO validar(String token) {
        try {
            Claims c = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            Number uid = c.get("uid", Number.class);
            Number tallerId = c.get("tallerId", Number.class);
            return new TokenValidacionDTO(true,
                    uid != null ? uid.longValue() : null,
                    c.getSubject(),
                    c.get("rol", String.class),
                    c.get("rut", String.class),
                    tallerId != null ? tallerId.longValue() : null,
                    c.getExpiration().toInstant());
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Token invalido o expirado");
        }
    }

    public long getExpiracionSegundos() {
        return expiracionSegundos;
    }
}
