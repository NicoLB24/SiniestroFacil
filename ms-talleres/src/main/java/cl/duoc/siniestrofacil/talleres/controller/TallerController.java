package cl.duoc.siniestrofacil.talleres.controller;

import cl.duoc.siniestrofacil.talleres.dto.AsignacionResponseDTO;
import cl.duoc.siniestrofacil.talleres.dto.TallerRequestDTO;
import cl.duoc.siniestrofacil.talleres.dto.TallerResponseDTO;
import cl.duoc.siniestrofacil.talleres.service.AsignacionService;
import cl.duoc.siniestrofacil.talleres.service.TallerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v1/talleres")
@Tag(name = "Talleres", description = "Red de talleres en convenio")
public class TallerController {

    private final TallerService tallerService;
    private final AsignacionService asignacionService;

    public TallerController(TallerService tallerService, AsignacionService asignacionService) {
        this.tallerService = tallerService;
        this.asignacionService = asignacionService;
    }

    @GetMapping
    @Operation(summary = "Lista los talleres; permite filtrar por comuna y por disponibilidad")
    public ResponseEntity<List<TallerResponseDTO>> listar(
            @RequestParam(required = false) String comuna,
            @RequestParam(defaultValue = "false") boolean disponibles) {
        return ResponseEntity.ok(tallerService.listar(comuna, disponibles));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un taller por su id")
    public ResponseEntity<TallerResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(tallerService.obtenerPorId(id));
    }

    @GetMapping("/{id}/denuncias")
    @Operation(summary = "Casos (denuncias) asignados al taller: visibilidad acotada del taller")
    public ResponseEntity<List<AsignacionResponseDTO>> denunciasAsignadas(@PathVariable Long id) {
        return ResponseEntity.ok(asignacionService.listarPorTaller(id));
    }

    @PostMapping
    @Operation(summary = "Registra un taller en convenio")
    public ResponseEntity<TallerResponseDTO> crear(@Valid @RequestBody TallerRequestDTO request) {
        TallerResponseDTO creado = tallerService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza los datos de un taller")
    public ResponseEntity<TallerResponseDTO> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody TallerRequestDTO request) {
        return ResponseEntity.ok(tallerService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un taller sin asignaciones")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tallerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
