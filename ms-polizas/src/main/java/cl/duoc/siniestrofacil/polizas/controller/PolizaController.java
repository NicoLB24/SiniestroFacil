package cl.duoc.siniestrofacil.polizas.controller;

import cl.duoc.siniestrofacil.polizas.dto.PolizaRequestDTO;
import cl.duoc.siniestrofacil.polizas.dto.PolizaResponseDTO;
import cl.duoc.siniestrofacil.polizas.dto.VigenciaResponseDTO;
import cl.duoc.siniestrofacil.polizas.service.PolizaService;
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
@RequestMapping("/api/v1/polizas")
@Tag(name = "Polizas", description = "Catalogo de polizas y validacion de vigencia")
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    @GetMapping
    @Operation(summary = "Lista las polizas, opcionalmente filtradas por RUT del asegurado")
    public ResponseEntity<List<PolizaResponseDTO>> listar(@RequestParam(required = false) String rut) {
        return ResponseEntity.ok(polizaService.listar(rut));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una poliza por su id")
    public ResponseEntity<PolizaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.obtenerPorId(id));
    }

    @GetMapping("/numero/{numeroPoliza}")
    @Operation(summary = "Obtiene una poliza por su numero")
    public ResponseEntity<PolizaResponseDTO> obtenerPorNumero(@PathVariable String numeroPoliza) {
        return ResponseEntity.ok(polizaService.obtenerPorNumero(numeroPoliza));
    }

    @GetMapping("/numero/{numeroPoliza}/vigencia")
    @Operation(summary = "Valida si la poliza esta vigente a la fecha de hoy")
    public ResponseEntity<VigenciaResponseDTO> validarVigencia(@PathVariable String numeroPoliza) {
        return ResponseEntity.ok(polizaService.validarVigencia(numeroPoliza));
    }

    @PostMapping
    @Operation(summary = "Crea una poliza con sus coberturas")
    public ResponseEntity<PolizaResponseDTO> crear(@Valid @RequestBody PolizaRequestDTO request) {
        PolizaResponseDTO creada = polizaService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza una poliza existente")
    public ResponseEntity<PolizaResponseDTO> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody PolizaRequestDTO request) {
        return ResponseEntity.ok(polizaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una poliza y sus coberturas")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        polizaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
