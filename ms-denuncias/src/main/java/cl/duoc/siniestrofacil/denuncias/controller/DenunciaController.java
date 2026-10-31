package cl.duoc.siniestrofacil.denuncias.controller;

import cl.duoc.siniestrofacil.denuncias.dto.CambioEstadoDTO;
import cl.duoc.siniestrofacil.denuncias.dto.DenunciaRequestDTO;
import cl.duoc.siniestrofacil.denuncias.dto.DenunciaResponseDTO;
import cl.duoc.siniestrofacil.denuncias.dto.FotoDTO;
import cl.duoc.siniestrofacil.denuncias.model.EstadoDenuncia;
import cl.duoc.siniestrofacil.denuncias.service.DenunciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/api/v1/denuncias")
@Tag(name = "Denuncias", description = "Registro y consulta de denuncias de siniestros")
public class DenunciaController {

    private final DenunciaService denunciaService;

    public DenunciaController(DenunciaService denunciaService) {
        this.denunciaService = denunciaService;
    }

    @PostMapping
    @Operation(summary = "Registra una denuncia y devuelve de inmediato su folio de recepcion")
    public ResponseEntity<DenunciaResponseDTO> registrar(@Valid @RequestBody DenunciaRequestDTO request) {
        DenunciaResponseDTO creada = denunciaService.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @GetMapping
    @Operation(summary = "Lista denuncias; filtra por RUT del asegurado y/o por estado")
    public ResponseEntity<List<DenunciaResponseDTO>> listar(@RequestParam(required = false) String rut,
                                                            @RequestParam(required = false) EstadoDenuncia estado) {
        return ResponseEntity.ok(denunciaService.listar(rut, estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una denuncia por su id")
    public ResponseEntity<DenunciaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(denunciaService.obtenerPorId(id));
    }

    @GetMapping("/folio/{folio}")
    @Operation(summary = "Obtiene una denuncia por su folio de recepcion")
    public ResponseEntity<DenunciaResponseDTO> obtenerPorFolio(@PathVariable String folio) {
        return ResponseEntity.ok(denunciaService.obtenerPorFolio(folio));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Corrige los datos de una denuncia que aun no fue tomada por un taller")
    public ResponseEntity<DenunciaResponseDTO> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody DenunciaRequestDTO request) {
        return ResponseEntity.ok(denunciaService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualiza el estado de la denuncia (ej: VEHICULO_RECIBIDO, CERRADA)")
    public ResponseEntity<DenunciaResponseDTO> cambiarEstado(@PathVariable Long id,
                                                             @Valid @RequestBody CambioEstadoDTO request) {
        return ResponseEntity.ok(denunciaService.cambiarEstado(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una denuncia que aun no fue tomada por un taller")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        denunciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/fotos")
    @Operation(summary = "Agrega la referencia de una fotografia a la denuncia")
    public ResponseEntity<FotoDTO> agregarFoto(@PathVariable Long id, @Valid @RequestBody FotoDTO foto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(denunciaService.agregarFoto(id, foto));
    }

    @DeleteMapping("/{id}/fotos/{fotoId}")
    @Operation(summary = "Elimina una fotografia de la denuncia")
    public ResponseEntity<Void> eliminarFoto(@PathVariable Long id, @PathVariable Long fotoId) {
        denunciaService.eliminarFoto(id, fotoId);
        return ResponseEntity.noContent().build();
    }
}
