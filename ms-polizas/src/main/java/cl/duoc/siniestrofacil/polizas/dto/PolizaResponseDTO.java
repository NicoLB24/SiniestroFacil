package cl.duoc.siniestrofacil.polizas.dto;

import java.time.LocalDate;
import java.util.List;

public record PolizaResponseDTO(
        Long id,
        String numeroPoliza,
        String rutAsegurado,
        String nombreAsegurado,
        String patente,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        boolean activa,
        boolean vigente,
        List<CoberturaDTO> coberturas) {
}
