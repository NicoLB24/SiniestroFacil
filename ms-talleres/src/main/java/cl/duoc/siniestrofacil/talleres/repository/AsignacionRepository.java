package cl.duoc.siniestrofacil.talleres.repository;

import cl.duoc.siniestrofacil.talleres.model.Asignacion;
import cl.duoc.siniestrofacil.talleres.model.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    List<Asignacion> findByTallerIdOrderByFechaAsignacionDesc(Long tallerId);

    List<Asignacion> findByDenunciaId(Long denunciaId);

    long countByTallerIdAndEstadoIn(Long tallerId, Collection<EstadoAsignacion> estados);

    boolean existsByDenunciaIdAndEstadoIn(Long denunciaId, Collection<EstadoAsignacion> estados);
}
