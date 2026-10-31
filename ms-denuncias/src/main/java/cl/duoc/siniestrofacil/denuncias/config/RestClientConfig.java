package cl.duoc.siniestrofacil.denuncias.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Clientes HTTP hacia ms-polizas y ms-talleres. Los timeouts cortos evitan
 * que un servicio lento deje colgado el procesamiento de denuncias.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient polizasRestClient(@Value("${servicios.polizas.url}") String url) {
        return RestClient.builder().baseUrl(url).requestFactory(requestFactory()).build();
    }

    @Bean
    public RestClient talleresRestClient(@Value("${servicios.talleres.url}") String url) {
        return RestClient.builder().baseUrl(url).requestFactory(requestFactory()).build();
    }

    private SimpleClientHttpRequestFactory requestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(3));
        return factory;
    }
}
