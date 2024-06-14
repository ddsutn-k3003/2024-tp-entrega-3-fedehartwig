package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor

@Entity
@Table(name = "traslado")
public class Traslado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String qrVianda;

    @OneToOne
    @JoinColumn(name = "ruta_id", referencedColumnName = "id")
    private Ruta ruta;
    @Column
    @Enumerated(EnumType.STRING)
    private EstadoTrasladoEnum status;
    @Column (name = "fechatraslado")
    private LocalDateTime fechaTraslado;
    @Column
    private Long colaboradorId;
    @Column
    private Integer heladeraOrigen;
    @Column
    private Integer heladeraDestino;

    protected Traslado(){
        super();
    }

    public Traslado(String qrVianda, EstadoTrasladoEnum status, LocalDateTime fechaTraslado,  Ruta ruta, Integer heladeraOrigen, Integer heladeraDestino) {
        this.qrVianda = qrVianda;
        this.ruta = ruta;
        this.status = status;
        this.fechaTraslado = fechaTraslado;
        this.colaboradorId = ruta.getColaboradorId();
        this.heladeraOrigen = heladeraOrigen;
        this.heladeraDestino = heladeraDestino;
    }

}
