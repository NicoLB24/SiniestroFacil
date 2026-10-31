package cl.duoc.siniestrofacil.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos para registrar un usuario. El rol por defecto es ASEGURADO;
 * para el rol TALLER se debe indicar el tallerId.
 */
public record RegistroRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres") String nombre,
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato valido") String email,
        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 6, max = 60, message = "La contrasena debe tener entre 6 y 60 caracteres") String password,
        @Pattern(regexp = "^\\d{7,8}-[\\dkK]$", message = "El RUT debe tener el formato 12345678-9") String rut,
        String rol,
        Long tallerId) {
}
