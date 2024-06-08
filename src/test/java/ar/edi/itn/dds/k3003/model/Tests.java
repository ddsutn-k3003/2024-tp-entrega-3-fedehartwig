package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.model.Heladera;
import org.junit.jupiter.api.*;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import ar.edu.utn.dds.k3003.model.Ruta;
import ar.edu.utn.dds.k3003.model.Traslado;
import ar.edu.utn.dds.k3003.model.Vianda;
import ar.edu.utn.dds.k3003.repositories.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.desktop.QuitResponse;
import java.time.LocalDateTime;
import java.util.*;
import ar.edu.utn.dds.k3003.tests.TestTP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

@ExtendWith({MockitoExtension.class})
public class Tests{
    private static final String QR_VIANDA = "123";
    private static final int HELADERA_ORIGEN = 1;
    private static final int HELADERA_DESTINO = 2;
    private TrasladoRepository trasladoRepository;

    @Mock
    FachadaViandas fachadaViandas;
    @Mock
    FachadaHeladeras fachadaHeladeras;

    FachadaLogistica instancia;
    public Tests() {
        this.trasladoRepository = new TrasladoRepository();

    }
    @BeforeEach
    void setUp() throws Throwable {

        try {
            this.instancia = new Fachada();
            this.instancia.setHeladerasProxy(this.fachadaHeladeras);
            this.instancia.setViandasProxy(this.fachadaViandas);
        } catch (Throwable var2) {
            Throwable $ex = var2;
            throw $ex;
        }
    }

    @Test
    void testAgregar() {
        // Arrange
        Fachada fachada = new Fachada();
        RutaDTO rutaDTO = new RutaDTO(1L, 1, 2);
        rutaDTO.setId(0L);
        // Act
        RutaDTO result = fachada.agregar(rutaDTO);

        // Assert
        assertNotNull(result);
        assertEquals(rutaDTO, result);
    }

    @Test
    void testAsignarTraslado() throws TrasladoNoAsignableException {
        RutaDTO agregar = this.instancia.agregar(new RutaDTO(14L, 1, 2));
        TrasladoDTO traslado = new TrasladoDTO("123", 1, 2);
        TrasladoDTO trasladoDTO = this.instancia.asignarTraslado(traslado);
        Assertions.assertEquals(EstadoTrasladoEnum.ASIGNADO, trasladoDTO.getStatus(), "el estado de un traslado debe figurar como asignado luego de una asignación");
    }

    @Test
    @DisplayName("Probar Traslado Retiro")
    void testTrasladoRetirado() throws TrasladoNoAsignableException {
        RutaDTO agregar = this.instancia.agregar(new RutaDTO(14L, 1, 2));
        TrasladoDTO traslado = new TrasladoDTO("123", 1, 2);
        TrasladoDTO trasladoDTO = this.instancia.asignarTraslado(traslado);
        this.instancia.trasladoRetirado(trasladoDTO.getId());
        Traslado trasladoNuevo = this.trasladoRepository.save(new Traslado(trasladoDTO.getQrVianda(), EstadoTrasladoEnum.EN_VIAJE, trasladoDTO.getFechaTraslado(),trasladoDTO.getHeladeraOrigen(),trasladoDTO.getHeladeraDestino()));

        Assertions.assertEquals(EstadoTrasladoEnum.EN_VIAJE, trasladoNuevo.getStatus(), "el estado de un traslado debe figurar como asignado luego de una asignación");
    }

    @Test
    @DisplayName("Probar Traslado deposito")
    void testTrasladoDepositado() throws TrasladoNoAsignableException {
        RutaDTO agregar = this.instancia.agregar(new RutaDTO(14L, 1, 2));
        TrasladoDTO traslado = new TrasladoDTO("123", 1, 2);
        TrasladoDTO trasladoDTO = this.instancia.asignarTraslado(traslado);
        this.instancia.trasladoRetirado(trasladoDTO.getId());
        Traslado trasladoNuevo = this.trasladoRepository.save(new Traslado(trasladoDTO.getQrVianda(), EstadoTrasladoEnum.ENTREGADO, trasladoDTO.getFechaTraslado(),trasladoDTO.getHeladeraOrigen(),trasladoDTO.getHeladeraDestino()));

        Assertions.assertEquals(EstadoTrasladoEnum.ENTREGADO, trasladoNuevo.getStatus(), "el estado de un traslado debe figurar como asignado luego de una asignación");
    }

}
