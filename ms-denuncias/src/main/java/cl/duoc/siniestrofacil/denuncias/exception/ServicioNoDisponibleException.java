package cl.duoc.siniestrofacil.denuncias.exception;

/** Un servicio del que depende el procesamiento no responde (HTTP 503). */
public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String message) {
        super(message);
    }
}
