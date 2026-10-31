package cl.duoc.siniestrofacil.talleres.service;

import cl.duoc.siniestrofacil.talleres.dto.TallerRequestDTO;
import cl.duoc.siniestrofacil.talleres.dto.TallerResponseDTO;
import cl.duoc.siniestrofacil.talleres.exception.BusinessException;
import cl.duoc.siniestrofacil.talleres.exception.ResourceNotFoundException;
import cl.duoc.siniestrofacil.talleres.model.EstadoAsignacion;
import cl.duoc.siniestrofacil.talleres.model.Taller;
import cl.duoc.siniestrofacil.talleres.repository.AsignacionRepository;
import cl.duoc.siniestrofacil.talleres.repository.TallerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TallerServiceImpl implements TallerService {

    private final TallerRepository tallerRepository;
    private final AsignacionRepository asignacionRepository;

    public TallerServiceImpl(TallerRepository tallerRepository, AsignacionRepository asignacionRepository) {
        this.tallerRepository = tallerRepository;
        this.asignacionRepository = asignacionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TallerResponseDTO> listar(String comuna, boolean soloDisponibles) {
        List<Taller> talleres = (comuna == null || comuna.isBlank())
                ? tallerRepository.findAll()
                : tallerRepository.findByComunaIgnoreCase(comuna);
        return talleres.stream()
                .map(this::toResponse)
                .filter(t -> !soloDisponibles || t.disponible())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TallerResponseDTO obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    public TallerResponseDTO crear(TallerRequestDTO request) {
        Taller taller = new Taller();
        aplicarDatos(taller, request);
        return toResponse(tallerRepository.save(taller));
    }

    @Override
    public TallerResponseDTO actualizar(Long id, TallerRequestDTO request) {
        Taller taller = buscar(id);
        aplicarDatos(taller, request);
        return toResponse(tallerRepository.save(taller));
    }

    @Override
    public void eliminar(Long id) {
        Taller taller = buscar(id);
        if (!taller.getAsignaciones().isEmpty()) {
            throw new BusinessException("El taller tiene asignaciones registradas; desactivelo en lugar de eliminarlo");
        }
        tallerRepository.delete(taller);
    }

    private Taller buscar(Long id) {
        return tallerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el taller con id " + id));
    }

    private void aplicarDatos(Taller taller, TallerRequestDTO request) {
        taller.setNombre(request.nombre());
        taller.setDireccion(request.direccion());
        taller.setComuna(request.comuna());
        taller.setTelefono(request.telefono());
        taller.setEmail(request.email());
        taller.setCapacidadMaxima(request.capacidadMaxima());
        taller.setActivo(request.activo() == null || request.activo());
    }

    private TallerResponseDTO toResponse(Taller t) {
        long activos = asignacionRepository.countByTallerIdAndEstadoIn(t.getId(), EstadoAsignacion.ACTIVOS);
        boolean disponible = t.isActivo() && activos < t.getCapacidadMaxima();
        return new TallerResponseDTO(t.getId(), t.getNombre(), t.getDireccion(), t.getComuna(), t.getTelefono(),
                t.getEmail(), t.getCapacidadMaxima(), activos, disponible, t.isActivo());
    }
}
