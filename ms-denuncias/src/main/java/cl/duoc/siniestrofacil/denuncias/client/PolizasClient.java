package cl.duoc.siniestrofacil.denuncias.client;

import cl.duoc.siniestrofacil.denuncias.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

/** Consulta a ms-polizas la vigencia de una poliza. */
@Component
public class PolizasClient {

    public record VigenciaResponse(String numeroPoliza, String patente, boolean vigente, String mensaje) {
    }

    private final RestClient restClient;

    public PolizasClient(@Qualifier("polizasRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /** Devuelve vacio si la poliza no existe; lanza 503 si ms-polizas no responde. */
    public Optional<VigenciaResponse> consultarVigencia(String numeroPoliza) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/api/v1/polizas/numero/{numero}/vigencia", numeroPoliza)
                    .retrieve()
                    .body(VigenciaResponse.class));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw new ServicioNoDisponibleException("ms-polizas respondio con error " + e.getStatusCode().value());
        } catch (RestClientException e) {
            throw new ServicioNoDisponibleException("ms-polizas no esta disponible, intente nuevamente");
        }
    }
}
