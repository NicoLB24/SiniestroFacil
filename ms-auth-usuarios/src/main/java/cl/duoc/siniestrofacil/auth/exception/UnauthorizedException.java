package cl.duoc.siniestrofacil.auth.exception;

/** Credenciales o token invalidos (HTTP 401). */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
