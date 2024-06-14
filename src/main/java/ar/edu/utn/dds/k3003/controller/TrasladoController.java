package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

public class TrasladoController {

  private final Fachada fachada;

  public TrasladoController(Fachada fachada) {
    this.fachada = fachada;
  }

  public void asignar(Context context) {
    try {
      var trasladoDTO = context.bodyAsClass(TrasladoDTO.class);
      var trasladoDTORta = this.fachada.asignarTraslado(trasladoDTO);
      context.json(trasladoDTORta);
      context.status(HttpStatus.CREATED);
    } catch (TrasladoNoAsignableException | NoSuchElementException e) {
      context.result(e.getLocalizedMessage());
      context.status(HttpStatus.BAD_REQUEST);
    }
  }

  public void obtener(Context context) {
    var id = context.pathParamAsClass("id", Long.class).get();
    try {
      var trasladoDTO = this.fachada.buscarXId(id);
      context.json(trasladoDTO);
    } catch (NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    }
  }

  public void trasladosColaborador(Context context){
    var id = context.queryParamAsClass("colaboradorId", Long.class).get();
    try {
      var listaDeTraslados = this.fachada.trasladosDeColaborador(id, LocalDateTime.now().getMonthValue(), LocalDateTime.now().getYear());
      context.json(listaDeTraslados);
    } catch (NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    }
  }

}
