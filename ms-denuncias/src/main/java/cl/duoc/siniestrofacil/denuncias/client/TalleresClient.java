package cl.duoc.siniestrofacil.denuncias.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Solicita a ms-talleres la asignacion de un taller disponible. */
@Component
public class TalleresClient {

    public record AsignacionRequest(Long denunciaId, String folio, String patente, String comuna) {
    }

    public record AsignacionResponse(Long id, Long tallerId, String tallerNombre, String estado) {
    }

    /** Resultado de intentar asignar: o hay asignacion, o un motivo por el que no se pudo. */
    public record ResultadoAsignacion(AsignacionResponse asignacion, String motivoFallo) {
        public boolean exitosa() {
            return asignacion != null;
        }
    }

    private final RestClient restClient;

    public TalleresClient(@Qualifier("talleresRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Nunca propaga el error: si no hay talleres (409) o el servicio esta caido,
     * la denuncia queda pendiente y se reintenta despues. Asi una falla de
     * talleres no afecta el ingreso de denuncias.
     */
    public ResultadoAsignacion solicitarAsignacion(AsignacionRequest request) {
        try {
            AsignacionResponse respuesta = restClient.post()
                    .uri("/api/v1/asignaciones")
                    .body(request)
                    .retrieve()
                    .body(AsignacionResponse.class);
            return new ResultadoAsignacion(respuesta, null);
        } catch (HttpClientErrorException.Conflict e) {
            return new ResultadoAsignacion(null, "Sin talleres disponibles; se reintentara la asignacion");
        } catch (RestClientException e) {
            return new ResultadoAsignacion(null, "ms-talleres no disponible; se reintentara la asignacion");
        }
    }
}
