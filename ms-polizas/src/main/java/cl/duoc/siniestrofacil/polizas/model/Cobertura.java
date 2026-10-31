package cl.duoc.siniestrofacil.polizas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** Cobertura incluida en una poliza (danos propios, robo, responsabilidad civil...). */
@Entity
@Table(name = "coberturas")
public class Cobertura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(name = "monto_maximo", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoMaximo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poliza_id", nullable = false)
    private Poliza poliza;

    public Cobertura() {
    }

    public Cobertura(String nombre, BigDecimal montoMaximo) {
        this.nombre = nombre;
        this.montoMaximo = montoMaximo;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getMontoMaximo() { return montoMaximo; }
    public void setMontoMaximo(BigDecimal montoMaximo) { this.montoMaximo = montoMaximo; }
    public Poliza getPoliza() { return poliza; }
    public void setPoliza(Poliza poliza) { this.poliza = poliza; }
}
