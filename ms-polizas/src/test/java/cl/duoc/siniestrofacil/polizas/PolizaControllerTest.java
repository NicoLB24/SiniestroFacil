package cl.duoc.siniestrofacil.polizas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PolizaControllerTest {

    private static final String BASE = "/api/v1/polizas";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listaLasPolizasCargadasPorFlyway() throws Exception {
        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(4))));
    }

    @Test
    void validaVigenciaDePolizaVigenteYVencida() throws Exception {
        mockMvc.perform(get(BASE + "/numero/POL-1001/vigencia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vigente").value(true));
        mockMvc.perform(get(BASE + "/numero/POL-1003/vigencia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vigente").value(false));
    }

    @Test
    void devuelve404SiLaPolizaNoExiste() throws Exception {
        mockMvc.perform(get(BASE + "/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void devuelve400SiFaltanCamposObligatorios() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("{\"numeroPoliza\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.rutAsegurado").exists());
    }

    @Test
    void cicloCrudCompleto() throws Exception {
        String body = """
                {"numeroPoliza":"POL-TEST","rutAsegurado":"44444444-4","nombreAsegurado":"Test Uno",
                 "patente":"test11","fechaInicio":"2025-01-01","fechaFin":"2030-01-01",
                 "coberturas":[{"nombre":"Danos propios","montoMaximo":5000000}]}
                """;
        String location = mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.patente").value("TEST11"))
                .andExpect(jsonPath("$.coberturas", hasSize(1)))
                .andReturn().getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf('/') + 1);

        // numero de poliza duplicado
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());

        mockMvc.perform(put(BASE + "/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(body.replace("Test Uno", "Test Editado")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreAsegurado").value("Test Editado"));

        mockMvc.perform(delete(BASE + "/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get(BASE + "/" + id)).andExpect(status().isNotFound());
    }
}
