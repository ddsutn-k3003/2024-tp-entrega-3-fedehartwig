package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladerasProxy;
import ar.edu.utn.dds.k3003.clients.ViandasProxy;
import ar.edu.utn.dds.k3003.controller.RutaController;
import ar.edu.utn.dds.k3003.controller.TrasladoController;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class WebApp {


    public static void main(String[] args) {



        var env = System.getenv();
        // Variables de entorno
        var URL_VIANDAS = env.get("URL_VIANDAS");
        var URL_LOGISTICA = env.get("URL_LOGISTICA");
        var URL_HELADERAS = env.get("URL_HELADERAS");
        var URL_COLABORADORES = env.get("URL_COLABORADORES");


        var objectMapper = createObjectMapper();
        var fachada = new Fachada();

        var port = Integer.parseInt(env.getOrDefault("PORT", "8080"));

        fachada.setViandasProxy(new ViandasProxy(objectMapper));
        fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));

        var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                configureObjectMapper(mapper);
            }));
        }).start(port);

        var rutaController = new RutaController(fachada);
        var trasladosController = new TrasladoController(fachada);

        app.get("/", ctx -> ctx.result("Home"));
        app.post("/rutas", rutaController::agregar);
        app.get("/traslados/search/findByColaboradorId", trasladosController::trasladosColaborador);
        app.post("/traslados", trasladosController::asignar);
        app.post("/borrar", trasladosController::borrar);
        app.get("/traslados/{id}", trasladosController::obtener);
    }

    public static ObjectMapper createObjectMapper() {
        var objectMapper = new ObjectMapper();
        configureObjectMapper(objectMapper);
        return objectMapper;
    }

    public static void configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);
    }
}
