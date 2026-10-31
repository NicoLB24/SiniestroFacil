package cl.duoc.siniestrofacil.talleres;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // cada prueba revierte sus cambios en la base
class TalleresControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listaSoloTalleresDisponibles() throws Exception {
        mockMvc.perform(get("/api/v1/talleres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
        // el taller El Roble esta inactivo
        mockMvc.perform(get("/api/v1/talleres").param("disponibles", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void asignaPrefiriendoLaComunaYLuegoConfirmaRecepcion() throws Exception {
        String respuesta = mockMvc.perform(post("/api/v1/asignaciones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"denunciaId\":10,\"folio\":\"SF-TEST-0001\",\"patente\":\"abcd12\",\"comuna\":\"Maipu\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tallerNombre").value("Desabolladura Maipu"))
                .andExpect(jsonPath("$.estado").value("ASIGNADA"))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(respuesta).get("id").asLong();

        // la misma denuncia no puede quedar asignada dos veces
        mockMvc.perform(post("/api/v1/asignaciones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"denunciaId\":10,\"folio\":\"SF-TEST-0001\",\"patente\":\"ABCD12\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/v1/talleres/3/denuncias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(put("/api/v1/asignaciones/" + id + "/recepcion").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"observacion\":\"Vehiculo con dano en parachoques\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECIBIDO"));

        // no se puede confirmar dos veces
        mockMvc.perform(put("/api/v1/asignaciones/" + id + "/recepcion"))
                .andExpect(status().isConflict());
    }

    @Test
    void sinCapacidadDevuelve409() throws Exception {
        // capacidad total disponible: 5 + 3 + 2 = 10
        for (int i = 1; i <= 10; i++) {
            mockMvc.perform(post("/api/v1/asignaciones").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"denunciaId\":" + i + ",\"folio\":\"SF-" + i + "\",\"patente\":\"AAAA11\"}"))
                    .andExpect(status().isCreated());
        }
        mockMvc.perform(post("/api/v1/asignaciones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"denunciaId\":99,\"folio\":\"SF-99\",\"patente\":\"AAAA11\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("No hay talleres disponibles en este momento"));
    }

    @Test
    void validacionesYErrores() throws Exception {
        mockMvc.perform(post("/api/v1/talleres").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"capacidadMaxima\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.capacidadMaxima").exists());
        mockMvc.perform(get("/api/v1/talleres/999")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/v1/talleres/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void cicloCrudDeTaller() throws Exception {
        String body = """
                {"nombre":"Taller Nuevo","direccion":"Calle 1","comuna":"Providencia","capacidadMaxima":2}
                """;
        String creado = mockMvc.perform(post("/api/v1/talleres").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.disponible").value(true))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(creado).get("id").asLong();

        mockMvc.perform(put("/api/v1/talleres/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(body.replace("2}", "6, \"activo\": false}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacidadMaxima").value(6))
                .andExpect(jsonPath("$.disponible").value(false));

        mockMvc.perform(delete("/api/v1/talleres/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/talleres/" + id)).andExpect(status().isNotFound());
    }
}
