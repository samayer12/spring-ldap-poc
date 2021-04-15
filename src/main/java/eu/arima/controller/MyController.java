package eu.arima.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Controller Definitions.
 */
@RestController
public class MyController {

    @GetMapping("/")
    public String init() {
        return "Home page";
    }

    @GetMapping("/secure")
    public String secure() {
        return "Only authorized users can see this page";
    }

}
