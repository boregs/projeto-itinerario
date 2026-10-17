package br.org.itinerario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ItinerarioApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(ItinerarioApplication.class, args);
        if (
            context.getEnvironment().getProperty("app.bootstrap-admin", Boolean.class, false)
        ) context.close();
    }
}
