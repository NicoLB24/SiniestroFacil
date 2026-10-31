package cl.duoc.siniestrofacil.denuncias.model;

/**
 * Estados de la denuncia. RECIBIDA es el unico estado que se asigna de forma
 * sincrona; el resto lo define el procesamiento asincrono posterior.
 */
public enum EstadoDenuncia {
    RECIBIDA,
    RECHAZADA,
    PENDIENTE_TALLER,
    TALLER_ASIGNADO,
    VEHICULO_RECIBIDO,
    CERRADA
}
