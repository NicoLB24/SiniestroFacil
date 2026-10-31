package cl.duoc.siniestrofacil.auth.dto;

public record LoginResponseDTO(
        String token,
        String tipo,
        long expiraEnSegundos,
        UsuarioResponseDTO usuario) {
}
