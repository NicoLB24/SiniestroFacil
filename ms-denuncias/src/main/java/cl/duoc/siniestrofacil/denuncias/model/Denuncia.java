package cl.duoc.siniestrofacil.denuncias.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Denuncia de un siniestro automotriz hecha por un asegurado. */
@Entity
@Table(name = "denuncias")
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String folio;

    @Column(name = "rut_asegurado", nullable = false, length = 12)
    private String rutAsegurado;

    @Column(name = "numero_poliza", nullable = false, length = 20)
    private String numeroPoliza;

    @Column(nullable = false, length = 10)
    private String patente;

    @Column(name = "fecha_siniestro", nullable = false)
    private LocalDateTime fechaSiniestro;

    @Column(nullable = false, length = 150)
    private String lugar;

    @Column(length = 60)
    private String comuna;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoDenuncia estado = EstadoDenuncia.RECIBIDA;

    @Column(name = "taller_id")
    private Long tallerId;

    @Column(name = "asignacion_id")
    private Long asignacionId;

    @Column(name = "detalle_estado", length = 255)
    private String detalleEstado;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "denuncia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Foto> fotos = new ArrayList<>();

    @PrePersist
    void alCrear() {
        fechaRegistro = LocalDateTime.now();
        fechaActualizacion = fechaRegistro;
    }

    @PreUpdate
    void alActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }

    public void agregarFoto(Foto foto) {
        foto.setDenuncia(this);
        fotos.add(foto);
    }

    /** Solo se puede editar o eliminar mientras ningun taller haya tomado el caso. */
    public boolean esModificable() {
        return estado == EstadoDenuncia.RECIBIDA || estado == EstadoDenuncia.PENDIENTE_TALLER
                || estado == EstadoDenuncia.RECHAZADA;
    }

    public Long getId() { return id; }
    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }
    public String getRutAsegurado() { return rutAsegurado; }
    public void setRutAsegurado(String rutAsegurado) { this.rutAsegurado = rutAsegurado; }
    public String getNumeroPoliza() { return numeroPoliza; }
    public void setNumeroPoliza(String numeroPoliza) { this.numeroPoliza = numeroPoliza; }
    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }
    public LocalDateTime getFechaSiniestro() { return fechaSiniestro; }
    public void setFechaSiniestro(LocalDateTime fechaSiniestro) { this.fechaSiniestro = fechaSiniestro; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public EstadoDenuncia getEstado() { return estado; }
    public void setEstado(EstadoDenuncia estado) { this.estado = estado; }
    public Long getTallerId() { return tallerId; }
    public void setTallerId(Long tallerId) { this.tallerId = tallerId; }
    public Long getAsignacionId() { return asignacionId; }
    public void setAsignacionId(Long asignacionId) { this.asignacionId = asignacionId; }
    public String getDetalleEstado() { return detalleEstado; }
    public void setDetalleEstado(String detalleEstado) { this.detalleEstado = detalleEstado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public List<Foto> getFotos() { return fotos; }
}
