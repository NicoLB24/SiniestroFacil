package cl.duoc.siniestrofacil.denuncias.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DenunciaResponseDTO(
        Long id,
        String folio,
        String rutAsegurado,
        String numeroPoliza,
        String patente,
        LocalDateTime fechaSiniestro,
        String lugar,
        String comuna,
        String descripcion,
        String estado,
        String detalleEstado,
        Long tallerId,
        Long asignacionId,
        LocalDateTime fechaRegistro,
        LocalDateTime fechaActualizacion,
        List<FotoDTO> fotos) {
}
