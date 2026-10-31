package cl.duoc.siniestrofacil.polizas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CoberturaDTO(
        Long id,
        @NotBlank(message = "El nombre de la cobertura es obligatorio") String nombre,
        @NotNull(message = "El monto maximo es obligatorio")
        @Positive(message = "El monto maximo debe ser mayor a cero") BigDecimal montoMaximo) {
}
