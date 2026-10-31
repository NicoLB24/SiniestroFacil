package cl.duoc.siniestrofacil.auth.controller;

import cl.duoc.siniestrofacil.auth.dto.LoginRequestDTO;
import cl.duoc.siniestrofacil.auth.dto.LoginResponseDTO;
import cl.duoc.siniestrofacil.auth.dto.RegistroRequestDTO;
import cl.duoc.siniestrofacil.auth.dto.TokenValidacionDTO;
import cl.duoc.siniestrofacil.auth.dto.UsuarioResponseDTO;
import cl.duoc.siniestrofacil.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticacion", description = "Registro, login y validacion de tokens JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    @Operation(summary = "Registra un usuario (por defecto con rol ASEGURADO)")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica al usuario y emite un token JWT")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/validar")
    @Operation(summary = "Valida un token JWT (lo usa el autorizador del API Gateway)")
    public ResponseEntity<TokenValidacionDTO> validar(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return ResponseEntity.ok(authService.validarToken(authorization));
    }
}
