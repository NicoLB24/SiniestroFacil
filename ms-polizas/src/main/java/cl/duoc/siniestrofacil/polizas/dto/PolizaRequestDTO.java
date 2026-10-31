package cl.duoc.siniestrofacil.polizas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/** Datos de entrada para crear o actualizar una poliza. */
public record PolizaRequestDTO(
        @NotBlank(message = "El numero de poliza es obligatorio")
        @Size(max = 20, message = "El numero de poliza no puede superar 20 caracteres") String numeroPoliza,
        @NotBlank(message = "El RUT del asegurado es obligatorio")
        @Pattern(regexp = "^\\d{7,8}-[\\dkK]$", message = "El RUT debe tener el formato 12345678-9") String rutAsegurado,
        @NotBlank(message = "El nombre del asegurado es obligatorio") String nombreAsegurado,
        @NotBlank(message = "La patente es obligatoria")
        @Size(max = 10, message = "La patente no puede superar 10 caracteres") String patente,
        @NotNull(message = "La fecha de inicio es obligatoria") LocalDate fechaInicio,
        @NotNull(message = "La fecha de fin es obligatoria") LocalDate fechaFin,
        Boolean activa,
        @Valid List<CoberturaDTO> coberturas) {
}
