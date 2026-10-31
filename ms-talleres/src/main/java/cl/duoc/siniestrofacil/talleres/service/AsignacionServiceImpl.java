package cl.duoc.siniestrofacil.talleres.service;

import cl.duoc.siniestrofacil.talleres.dto.AsignacionRequestDTO;
import cl.duoc.siniestrofacil.talleres.dto.AsignacionResponseDTO;
import cl.duoc.siniestrofacil.talleres.dto.RecepcionRequestDTO;
import cl.duoc.siniestrofacil.talleres.exception.BusinessException;
import cl.duoc.siniestrofacil.talleres.exception.ResourceNotFoundException;
import cl.duoc.siniestrofacil.talleres.model.Asignacion;
import cl.duoc.siniestrofacil.talleres.model.EstadoAsignacion;
import cl.duoc.siniestrofacil.talleres.model.Taller;
import cl.duoc.siniestrofacil.talleres.repository.AsignacionRepository;
import cl.duoc.siniestrofacil.talleres.repository.TallerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class AsignacionServiceImpl implements AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final TallerRepository tallerRepository;

    public AsignacionServiceImpl(AsignacionRepository asignacionRepository, TallerRepository tallerRepository) {
        this.asignacionRepository = asignacionRepository;
        this.tallerRepository = tallerRepository;
    }

    /**
     * Elige el taller activo con capacidad libre y menos carga, prefiriendo
     * los de la misma comuna. Si no hay ninguno responde 409 para que el
     * procesador reintente mas tarde (el mensaje vuelve a la cola).
     */
    @Override
    public AsignacionResponseDTO asignar(AsignacionRequestDTO request) {
        if (asignacionRepository.existsByDenunciaIdAndEstadoIn(request.denunciaId(), EstadoAsignacion.ACTIVOS)) {
            throw new BusinessException("La denuncia " + request.folio() + " ya tiene un taller asignado");
        }
        Taller taller = tallerRepository.findByActivoTrue().stream()
                .filter(t -> carga(t) < t.getCapacidadMaxima())
                .min(Comparator
                        .comparing((Taller t) -> !t.getComuna().equalsIgnoreCase(String.valueOf(request.comuna())))
                        .thenComparingLong(this::carga))
                .orElseThrow(() -> new BusinessException("No hay talleres disponibles en este momento"));

        Asignacion asignacion = new Asignacion();
        asignacion.setDenunciaId(request.denunciaId());
        asignacion.setFolio(request.folio());
        asignacion.setPatente(request.patente().toUpperCase());
        asignacion.setFechaAsignacion(LocalDateTime.now());
        asignacion.setTaller(taller);
        return toResponse(asignacionRepository.save(asignacion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionResponseDTO> listarPorTaller(Long tallerId) {
        if (!tallerRepository.existsById(tallerId)) {
            throw new ResourceNotFoundException("No existe el taller con id " + tallerId);
        }
        return asignacionRepository.findByTallerIdOrderByFechaAsignacionDesc(tallerId).stream()
                .map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionResponseDTO> listarPorDenuncia(Long denunciaId) {
        return asignacionRepository.findByDenunciaId(denunciaId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AsignacionResponseDTO obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    public AsignacionResponseDTO confirmarRecepcion(Long id, RecepcionRequestDTO request) {
        Asignacion asignacion = buscar(id);
        validarEstado(asignacion, EstadoAsignacion.ASIGNADA, "confirmar la recepcion");
        asignacion.setEstado(EstadoAsignacion.RECIBIDO);
        asignacion.setFechaRecepcion(LocalDateTime.now());
        asignacion.setObservacion(request != null ? request.observacion() : null);
        return toResponse(asignacion);
    }

    @Override
    public AsignacionResponseDTO finalizar(Long id) {
        Asignacion asignacion = buscar(id);
        validarEstado(asignacion, EstadoAsignacion.RECIBIDO, "finalizar");
        asignacion.setEstado(EstadoAsignacion.FINALIZADA);
        return toResponse(asignacion);
    }

    @Override
    public AsignacionResponseDTO cancelar(Long id) {
        Asignacion asignacion = buscar(id);
        validarEstado(asignacion, EstadoAsignacion.ASIGNADA, "cancelar");
        asignacion.setEstado(EstadoAsignacion.CANCELADA);
        return toResponse(asignacion);
    }

    private long carga(Taller taller) {
        return asignacionRepository.countByTallerIdAndEstadoIn(taller.getId(), EstadoAsignacion.ACTIVOS);
    }

    private void validarEstado(Asignacion asignacion, EstadoAsignacion esperado, String accion) {
        if (asignacion.getEstado() != esperado) {
            throw new BusinessException("No se puede " + accion + ": la asignacion esta en estado "
                    + asignacion.getEstado());
        }
    }

    private Asignacion buscar(Long id) {
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la asignacion con id " + id));
    }

    private AsignacionResponseDTO toResponse(Asignacion a) {
        return new AsignacionResponseDTO(a.getId(), a.getDenunciaId(), a.getFolio(), a.getPatente(),
                a.getEstado().name(), a.getFechaAsignacion(), a.getFechaRecepcion(), a.getObservacion(),
                a.getTaller().getId(), a.getTaller().getNombre());
    }
}
