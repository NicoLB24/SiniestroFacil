package cl.duoc.siniestrofacil.denuncias;

import cl.duoc.siniestrofacil.denuncias.client.PolizasClient;
import cl.duoc.siniestrofacil.denuncias.client.TalleresClient;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/** Verifica el contrato HTTP con ms-polizas y ms-talleres sin levantarlos. */
class ClientesHttpTest {

    @Test
    void polizasClientLeeLaVigenciaYTrata404ComoInexistente() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://polizas");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        PolizasClient client = new PolizasClient(builder.build());

        server.expect(requestTo("http://polizas/api/v1/polizas/numero/POL-1001/vigencia"))
                .andRespond(withSuccess("""
                        {"numeroPoliza":"POL-1001","patente":"ABCD12","vigente":true,
                         "fechaConsulta":"2025-10-30","mensaje":"Poliza vigente"}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo("http://polizas/api/v1/polizas/numero/NO-EXISTE/vigencia"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThat(client.consultarVigencia("POL-1001")).hasValueSatisfying(v -> assertThat(v.vigente()).isTrue());
        assertThat(client.consultarVigencia("NO-EXISTE")).isEmpty();
        server.verify();
    }

    @Test
    void talleresClientDistingueAsignacionExitosaYSinCapacidad() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://talleres");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        TalleresClient client = new TalleresClient(builder.build());
        TalleresClient.AsignacionRequest request =
                new TalleresClient.AsignacionRequest(1L, "SF-20251030-AAAA1111", "ABCD12", "Santiago");

        server.expect(requestTo("http://talleres/api/v1/asignaciones"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.folio").value("SF-20251030-AAAA1111"))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body("""
                        {"id":7,"denunciaId":1,"folio":"SF-20251030-AAAA1111","estado":"ASIGNADA",
                         "tallerId":1,"tallerNombre":"Taller Automotriz Central"}
                        """));
        server.expect(requestTo("http://talleres/api/v1/asignaciones"))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        TalleresClient.ResultadoAsignacion ok = client.solicitarAsignacion(request);
        assertThat(ok.exitosa()).isTrue();
        assertThat(ok.asignacion().tallerId()).isEqualTo(1L);

        TalleresClient.ResultadoAsignacion sinTaller = client.solicitarAsignacion(request);
        assertThat(sinTaller.exitosa()).isFalse();
        assertThat(sinTaller.motivoFallo()).contains("Sin talleres disponibles");
        server.verify();
    }
}
