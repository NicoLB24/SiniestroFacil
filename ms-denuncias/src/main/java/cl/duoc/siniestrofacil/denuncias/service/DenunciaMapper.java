package cl.duoc.siniestrofacil.denuncias.service;

import cl.duoc.siniestrofacil.denuncias.dto.DenunciaResponseDTO;
import cl.duoc.siniestrofacil.denuncias.dto.FotoDTO;
import cl.duoc.siniestrofacil.denuncias.model.Denuncia;
import cl.duoc.siniestrofacil.denuncias.model.Foto;
import org.springframework.stereotype.Component;

/** Convierte entidades a DTO para no exponer el modelo JPA en la API. */
@Component
public class DenunciaMapper {

    public DenunciaResponseDTO toResponse(Denuncia d) {
        return new DenunciaResponseDTO(d.getId(), d.getFolio(), d.getRutAsegurado(), d.getNumeroPoliza(),
                d.getPatente(), d.getFechaSiniestro(), d.getLugar(), d.getComuna(), d.getDescripcion(),
                d.getEstado().name(), d.getDetalleEstado(), d.getTallerId(), d.getAsignacionId(),
                d.getFechaRegistro(), d.getFechaActualizacion(),
                d.getFotos().stream().map(this::toDto).toList());
    }

    public FotoDTO toDto(Foto f) {
        return new FotoDTO(f.getId(), f.getUrl(), f.getDescripcion(), f.getFechaCarga());
    }
}
