package cl.duoc.siniestrofacil.denuncias.exception;

/** Error de regla de negocio: datos con formato valido pero incoherentes (HTTP 409). */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
