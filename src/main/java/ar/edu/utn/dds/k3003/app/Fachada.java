package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import ar.edu.utn.dds.k3003.model.Ruta;
import ar.edu.utn.dds.k3003.model.Traslado;
import ar.edu.utn.dds.k3003.model.Vianda;
import ar.edu.utn.dds.k3003.repositories.*;

import java.awt.desktop.QuitResponse;
import java.time.LocalDateTime;
import java.util.*;


public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaLogistica {

    private final RutaRepository rutaRepository;
    private final RutaMapper rutaMapper;
    private final HeladeraRepository heladeraRepository;
    private final HeladeraMapper heladeraMapper;
    private final TrasladoRepository trasladoRepository;
    private final TrasladoMapper trasladoMapper;
    private final RetiroDTOMapper retiroDTOMapper;
    private final RetiroDTORepository retiroDTORepository;
    private final ViandaMapper viandaMapper;
    private final ViandaRepository viandaRepository;
    private FachadaViandas fachadaViandas;
    private FachadaHeladeras fachadaHeladeras;

    public Fachada() {
        this.rutaRepository = new RutaRepository();
        this.rutaMapper = new RutaMapper();
        this.trasladoMapper = new TrasladoMapper();
        this.trasladoRepository = new TrasladoRepository();
        this.heladeraMapper = new HeladeraMapper();
        this.heladeraRepository = new HeladeraRepository();
        this.retiroDTOMapper = new RetiroDTOMapper();
        this.retiroDTORepository = new RetiroDTORepository();
        this.viandaMapper = new ViandaMapper();
        this.viandaRepository = new ViandaRepository();
    }



    @Override
    public RutaDTO agregar(RutaDTO rutaDTO) {
        Ruta rutaAgregar = new Ruta(rutaDTO.getColaboradorId(), rutaDTO.getHeladeraIdOrigen(), rutaDTO.getHeladeraIdDestino());
        rutaRepository.save(rutaAgregar);

        return rutaMapper.map(rutaAgregar);

    }

    @Override
    public TrasladoDTO asignarTraslado(TrasladoDTO traslado) throws TrasladoNoAsignableException {
        try {
            // Intenta buscar la vianda por el QR
            ViandaDTO viandaDTO = fachadaViandas.buscarXQR(traslado.getQrVianda());

            // Intenta encontrar rutas válidas para el traslado
            List<Ruta> rutasPosibles = rutaRepository.findByHeladeras(traslado.getHeladeraOrigen(), traslado.getHeladeraDestino());

            if (rutasPosibles.isEmpty()) {
                throw new TrasladoNoAsignableException("noasignable");
            }

            Collections.shuffle(rutasPosibles);
            Ruta ruta = rutasPosibles.get(0);

            // Crea un nuevo traslado con la ruta seleccionada y otros detalles del traslado proporcionado
            //TrasladoDTO trasladoNuevo = new TrasladoDTO(traslado.getQrVianda(), EstadoTrasladoEnum.ASIGNADO, LocalDateTime.now(), traslado.getHeladeraOrigen(), traslado.getHeladeraDestino());

            Traslado trasladoNuevo = new Traslado(traslado.getQrVianda(), EstadoTrasladoEnum.ASIGNADO, LocalDateTime.now(), traslado.getHeladeraOrigen(),traslado.getHeladeraDestino());
            trasladoNuevo.setColaboradorId(14L);
            this.trasladoRepository.save(trasladoNuevo);

            // Asigna un colaborador (o lo que corresponda)

            // Aquí podrías guardar el traslado en la base de datos o realizar cualquier otra operación necesaria

            return trasladoMapper.map(trasladoNuevo);
        } catch (NoSuchElementException e) {
            // Si no se encuentra la vianda, arrojar la excepción correspondiente
            throw new NoSuchElementException("No se encontro el traslado");
        }
    }



    @Override
    public List<TrasladoDTO> trasladosDeColaborador(Long aLong, Integer integer, Integer integer1) {
        return null;
    }

    @Override
    public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {
        this.fachadaHeladeras = fachadaHeladeras;
    }

    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas) {
        this.fachadaViandas = fachadaViandas;
    }

    public List<TrasladoDTO> traslados = new ArrayList<>();


    public List<ViandaDTO> viandas = new ArrayList<>();


    @Override
    public void trasladoRetirado(Long idTraslado) {
        TrasladoDTO traslado = buscarXId(idTraslado);
        Traslado trasladomap = this.trasladoRepository.actualizarTrasladoRetirado(idTraslado);
        traslado = trasladoMapper.map(trasladomap);

        //RetiroDTO heladeraRetirada = buscarRetiro(idTraslado);
        RetiroDTO retiroNuevo = new RetiroDTO(traslado.getQrVianda(), "444", LocalDateTime.now(), 444);
        retiroNuevo.setId(idTraslado);
        this.fachadaHeladeras.retirar(retiroNuevo);
        this.fachadaViandas.modificarEstado(traslado.getQrVianda(), EstadoViandaEnum.EN_TRASLADO);

        //Vianda vianda = this.viandaRepository.buscarXQR(traslado.getQrVianda());
        //this.viandaRepository.save(vianda);

        //viandas.add(viandaDTO);
        //Vianda viandaBase = new Vianda(vianda.getCodigoQR(), vianda.getFechaElaboracion(),EstadoViandaEnum.EN_TRASLADO, vianda.getColaboradorId(),vianda.getHeladeraId());

    }

    @Override
    public void trasladoDepositado(Long idTraslado) {

        TrasladoDTO traslado = buscarXId(idTraslado);
        this.trasladoRepository.actualizarTrasladoDepositado(idTraslado);
        //RetiroDTO heladeraRetirada = buscarRetiro(idTraslado);

        RetiroDTO retiroNuevo = new RetiroDTO(traslado.getQrVianda(), "444", LocalDateTime.now(), 444);
        retiroNuevo.setId(idTraslado);
        this.fachadaHeladeras.depositar(retiroNuevo.getHeladeraId(), retiroNuevo.getQrVianda());
        this.fachadaViandas.modificarEstado(traslado.getQrVianda(), EstadoViandaEnum.DEPOSITADA);
        this.fachadaViandas.modificarHeladera(traslado.getQrVianda(),2);
    }

    @Override
    public TrasladoDTO buscarXId(Long aLong) {
        return trasladoMapper.map(this.trasladoRepository.findById(aLong));
    }

    public RetiroDTO buscarRetiro(Long codigo) {
        return retiroDTOMapper.map(this.retiroDTORepository.findByRetiros(codigo));
    }

    public ViandaDTO buscarVianda(String qr) {
        return viandaMapper.map(this.viandaRepository.findById(qr));
    }


}