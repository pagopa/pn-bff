package it.pagopa.pn.bff;

import it.pagopa.pn.commons.configs.listeners.TaskIdApplicationListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class BffApplication {
    

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BffApplication.class);
        app.addListeners(new TaskIdApplicationListener());
        app.run(args);
    }

    @RestController
    @RequestMapping("")
    public static class RootController {

        @GetMapping("")
        public String home() {
            return "Sono Vivo!";
        }
    }
}