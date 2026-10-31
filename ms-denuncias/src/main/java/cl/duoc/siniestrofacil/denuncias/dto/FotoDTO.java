package cl.duoc.siniestrofacil.denuncias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record FotoDTO(
        Long id,
        @NotBlank(message = "La URL de la foto es obligatoria")
        @Pattern(regexp = "^https?://.+", message = "La URL debe comenzar con http:// o https://")
        @Size(max = 500, message = "La URL no puede superar 500 caracteres") String url,
        @Size(max = 150, message = "La descripcion no puede superar 150 caracteres") String descripcion,
        LocalDateTime fechaCarga) {
}
