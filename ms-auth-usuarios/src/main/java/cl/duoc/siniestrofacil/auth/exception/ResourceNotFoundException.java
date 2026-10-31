package cl.duoc.siniestrofacil.auth.exception;

/** Se lanza cuando el recurso solicitado no existe (HTTP 404). */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
