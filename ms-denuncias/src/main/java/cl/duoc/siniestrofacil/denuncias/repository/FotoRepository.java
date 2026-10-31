package cl.duoc.siniestrofacil.denuncias.repository;

import cl.duoc.siniestrofacil.denuncias.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Long> {

    Optional<Foto> findByIdAndDenunciaId(Long id, Long denunciaId);
}
