package cl.duoc.siniestrofacil.polizas.service;

import cl.duoc.siniestrofacil.polizas.dto.CoberturaDTO;
import cl.duoc.siniestrofacil.polizas.dto.PolizaRequestDTO;
import cl.duoc.siniestrofacil.polizas.dto.PolizaResponseDTO;
import cl.duoc.siniestrofacil.polizas.dto.VigenciaResponseDTO;
import cl.duoc.siniestrofacil.polizas.exception.BusinessException;
import cl.duoc.siniestrofacil.polizas.exception.ResourceNotFoundException;
import cl.duoc.siniestrofacil.polizas.model.Cobertura;
import cl.duoc.siniestrofacil.polizas.model.Poliza;
import cl.duoc.siniestrofacil.polizas.repository.PolizaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PolizaServiceImpl implements PolizaService {

    private final PolizaRepository polizaRepository;

    public PolizaServiceImpl(PolizaRepository polizaRepository) {
        this.polizaRepository = polizaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolizaResponseDTO> listar(String rutAsegurado) {
        List<Poliza> polizas = (rutAsegurado == null || rutAsegurado.isBlank())
                ? polizaRepository.findAll()
                : polizaRepository.findByRutAsegurado(rutAsegurado);
        return polizas.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PolizaResponseDTO obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PolizaResponseDTO obtenerPorNumero(String numeroPoliza) {
        return toResponse(buscarPorNumero(numeroPoliza));
    }

    @Override
    public PolizaResponseDTO crear(PolizaRequestDTO request) {
        if (polizaRepository.existsByNumeroPoliza(request.numeroPoliza())) {
            throw new BusinessException("Ya existe una poliza con numero " + request.numeroPoliza());
        }
        Poliza poliza = new Poliza();
        aplicarDatos(poliza, request);
        return toResponse(polizaRepository.save(poliza));
    }

    @Override
    public PolizaResponseDTO actualizar(Long id, PolizaRequestDTO request) {
        Poliza poliza = buscar(id);
        if (!poliza.getNumeroPoliza().equals(request.numeroPoliza())
                && polizaRepository.existsByNumeroPoliza(request.numeroPoliza())) {
            throw new BusinessException("Ya existe una poliza con numero " + request.numeroPoliza());
        }
        aplicarDatos(poliza, request);
        return toResponse(polizaRepository.save(poliza));
    }

    @Override
    public void eliminar(Long id) {
        polizaRepository.delete(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public VigenciaResponseDTO validarVigencia(String numeroPoliza) {
        Poliza poliza = buscarPorNumero(numeroPoliza);
        LocalDate hoy = LocalDate.now();
        boolean vigente = poliza.estaVigente(hoy);
        String mensaje = vigente ? "Poliza vigente" : "Poliza no vigente o inactiva";
        return new VigenciaResponseDTO(poliza.getNumeroPoliza(), poliza.getPatente(), vigente, hoy, mensaje);
    }

    private Poliza buscar(Long id) {
        return polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la poliza con id " + id));
    }

    private Poliza buscarPorNumero(String numeroPoliza) {
        return polizaRepository.findByNumeroPoliza(numeroPoliza)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la poliza " + numeroPoliza));
    }

    private void aplicarDatos(Poliza poliza, PolizaRequestDTO request) {
        if (request.fechaFin().isBefore(request.fechaInicio())) {
            throw new BusinessException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        poliza.setNumeroPoliza(request.numeroPoliza());
        poliza.setRutAsegurado(request.rutAsegurado());
        poliza.setNombreAsegurado(request.nombreAsegurado());
        poliza.setPatente(request.patente().toUpperCase());
        poliza.setFechaInicio(request.fechaInicio());
        poliza.setFechaFin(request.fechaFin());
        poliza.setActiva(request.activa() == null || request.activa());
        poliza.limpiarCoberturas();
        if (request.coberturas() != null) {
            request.coberturas().forEach(c -> poliza.agregarCobertura(new Cobertura(c.nombre(), c.montoMaximo())));
        }
    }

    private PolizaResponseDTO toResponse(Poliza p) {
        List<CoberturaDTO> coberturas = p.getCoberturas().stream()
                .map(c -> new CoberturaDTO(c.getId(), c.getNombre(), c.getMontoMaximo()))
                .toList();
        return new PolizaResponseDTO(p.getId(), p.getNumeroPoliza(), p.getRutAsegurado(), p.getNombreAsegurado(),
                p.getPatente(), p.getFechaInicio(), p.getFechaFin(), p.isActiva(),
                p.estaVigente(LocalDate.now()), coberturas);
    }
}
