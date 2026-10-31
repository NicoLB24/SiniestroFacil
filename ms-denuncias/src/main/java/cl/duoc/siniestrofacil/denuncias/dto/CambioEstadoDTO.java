package cl.duoc.siniestrofacil.denuncias.dto;

import cl.duoc.siniestrofacil.denuncias.model.EstadoDenuncia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Actualizacion de estado informada por otro componente (ej: taller recibio el vehiculo). */
public record CambioEstadoDTO(
        @NotNull(message = "El estado es obligatorio") EstadoDenuncia estado,
        @Size(max = 255, message = "El detalle no puede superar 255 caracteres") String detalle) {
}
