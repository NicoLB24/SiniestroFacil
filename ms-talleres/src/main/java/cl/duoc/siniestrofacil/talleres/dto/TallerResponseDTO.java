package cl.duoc.siniestrofacil.talleres.dto;

public record TallerResponseDTO(
        Long id,
        String nombre,
        String direccion,
        String comuna,
        String telefono,
        String email,
        int capacidadMaxima,
        long casosActivos,
        boolean disponible,
        boolean activo) {
}
