package cl.duoc.siniestrofacil.talleres.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TallerRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres") String nombre,
        @NotBlank(message = "La direccion es obligatoria") String direccion,
        @NotBlank(message = "La comuna es obligatoria") String comuna,
        @Size(max = 20, message = "El telefono no puede superar 20 caracteres") String telefono,
        @Email(message = "El email no tiene un formato valido") String email,
        @NotNull(message = "La capacidad maxima es obligatoria")
        @Min(value = 1, message = "La capacidad minima es 1")
        @Max(value = 100, message = "La capacidad maxima es 100") Integer capacidadMaxima,
        Boolean activo) {
}
