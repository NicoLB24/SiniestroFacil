package cl.duoc.siniestrofacil.denuncias.service;

import cl.duoc.siniestrofacil.denuncias.dto.CambioEstadoDTO;
import cl.duoc.siniestrofacil.denuncias.dto.DenunciaRequestDTO;
import cl.duoc.siniestrofacil.denuncias.dto.DenunciaResponseDTO;
import cl.duoc.siniestrofacil.denuncias.dto.FotoDTO;
import cl.duoc.siniestrofacil.denuncias.model.EstadoDenuncia;

import java.util.List;

public interface DenunciaService {

    DenunciaResponseDTO registrar(DenunciaRequestDTO request);

    List<DenunciaResponseDTO> listar(String rutAsegurado, EstadoDenuncia estado);

    DenunciaResponseDTO obtenerPorId(Long id);

    DenunciaResponseDTO obtenerPorFolio(String folio);

    DenunciaResponseDTO actualizar(Long id, DenunciaRequestDTO request);

    DenunciaResponseDTO cambiarEstado(Long id, CambioEstadoDTO request);

    void eliminar(Long id);

    FotoDTO agregarFoto(Long denunciaId, FotoDTO foto);

    void eliminarFoto(Long denunciaId, Long fotoId);
}
