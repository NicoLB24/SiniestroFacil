package cl.duoc.siniestrofacil.denuncias.service;

import cl.duoc.siniestrofacil.denuncias.dto.CambioEstadoDTO;
import cl.duoc.siniestrofacil.denuncias.dto.DenunciaRequestDTO;
import cl.duoc.siniestrofacil.denuncias.dto.DenunciaResponseDTO;
import cl.duoc.siniestrofacil.denuncias.dto.FotoDTO;
import cl.duoc.siniestrofacil.denuncias.exception.BusinessException;
import cl.duoc.siniestrofacil.denuncias.exception.ResourceNotFoundException;
import cl.duoc.siniestrofacil.denuncias.model.Denuncia;
import cl.duoc.siniestrofacil.denuncias.model.EstadoDenuncia;
import cl.duoc.siniestrofacil.denuncias.model.Foto;
import cl.duoc.siniestrofacil.denuncias.repository.DenunciaRepository;
import cl.duoc.siniestrofacil.denuncias.repository.FotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DenunciaServiceImpl implements DenunciaService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.BASIC_ISO_DATE;

    private final DenunciaRepository denunciaRepository;
    private final FotoRepository fotoRepository;
    private final PublicadorEventos publicadorEventos;
    private final DenunciaMapper mapper;

    public DenunciaServiceImpl(DenunciaRepository denunciaRepository, FotoRepository fotoRepository,
                               PublicadorEventos publicadorEventos, DenunciaMapper mapper) {
        this.denunciaRepository = denunciaRepository;
        this.fotoRepository = fotoRepository;
        this.publicadorEventos = publicadorEventos;
        this.mapper = mapper;
    }

    /**
     * Camino critico: guarda la denuncia, genera el folio y publica el evento.
     * No llama a polizas ni a talleres, por eso responde de inmediato.
     */
    @Override
    public DenunciaResponseDTO registrar(DenunciaRequestDTO request) {
        Denuncia denuncia = new Denuncia();
        denuncia.setFolio(generarFolio());
        aplicarDatos(denuncia, request);
        if (request.fotos() != null) {
            request.fotos().forEach(f -> denuncia.agregarFoto(new Foto(f.url(), f.descripcion())));
        }
        denuncia.setDetalleEstado("Denuncia recibida; validacion de poliza y asignacion de taller en proceso");
        Denuncia guardada = denunciaRepository.save(denuncia);
        publicadorEventos.publicarDenunciaRegistrada(guardada);
        return mapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DenunciaResponseDTO> listar(String rutAsegurado, EstadoDenuncia estado) {
        List<Denuncia> denuncias;
        if (rutAsegurado != null && !rutAsegurado.isBlank()) {
            denuncias = denunciaRepository.findByRutAseguradoOrderByFechaRegistroDesc(rutAsegurado);
        } else if (estado != null) {
            denuncias = denunciaRepository.findByEstadoOrderByFechaRegistroAsc(estado);
        } else {
            denuncias = denunciaRepository.findAll();
        }
        return denuncias.stream()
                .filter(d -> estado == null || d.getEstado() == estado)
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DenunciaResponseDTO obtenerPorId(Long id) {
        return mapper.toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public DenunciaResponseDTO obtenerPorFolio(String folio) {
        return denunciaRepository.findByFolio(folio)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la denuncia con folio " + folio));
    }

    @Override
    public DenunciaResponseDTO actualizar(Long id, DenunciaRequestDTO request) {
        Denuncia denuncia = buscar(id);
        validarModificable(denuncia);
        aplicarDatos(denuncia, request);
        return mapper.toResponse(denuncia);
    }

    @Override
    public DenunciaResponseDTO cambiarEstado(Long id, CambioEstadoDTO request) {
        Denuncia denuncia = buscar(id);
        if (denuncia.getEstado() == EstadoDenuncia.CERRADA) {
            throw new BusinessException("La denuncia " + denuncia.getFolio() + " ya esta cerrada");
        }
        denuncia.setEstado(request.estado());
        denuncia.setDetalleEstado(request.detalle());
        return mapper.toResponse(denuncia);
    }

    @Override
    public void eliminar(Long id) {
        Denuncia denuncia = buscar(id);
        validarModificable(denuncia);
        denunciaRepository.delete(denuncia);
    }

    @Override
    public FotoDTO agregarFoto(Long denunciaId, FotoDTO foto) {
        Denuncia denuncia = buscar(denunciaId);
        Foto nueva = new Foto(foto.url(), foto.descripcion());
        denuncia.agregarFoto(nueva);
        return mapper.toDto(fotoRepository.save(nueva));
    }

    @Override
    public void eliminarFoto(Long denunciaId, Long fotoId) {
        Foto foto = fotoRepository.findByIdAndDenunciaId(fotoId, denunciaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe la foto " + fotoId + " en la denuncia " + denunciaId));
        foto.getDenuncia().getFotos().remove(foto);
        fotoRepository.delete(foto);
    }

    private Denuncia buscar(Long id) {
        return denunciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la denuncia con id " + id));
    }

    private void validarModificable(Denuncia denuncia) {
        if (!denuncia.esModificable()) {
            throw new BusinessException("La denuncia " + denuncia.getFolio()
                    + " ya fue tomada por un taller y no se puede modificar (estado " + denuncia.getEstado() + ")");
        }
    }

    private void aplicarDatos(Denuncia denuncia, DenunciaRequestDTO request) {
        denuncia.setRutAsegurado(request.rutAsegurado());
        denuncia.setNumeroPoliza(request.numeroPoliza().toUpperCase());
        denuncia.setPatente(request.patente().toUpperCase());
        denuncia.setFechaSiniestro(request.fechaSiniestro());
        denuncia.setLugar(request.lugar());
        denuncia.setComuna(request.comuna());
        denuncia.setDescripcion(request.descripcion());
    }

    /** Folio legible para el asegurado: SF-AAAAMMDD-XXXXXXXX. */
    private String generarFolio() {
        String folio;
        do {
            String sufijo = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            folio = "SF-" + LocalDate.now().format(FORMATO_FECHA) + "-" + sufijo;
        } while (denunciaRepository.existsByFolio(folio));
        return folio;
    }
}
