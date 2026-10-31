package cl.duoc.siniestrofacil.talleres.dto;

import java.time.LocalDateTime;

public record AsignacionResponseDTO(
        Long id,
        Long denunciaId,
        String folio,
        String patente,
        String estado,
        LocalDateTime fechaAsignacion,
        LocalDateTime fechaRecepcion,
        String observacion,
        Long tallerId,
        String tallerNombre) {
}
