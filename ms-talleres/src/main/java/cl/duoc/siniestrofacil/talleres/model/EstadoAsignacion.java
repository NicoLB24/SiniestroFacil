package cl.duoc.siniestrofacil.talleres.model;

import java.util.List;

/** Ciclo de vida de un caso dentro del taller. */
public enum EstadoAsignacion {
    ASIGNADA,
    RECIBIDO,
    FINALIZADA,
    CANCELADA;

    /** Estados que ocupan capacidad del taller. */
    public static final List<EstadoAsignacion> ACTIVOS = List.of(ASIGNADA, RECIBIDO);
}
