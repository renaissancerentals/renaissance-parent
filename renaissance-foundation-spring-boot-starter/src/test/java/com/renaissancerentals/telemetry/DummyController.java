package com.renaissancerentals.telemetry;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dummy/telemetry")
class DummyController {
    @GetMapping("/ok")
    public String ok(){
        return "ok";
    }
}
