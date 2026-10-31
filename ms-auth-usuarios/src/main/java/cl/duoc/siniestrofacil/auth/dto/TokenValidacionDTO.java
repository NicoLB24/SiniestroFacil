package cl.duoc.siniestrofacil.auth.dto;

import java.time.Instant;

/**
 * Resultado de validar un token. Es lo que necesita el autorizador del
 * API Gateway para decidir si deja pasar la peticion y con que rol.
 */
public record TokenValidacionDTO(
        boolean valido,
        Long usuarioId,
        String email,
        String rol,
        String rut,
        Long tallerId,
        Instant expira) {
}
