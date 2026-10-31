package cl.duoc.siniestrofacil.polizas.service;

import cl.duoc.siniestrofacil.polizas.dto.PolizaRequestDTO;
import cl.duoc.siniestrofacil.polizas.dto.PolizaResponseDTO;
import cl.duoc.siniestrofacil.polizas.dto.VigenciaResponseDTO;

import java.util.List;

public interface PolizaService {

    List<PolizaResponseDTO> listar(String rutAsegurado);

    PolizaResponseDTO obtenerPorId(Long id);

    PolizaResponseDTO obtenerPorNumero(String numeroPoliza);

    PolizaResponseDTO crear(PolizaRequestDTO request);

    PolizaResponseDTO actualizar(Long id, PolizaRequestDTO request);

    void eliminar(Long id);

    VigenciaResponseDTO validarVigencia(String numeroPoliza);
}
