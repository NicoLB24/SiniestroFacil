package cl.duoc.siniestrofacil.auth.dto;

import java.time.LocalDateTime;

/** Representacion publica del usuario: nunca expone el hash de la contrasena. */
public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String email,
        String rut,
        String rol,
        Long tallerId,
        boolean activo,
        LocalDateTime fechaCreacion) {
}
