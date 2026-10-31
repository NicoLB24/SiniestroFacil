package cl.duoc.siniestrofacil.talleres.repository;

import cl.duoc.siniestrofacil.talleres.model.Taller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TallerRepository extends JpaRepository<Taller, Long> {

    List<Taller> findByActivoTrue();

    List<Taller> findByComunaIgnoreCase(String comuna);
}
