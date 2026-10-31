package cl.duoc.siniestrofacil.polizas.repository;

import cl.duoc.siniestrofacil.polizas.model.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    Optional<Poliza> findByNumeroPoliza(String numeroPoliza);

    List<Poliza> findByRutAsegurado(String rutAsegurado);

    boolean existsByNumeroPoliza(String numeroPoliza);
}
