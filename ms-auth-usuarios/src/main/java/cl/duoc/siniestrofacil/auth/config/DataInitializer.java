package cl.duoc.siniestrofacil.auth.config;

import cl.duoc.siniestrofacil.auth.model.Rol;
import cl.duoc.siniestrofacil.auth.model.Usuario;
import cl.duoc.siniestrofacil.auth.repository.RolRepository;
import cl.duoc.siniestrofacil.auth.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea usuarios de prueba la primera vez que se levanta el servicio.
 * Se hace aqui y no en Flyway porque la contrasena debe guardarse como hash BCrypt.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }
        crear("Administrador", "admin@siniestrofacil.cl", "Admin123", null, "ADMIN", null);
        crear("Juan Perez Soto", "juan.perez@mail.cl", "Clave123", "12345678-9", "ASEGURADO", null);
        crear("Maria Gonzalez Rojas", "maria.gonzalez@mail.cl", "Clave123", "11111111-1", "ASEGURADO", null);
        crear("Taller Automotriz Central", "taller.central@mail.cl", "Clave123", null, "TALLER", 1L);
        log.info("Usuarios de prueba creados");
    }

    private void crear(String nombre, String email, String password, String rut, String rol, Long tallerId) {
        Rol r = rolRepository.findByNombre(rol).orElseThrow();
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setRut(rut);
        u.setRol(r);
        u.setTallerId(tallerId);
        usuarioRepository.save(u);
    }
}
