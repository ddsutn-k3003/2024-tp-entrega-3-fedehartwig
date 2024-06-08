package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Traslado {
    private Long id;
    private final String qrVianda;
    private final EstadoTrasladoEnum status;
    private final LocalDateTime fechaTraslado;
    private final Integer heladeraOrigen;
    private final Integer heladeraDestino;
    private Long colaboradorId;

    public Traslado(String qrVianda, Integer heladeraOrigen, Integer heladeraDestino) {
        this(qrVianda, EstadoTrasladoEnum.CREADO, LocalDateTime.now(), heladeraOrigen, heladeraDestino);
    }

    public Traslado(String qrVianda, EstadoTrasladoEnum status, LocalDateTime fechaTraslado, Integer heladeraOrigen, Integer heladeraDestino) {
        this.qrVianda = qrVianda;
        this.status = status;
        this.fechaTraslado = fechaTraslado;
        this.heladeraOrigen = heladeraOrigen;
        this.heladeraDestino = heladeraDestino;
    }

}
