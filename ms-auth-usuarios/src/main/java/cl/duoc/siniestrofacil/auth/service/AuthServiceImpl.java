package cl.duoc.siniestrofacil.auth.service;

import cl.duoc.siniestrofacil.auth.dto.LoginRequestDTO;
import cl.duoc.siniestrofacil.auth.dto.LoginResponseDTO;
import cl.duoc.siniestrofacil.auth.dto.RegistroRequestDTO;
import cl.duoc.siniestrofacil.auth.dto.TokenValidacionDTO;
import cl.duoc.siniestrofacil.auth.dto.UsuarioResponseDTO;
import cl.duoc.siniestrofacil.auth.exception.BusinessException;
import cl.duoc.siniestrofacil.auth.exception.ResourceNotFoundException;
import cl.duoc.siniestrofacil.auth.exception.UnauthorizedException;
import cl.duoc.siniestrofacil.auth.model.Rol;
import cl.duoc.siniestrofacil.auth.model.Usuario;
import cl.duoc.siniestrofacil.auth.repository.RolRepository;
import cl.duoc.siniestrofacil.auth.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final String BEARER = "Bearer ";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                           UsuarioService usuarioService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UsuarioResponseDTO registrar(RegistroRequestDTO request) {
        String email = request.email().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new BusinessException("Ya existe un usuario registrado con el email " + email);
        }
        String nombreRol = request.rol() == null ? "ASEGURADO" : request.rol().toUpperCase();
        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el rol " + nombreRol));
        if ("ASEGURADO".equals(rol.getNombre()) && request.rut() == null) {
            throw new BusinessException("Un asegurado debe registrar su RUT");
        }
        if ("TALLER".equals(rol.getNombre()) && request.tallerId() == null) {
            throw new BusinessException("Un usuario con rol TALLER debe indicar el tallerId");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setRut(request.rut());
        usuario.setRol(rol);
        usuario.setTallerId(request.tallerId());
        return usuarioService.toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email().toLowerCase())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new UnauthorizedException("Email o contrasena incorrectos"));
        if (!usuario.isActivo()) {
            throw new UnauthorizedException("El usuario esta desactivado");
        }
        String token = jwtService.generarToken(usuario);
        return new LoginResponseDTO(token, "Bearer", jwtService.getExpiracionSegundos(),
                usuarioService.toResponse(usuario));
    }

    @Override
    public TokenValidacionDTO validarToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER)) {
            throw new UnauthorizedException("Debe enviar el header Authorization: Bearer <token>");
        }
        return jwtService.validar(authorizationHeader.substring(BEARER.length()));
    }
}
