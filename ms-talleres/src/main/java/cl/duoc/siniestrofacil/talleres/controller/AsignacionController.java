package cl.duoc.siniestrofacil.talleres.controller;

import cl.duoc.siniestrofacil.talleres.dto.AsignacionRequestDTO;
import cl.duoc.siniestrofacil.talleres.dto.AsignacionResponseDTO;
import cl.duoc.siniestrofacil.talleres.dto.RecepcionRequestDTO;
import cl.duoc.siniestrofacil.talleres.service.AsignacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/asignaciones")
@Tag(name = "Asignaciones", description = "Asignacion de denuncias a talleres y confirmacion de recepcion")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @PostMapping
    @Operation(summary = "Asigna la denuncia a un taller disponible (lo invoca el procesador de denuncias)")
    public ResponseEntity<AsignacionResponseDTO> asignar(@Valid @RequestBody AsignacionRequestDTO request) {
        AsignacionResponseDTO creada = asignacionService.asignar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @GetMapping
    @Operation(summary = "Busca las asignaciones de una denuncia")
    public ResponseEntity<List<AsignacionResponseDTO>> listarPorDenuncia(@RequestParam Long denunciaId) {
        return ResponseEntity.ok(asignacionService.listarPorDenuncia(denunciaId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una asignacion por su id")
    public ResponseEntity<AsignacionResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(asignacionService.obtenerPorId(id));
    }

    @PutMapping("/{id}/recepcion")
    @Operation(summary = "El taller confirma la recepcion del vehiculo")
    public ResponseEntity<AsignacionResponseDTO> confirmarRecepcion(
            @PathVariable Long id, @Valid @RequestBody(required = false) RecepcionRequestDTO request) {
        return ResponseEntity.ok(asignacionService.confirmarRecepcion(id, request));
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Marca la reparacion como finalizada y libera capacidad del taller")
    public ResponseEntity<AsignacionResponseDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(asignacionService.finalizar(id));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancela una asignacion que aun no ha sido recibida")
    public ResponseEntity<AsignacionResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(asignacionService.cancelar(id));
    }
}
