package cl.duoc.siniestrofacil.denuncias.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Referencia a una fotografia del siniestro. Se guarda la URL donde la app
 * subio la imagen (por ejemplo un bucket S3), no el archivo en si.
 */
@Entity
@Table(name = "fotos")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(length = 150)
    private String descripcion;

    @Column(name = "fecha_carga", nullable = false)
    private LocalDateTime fechaCarga;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "denuncia_id", nullable = false)
    private Denuncia denuncia;

    public Foto() {
    }

    public Foto(String url, String descripcion) {
        this.url = url;
        this.descripcion = descripcion;
    }

    @PrePersist
    void alCrear() {
        fechaCarga = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaCarga() { return fechaCarga; }
    public Denuncia getDenuncia() { return denuncia; }
    public void setDenuncia(Denuncia denuncia) { this.denuncia = denuncia; }
}
