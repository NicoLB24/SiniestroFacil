package cl.duoc.siniestrofacil.polizas.dto;

import java.time.LocalDate;

/** Respuesta que usa el procesador de denuncias para decidir si asigna taller. */
public record VigenciaResponseDTO(
        String numeroPoliza,
        String patente,
        boolean vigente,
        LocalDate fechaConsulta,
        String mensaje) {
}
