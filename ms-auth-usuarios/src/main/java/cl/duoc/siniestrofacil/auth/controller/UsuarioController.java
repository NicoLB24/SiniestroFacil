package cl.duoc.siniestrofacil.auth.controller;

import cl.duoc.siniestrofacil.auth.dto.RolDTO;
import cl.duoc.siniestrofacil.auth.dto.UsuarioResponseDTO;
import cl.duoc.siniestrofacil.auth.dto.UsuarioUpdateDTO;
import cl.duoc.siniestrofacil.auth.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Usuarios", description = "Administracion de usuarios y roles")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Lista los usuarios, opcionalmente filtrados por rol")
    public ResponseEntity<List<UsuarioResponseDTO>> listar(@RequestParam(required = false) String rol) {
        return ResponseEntity.ok(usuarioService.listar(rol));
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Obtiene un usuario por su id")
    public ResponseEntity<UsuarioResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PutMapping("/usuarios/{id}")
    @Operation(summary = "Actualiza nombre, RUT, rol, taller y estado de un usuario")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody UsuarioUpdateDTO request) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    @DeleteMapping("/usuarios/{id}")
    @Operation(summary = "Elimina un usuario")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    @Operation(summary = "Lista los roles disponibles")
    public ResponseEntity<List<RolDTO>> listarRoles() {
        return ResponseEntity.ok(usuarioService.listarRoles());
    }
}
