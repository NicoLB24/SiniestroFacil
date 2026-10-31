package cl.duoc.siniestrofacil.denuncias.service;

import cl.duoc.siniestrofacil.denuncias.model.Denuncia;

/**
 * Publica el evento "denuncia registrada" en la cola denuncias-pendientes.
 * En AWS la implementacion usaria Amazon SQS; en local se registra en el log.
 */
public interface PublicadorEventos {

    void publicarDenunciaRegistrada(Denuncia denuncia);
}
