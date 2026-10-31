package cl.duoc.siniestrofacil.talleres.service;

import cl.duoc.siniestrofacil.talleres.dto.AsignacionRequestDTO;
import cl.duoc.siniestrofacil.talleres.dto.AsignacionResponseDTO;
import cl.duoc.siniestrofacil.talleres.dto.RecepcionRequestDTO;

import java.util.List;

public interface AsignacionService {

    AsignacionResponseDTO asignar(AsignacionRequestDTO request);

    List<AsignacionResponseDTO> listarPorTaller(Long tallerId);

    List<AsignacionResponseDTO> listarPorDenuncia(Long denunciaId);

    AsignacionResponseDTO obtenerPorId(Long id);

    AsignacionResponseDTO confirmarRecepcion(Long id, RecepcionRequestDTO request);

    AsignacionResponseDTO finalizar(Long id);

    AsignacionResponseDTO cancelar(Long id);
}
