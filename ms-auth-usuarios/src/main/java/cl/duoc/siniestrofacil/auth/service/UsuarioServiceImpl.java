package cl.duoc.siniestrofacil.auth.service;

import cl.duoc.siniestrofacil.auth.dto.RolDTO;
import cl.duoc.siniestrofacil.auth.dto.UsuarioResponseDTO;
import cl.duoc.siniestrofacil.auth.dto.UsuarioUpdateDTO;
import cl.duoc.siniestrofacil.auth.exception.BusinessException;
import cl.duoc.siniestrofacil.auth.exception.ResourceNotFoundException;
import cl.duoc.siniestrofacil.auth.model.Rol;
import cl.duoc.siniestrofacil.auth.model.Usuario;
import cl.duoc.siniestrofacil.auth.repository.RolRepository;
import cl.duoc.siniestrofacil.auth.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar(String rol) {
        List<Usuario> usuarios = (rol == null || rol.isBlank())
                ? usuarioRepository.findAll()
                : usuarioRepository.findByRol_Nombre(rol.toUpperCase());
        return usuarios.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    public UsuarioResponseDTO actualizar(Long id, UsuarioUpdateDTO request) {
        Usuario usuario = buscar(id);
        Rol rol = buscarRol(request.rol());
        if ("TALLER".equals(rol.getNombre()) && request.tallerId() == null) {
            throw new BusinessException("Un usuario con rol TALLER debe tener tallerId");
        }
        usuario.setNombre(request.nombre());
        usuario.setRut(request.rut());
        usuario.setRol(rol);
        usuario.setTallerId(request.tallerId());
        usuario.setActivo(request.activo());
        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.delete(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> listarRoles() {
        return rolRepository.findAll().stream()
                .map(r -> new RolDTO(r.getId(), r.getNombre(), r.getDescripcion(), r.getUsuarios().size()))
                .toList();
    }

    @Override
    public UsuarioResponseDTO toResponse(Usuario u) {
        return new UsuarioResponseDTO(u.getId(), u.getNombre(), u.getEmail(), u.getRut(),
                u.getRol().getNombre(), u.getTallerId(), u.isActivo(), u.getFechaCreacion());
    }

    private Rol buscarRol(String nombre) {
        return rolRepository.findByNombre(nombre.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el rol " + nombre));
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con id " + id));
    }
}
