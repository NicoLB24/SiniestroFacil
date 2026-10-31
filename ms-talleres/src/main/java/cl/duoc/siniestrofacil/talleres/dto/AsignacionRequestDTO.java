package cl.duoc.siniestrofacil.talleres.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Solicitud de asignacion que envia el procesador de denuncias.
 * La comuna es opcional y se usa para preferir un taller cercano.
 */
public record AsignacionRequestDTO(
        @NotNull(message = "El id de la denuncia es obligatorio") Long denunciaId,
        @NotBlank(message = "El folio es obligatorio") String folio,
        @NotBlank(message = "La patente es obligatoria") String patente,
        String comuna) {
}
