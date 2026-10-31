package cl.duoc.siniestrofacil.denuncias.service;

import cl.duoc.siniestrofacil.denuncias.client.PolizasClient;
import cl.duoc.siniestrofacil.denuncias.client.PolizasClient.VigenciaResponse;
import cl.duoc.siniestrofacil.denuncias.client.TalleresClient;
import cl.duoc.siniestrofacil.denuncias.client.TalleresClient.AsignacionRequest;
import cl.duoc.siniestrofacil.denuncias.client.TalleresClient.ResultadoAsignacion;
import cl.duoc.siniestrofacil.denuncias.dto.DenunciaResponseDTO;
import cl.duoc.siniestrofacil.denuncias.exception.BusinessException;
import cl.duoc.siniestrofacil.denuncias.exception.ResourceNotFoundException;
import cl.duoc.siniestrofacil.denuncias.model.Denuncia;
import cl.duoc.siniestrofacil.denuncias.model.EstadoDenuncia;
import cl.duoc.siniestrofacil.denuncias.repository.DenunciaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Simulacion local de la funcion AWS Lambda del diseno, que consume la cola
 * denuncias-pendientes:
 * 1. valida la vigencia de la poliza en ms-polizas;
 * 2. si NO esta vigente, la denuncia queda RECHAZADA;
 * 3. si esta vigente, pide a ms-talleres un taller disponible.
 * Si no hay taller la denuncia queda PENDIENTE_TALLER para reintentar,
 * igual que un mensaje que vuelve a la cola.
 */
@Service
@Transactional
public class ProcesadorDenunciasService {

    private static final Logger log = LoggerFactory.getLogger(ProcesadorDenunciasService.class);

    private final DenunciaRepository denunciaRepository;
    private final PolizasClient polizasClient;
    private final TalleresClient talleresClient;
    private final DenunciaMapper mapper;

    public ProcesadorDenunciasService(DenunciaRepository denunciaRepository, PolizasClient polizasClient,
                                      TalleresClient talleresClient, DenunciaMapper mapper) {
        this.denunciaRepository = denunciaRepository;
        this.polizasClient = polizasClient;
        this.talleresClient = talleresClient;
        this.mapper = mapper;
    }

    public DenunciaResponseDTO procesar(Long id) {
        Denuncia denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la denuncia con id " + id));
        if (denuncia.getEstado() != EstadoDenuncia.RECIBIDA && denuncia.getEstado() != EstadoDenuncia.PENDIENTE_TALLER) {
            throw new BusinessException("La denuncia " + denuncia.getFolio() + " ya fue procesada (estado "
                    + denuncia.getEstado() + ")");
        }

        Optional<VigenciaResponse> vigencia = polizasClient.consultarVigencia(denuncia.getNumeroPoliza());
        if (vigencia.isEmpty()) {
            return rechazar(denuncia, "La poliza " + denuncia.getNumeroPoliza() + " no existe");
        }
        if (!vigencia.get().vigente()) {
            return rechazar(denuncia, "La poliza " + denuncia.getNumeroPoliza() + " no esta vigente");
        }

        ResultadoAsignacion resultado = talleresClient.solicitarAsignacion(new AsignacionRequest(
                denuncia.getId(), denuncia.getFolio(), denuncia.getPatente(), denuncia.getComuna()));
        if (resultado.exitosa()) {
            denuncia.setEstado(EstadoDenuncia.TALLER_ASIGNADO);
            denuncia.setTallerId(resultado.asignacion().tallerId());
            denuncia.setAsignacionId(resultado.asignacion().id());
            denuncia.setDetalleEstado("Asignada al taller " + resultado.asignacion().tallerNombre());
        } else {
            denuncia.setEstado(EstadoDenuncia.PENDIENTE_TALLER);
            denuncia.setDetalleEstado(resultado.motivoFallo());
        }
        log.info("Denuncia {} procesada: {}", denuncia.getFolio(), denuncia.getEstado());
        return mapper.toResponse(denuncia);
    }

    /** Procesa en orden de llegada todas las denuncias que esperan (equivalente a vaciar la cola). */
    public List<DenunciaResponseDTO> procesarPendientes() {
        List<Denuncia> pendientes = new ArrayList<>(
                denunciaRepository.findByEstadoOrderByFechaRegistroAsc(EstadoDenuncia.RECIBIDA));
        pendientes.addAll(denunciaRepository.findByEstadoOrderByFechaRegistroAsc(EstadoDenuncia.PENDIENTE_TALLER));
        return pendientes.stream().map(d -> procesar(d.getId())).toList();
    }

    private DenunciaResponseDTO rechazar(Denuncia denuncia, String motivo) {
        denuncia.setEstado(EstadoDenuncia.RECHAZADA);
        denuncia.setDetalleEstado(motivo);
        log.info("Denuncia {} rechazada: {}", denuncia.getFolio(), motivo);
        return mapper.toResponse(denuncia);
    }
}
