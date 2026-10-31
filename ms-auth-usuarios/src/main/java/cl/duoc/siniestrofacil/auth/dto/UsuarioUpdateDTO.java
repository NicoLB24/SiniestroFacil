package cl.duoc.siniestrofacil.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos editables de un usuario (el email y la contrasena no se cambian aqui). */
public record UsuarioUpdateDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres") String nombre,
        @Pattern(regexp = "^\\d{7,8}-[\\dkK]$", message = "El RUT debe tener el formato 12345678-9") String rut,
        @NotBlank(message = "El rol es obligatorio") String rol,
        Long tallerId,
        @NotNull(message = "Debe indicar si el usuario esta activo") Boolean activo) {
}
