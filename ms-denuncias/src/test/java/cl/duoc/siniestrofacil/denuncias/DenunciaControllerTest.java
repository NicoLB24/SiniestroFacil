package cl.duoc.siniestrofacil.denuncias;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // cada prueba revierte sus cambios en la base
class DenunciaControllerTest {

    private static final String BASE = "/api/v1/denuncias";
    private static final String DENUNCIA = """
            {"rutAsegurado":"12345678-9","numeroPoliza":"pol-1001","patente":"abcd12",
             "fechaSiniestro":"2025-10-20T18:30:00","lugar":"Av. Providencia con Los Leones",
             "comuna":"Providencia","descripcion":"Colision por alcance en semaforo, dano en parachoques trasero",
             "fotos":[{"url":"https://fotos.siniestrofacil.cl/abc/1.jpg","descripcion":"Parachoques"}]}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private JsonNode registrar() throws Exception {
        String respuesta = mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(DENUNCIA))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta);
    }

    @Test
    void registrarDevuelveFolioYEstadoRecibida() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(DENUNCIA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.folio", matchesPattern("SF-\\d{8}-[A-Z0-9]{8}")))
                .andExpect(jsonPath("$.estado").value("RECIBIDA"))
                .andExpect(jsonPath("$.numeroPoliza").value("POL-1001"))
                .andExpect(jsonPath("$.fotos", hasSize(1)));
    }

    @Test
    void validacionesDeEntrada() throws Exception {
        String invalida = DENUNCIA.replace("12345678-9", "123").replace("2025-10-20", "2099-01-01");
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(invalida))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.rutAsegurado").exists())
                .andExpect(jsonPath("$.fields.fechaSiniestro").exists());
        mockMvc.perform(get(BASE + "/999")).andExpect(status().isNotFound());
        mockMvc.perform(get(BASE).param("estado", "INVENTADO")).andExpect(status().isBadRequest());
    }

    @Test
    void consultasPorFolioRutYEstado() throws Exception {
        String folio = registrar().get("folio").asText();
        mockMvc.perform(get(BASE + "/folio/" + folio))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.folio").value(folio));
        mockMvc.perform(get(BASE).param("rut", "12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get(BASE).param("estado", "RECHAZADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void cicloCrudConFotos() throws Exception {
        long id = registrar().get("id").asLong();

        mockMvc.perform(put(BASE + "/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(DENUNCIA.replace("Providencia\",", "Santiago\",")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comuna").value("Santiago"));

        String foto = mockMvc.perform(post(BASE + "/" + id + "/fotos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://fotos.siniestrofacil.cl/abc/2.jpg\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long fotoId = objectMapper.readTree(foto).get("id").asLong();
        mockMvc.perform(delete(BASE + "/" + id + "/fotos/" + fotoId)).andExpect(status().isNoContent());

        mockMvc.perform(delete(BASE + "/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get(BASE + "/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void noSePuedeModificarUnaDenunciaTomadaPorTaller() throws Exception {
        long id = registrar().get("id").asLong();
        mockMvc.perform(patch(BASE + "/" + id + "/estado").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"VEHICULO_RECIBIDO\",\"detalle\":\"Recibido por el taller\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("VEHICULO_RECIBIDO"));
        mockMvc.perform(delete(BASE + "/" + id)).andExpect(status().isConflict());
    }

    @Test
    void procesadorResponde503SiPolizasNoEstaDisponible() throws Exception {
        long id = registrar().get("id").asLong();
        mockMvc.perform(post("/api/v1/procesador/denuncias/" + id))
                .andExpect(status().isServiceUnavailable());
        // el ingreso no se ve afectado: la denuncia sigue registrada como RECIBIDA
        mockMvc.perform(get(BASE + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECIBIDA"));
    }
}
