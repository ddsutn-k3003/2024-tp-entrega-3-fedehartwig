package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.model.Ruta;
import ar.edu.utn.dds.k3003.model.Traslado;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class TrasladoRepository {

    private static AtomicLong seqId = new AtomicLong();

    private Collection<Traslado> traslados;

    public TrasladoRepository(){
        this.traslados = new ArrayList<>();
    }

    public Traslado save(Traslado traslado) {
        if (Objects.isNull(traslado.getId())) {
            traslado.setId(seqId.getAndIncrement());
            this.traslados.add(traslado);
        }
        return traslado;
    }

    public Traslado findById(Long id) {
        Optional<Traslado> first = this.traslados.stream().filter(x -> x.getId().equals(id)).findFirst();
        return first.orElseThrow(() -> new NoSuchElementException(
                String.format("No hay un traslado de id: %s", id)
        ));
    }

    public Traslado actualizarTrasladoRetirado(Long id){
        Traslado trasladoActualizar = findById(id);
        Traslado trasladoBase = new Traslado(trasladoActualizar.getQrVianda(), EstadoTrasladoEnum.EN_VIAJE, LocalDateTime.now(), trasladoActualizar.getHeladeraOrigen(), trasladoActualizar.getHeladeraDestino());
        Traslado trasladoGuardar= this.save(trasladoBase);
        trasladoGuardar.setId(id);

        return trasladoBase;
    }

    public Traslado actualizarTrasladoDepositado(Long id){
        Traslado trasladoActualizar = findById(id);
        Traslado trasladoBase = new Traslado(trasladoActualizar.getQrVianda(), EstadoTrasladoEnum.ENTREGADO, LocalDateTime.now(), trasladoActualizar.getHeladeraOrigen(), trasladoActualizar.getHeladeraDestino());
        Traslado trasladoGuardar= this.save(trasladoBase);

        trasladoGuardar.setId(id);
        return trasladoBase;
    }
}
