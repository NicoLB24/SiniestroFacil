package cl.duoc.siniestrofacil.talleres.service;

import cl.duoc.siniestrofacil.talleres.dto.TallerRequestDTO;
import cl.duoc.siniestrofacil.talleres.dto.TallerResponseDTO;

import java.util.List;

public interface TallerService {

    List<TallerResponseDTO> listar(String comuna, boolean soloDisponibles);

    TallerResponseDTO obtenerPorId(Long id);

    TallerResponseDTO crear(TallerRequestDTO request);

    TallerResponseDTO actualizar(Long id, TallerRequestDTO request);

    void eliminar(Long id);
}
