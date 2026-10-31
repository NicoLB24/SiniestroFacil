package cl.duoc.siniestrofacil.polizas.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Poliza de seguro automotriz. Cubre un vehiculo (patente) y tiene
 * una o varias coberturas asociadas.
 */
@Entity
@Table(name = "polizas")
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_poliza", nullable = false, unique = true, length = 20)
    private String numeroPoliza;

    @Column(name = "rut_asegurado", nullable = false, length = 12)
    private String rutAsegurado;

    @Column(name = "nombre_asegurado", nullable = false, length = 100)
    private String nombreAsegurado;

    @Column(nullable = false, length = 10)
    private String patente;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private boolean activa = true;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cobertura> coberturas = new ArrayList<>();

    /** La poliza esta vigente si esta activa y la fecha cae dentro de su periodo. */
    public boolean estaVigente(LocalDate fecha) {
        return activa && !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public void agregarCobertura(Cobertura cobertura) {
        cobertura.setPoliza(this);
        coberturas.add(cobertura);
    }

    public void limpiarCoberturas() {
        coberturas.forEach(c -> c.setPoliza(null));
        coberturas.clear();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroPoliza() { return numeroPoliza; }
    public void setNumeroPoliza(String numeroPoliza) { this.numeroPoliza = numeroPoliza; }
    public String getRutAsegurado() { return rutAsegurado; }
    public void setRutAsegurado(String rutAsegurado) { this.rutAsegurado = rutAsegurado; }
    public String getNombreAsegurado() { return nombreAsegurado; }
    public void setNombreAsegurado(String nombreAsegurado) { this.nombreAsegurado = nombreAsegurado; }
    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public List<Cobertura> getCoberturas() { return coberturas; }
}
