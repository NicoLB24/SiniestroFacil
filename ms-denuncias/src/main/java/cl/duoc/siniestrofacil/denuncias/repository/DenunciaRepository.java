package cl.duoc.siniestrofacil.denuncias.repository;

import cl.duoc.siniestrofacil.denuncias.model.Denuncia;
import cl.duoc.siniestrofacil.denuncias.model.EstadoDenuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {

    Optional<Denuncia> findByFolio(String folio);

    List<Denuncia> findByRutAseguradoOrderByFechaRegistroDesc(String rutAsegurado);

    List<Denuncia> findByEstadoOrderByFechaRegistroAsc(EstadoDenuncia estado);

    boolean existsByFolio(String folio);
}
