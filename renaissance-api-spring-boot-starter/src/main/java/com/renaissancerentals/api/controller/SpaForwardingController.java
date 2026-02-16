package com.renaissancerentals.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SpaForwardingController {

    @RequestMapping({"/{path:[^\\.]*}", "/{path:^(?!api$).*$}/**/{subpath:[^\\.]*}"})
    public String forward(HttpServletRequest request){

        String uri = request.getRequestURI();

        // Let Spring Data REST or any other backend controller serve /api/**
        if (uri.startsWith("/api")) {
            return null;
        }

        // Forward all non-static, non-API routes to index.html
        return "forward:/index.html";
    }
}
