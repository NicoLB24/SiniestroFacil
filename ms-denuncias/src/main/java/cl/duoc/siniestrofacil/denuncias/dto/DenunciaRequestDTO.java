package cl.duoc.siniestrofacil.denuncias.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/** Parte del siniestro que envia el asegurado desde la app. */
public record DenunciaRequestDTO(
        @NotBlank(message = "El RUT del asegurado es obligatorio")
        @Pattern(regexp = "^\\d{7,8}-[\\dkK]$", message = "El RUT debe tener el formato 12345678-9") String rutAsegurado,
        @NotBlank(message = "El numero de poliza es obligatorio") String numeroPoliza,
        @NotBlank(message = "La patente es obligatoria")
        @Size(max = 10, message = "La patente no puede superar 10 caracteres") String patente,
        @NotNull(message = "La fecha del siniestro es obligatoria")
        @PastOrPresent(message = "La fecha del siniestro no puede ser futura") LocalDateTime fechaSiniestro,
        @NotBlank(message = "El lugar del siniestro es obligatorio") String lugar,
        String comuna,
        @NotBlank(message = "La descripcion es obligatoria")
        @Size(min = 10, max = 1000, message = "La descripcion debe tener entre 10 y 1000 caracteres") String descripcion,
        @Valid List<FotoDTO> fotos) {
}
