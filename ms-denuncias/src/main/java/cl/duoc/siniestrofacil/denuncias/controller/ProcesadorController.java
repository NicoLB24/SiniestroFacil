package cl.duoc.siniestrofacil.denuncias.controller;

import cl.duoc.siniestrofacil.denuncias.dto.DenunciaResponseDTO;
import cl.duoc.siniestrofacil.denuncias.service.ProcesadorDenunciasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Dispara manualmente el procesamiento asincrono. En AWS lo haria la Lambda
 * al leer la cola SQS; en local se invoca desde Postman.
 */
@RestController
@RequestMapping("/api/v1/procesador")
@Tag(name = "Procesador", description = "Simulacion local de SQS + Lambda: valida poliza y asigna taller")
public class ProcesadorController {

    private final ProcesadorDenunciasService procesadorService;

    public ProcesadorController(ProcesadorDenunciasService procesadorService) {
        this.procesadorService = procesadorService;
    }

    @PostMapping("/denuncias/{id}")
    @Operation(summary = "Procesa una denuncia: valida vigencia en ms-polizas y pide taller a ms-talleres")
    public ResponseEntity<DenunciaResponseDTO> procesar(@PathVariable Long id) {
        return ResponseEntity.ok(procesadorService.procesar(id));
    }

    @PostMapping("/pendientes")
    @Operation(summary = "Procesa todas las denuncias RECIBIDAS o PENDIENTE_TALLER en orden de llegada")
    public ResponseEntity<List<DenunciaResponseDTO>> procesarPendientes() {
        return ResponseEntity.ok(procesadorService.procesarPendientes());
    }
}
