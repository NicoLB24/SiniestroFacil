package cl.duoc.siniestrofacil.auth.service;

import cl.duoc.siniestrofacil.auth.dto.RolDTO;
import cl.duoc.siniestrofacil.auth.dto.UsuarioResponseDTO;
import cl.duoc.siniestrofacil.auth.dto.UsuarioUpdateDTO;
import cl.duoc.siniestrofacil.auth.model.Usuario;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponseDTO> listar(String rol);

    UsuarioResponseDTO obtenerPorId(Long id);

    UsuarioResponseDTO actualizar(Long id, UsuarioUpdateDTO request);

    void eliminar(Long id);

    List<RolDTO> listarRoles();

    UsuarioResponseDTO toResponse(Usuario usuario);
}
