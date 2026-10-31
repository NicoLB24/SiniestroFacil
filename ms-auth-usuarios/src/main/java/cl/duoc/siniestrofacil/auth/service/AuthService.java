package cl.duoc.siniestrofacil.auth.service;

import cl.duoc.siniestrofacil.auth.dto.LoginRequestDTO;
import cl.duoc.siniestrofacil.auth.dto.LoginResponseDTO;
import cl.duoc.siniestrofacil.auth.dto.RegistroRequestDTO;
import cl.duoc.siniestrofacil.auth.dto.TokenValidacionDTO;
import cl.duoc.siniestrofacil.auth.dto.UsuarioResponseDTO;

public interface AuthService {

    UsuarioResponseDTO registrar(RegistroRequestDTO request);

    LoginResponseDTO login(LoginRequestDTO request);

    TokenValidacionDTO validarToken(String authorizationHeader);
}
