package cl.duoc.siniestrofacil.denuncias.service;

import cl.duoc.siniestrofacil.denuncias.model.Denuncia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Implementacion para ambiente local: deja el evento en el log en lugar de enviarlo a SQS. */
@Component
public class PublicadorEventosLocal implements PublicadorEventos {

    private static final Logger log = LoggerFactory.getLogger(PublicadorEventosLocal.class);

    @Override
    public void publicarDenunciaRegistrada(Denuncia denuncia) {
        log.info("[cola denuncias-pendientes] evento DenunciaRegistrada id={} folio={} poliza={}",
                denuncia.getId(), denuncia.getFolio(), denuncia.getNumeroPoliza());
    }
}
