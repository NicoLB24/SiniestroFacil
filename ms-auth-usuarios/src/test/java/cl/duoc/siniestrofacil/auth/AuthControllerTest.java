package cl.duoc.siniestrofacil.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginCorrectoEntregaTokenQueLuegoSeValida() throws Exception {
        String respuesta = mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"juan.perez@mail.cl\",\"password\":\"Clave123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.usuario.rol").value("ASEGURADO"))
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(respuesta);
        mockMvc.perform(get("/api/v1/auth/validar")
                        .header("Authorization", "Bearer " + json.get("token").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true))
                .andExpect(jsonPath("$.rut").value("12345678-9"));
    }

    @Test
    void loginConContrasenaIncorrectaDevuelve401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"juan.perez@mail.cl\",\"password\":\"mala\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenAdulteradoDevuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/validar").header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registroConEmailInvalidoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"X\",\"email\":\"no-es-email\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.email").exists())
                .andExpect(jsonPath("$.fields.password").exists());
    }

    @Test
    void cicloCrudDeUsuario() throws Exception {
        String registro = """
                {"nombre":"Usuario Test","email":"test@mail.cl","password":"Clave123","rut":"55555555-5"}
                """;
        String creado = mockMvc.perform(post("/api/v1/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content(registro))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("ASEGURADO"))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(creado).get("id").asLong();

        // email duplicado
        mockMvc.perform(post("/api/v1/auth/registro").contentType(MediaType.APPLICATION_JSON).content(registro))
                .andExpect(status().isConflict());

        mockMvc.perform(put("/api/v1/usuarios/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Usuario Editado\",\"rut\":\"55555555-5\",\"rol\":\"ASEGURADO\",\"activo\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Usuario Editado"));

        mockMvc.perform(get("/api/v1/usuarios").param("rol", "TALLER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(delete("/api/v1/usuarios/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/usuarios/" + id)).andExpect(status().isNotFound());
    }
}
