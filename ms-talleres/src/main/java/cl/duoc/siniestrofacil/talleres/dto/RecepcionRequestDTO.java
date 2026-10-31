package cl.duoc.siniestrofacil.talleres.dto;

import jakarta.validation.constraints.Size;

/** Datos que envia el taller al confirmar la recepcion del vehiculo. */
public record RecepcionRequestDTO(
        @Size(max = 255, message = "La observacion no puede superar 255 caracteres") String observacion) {
}
