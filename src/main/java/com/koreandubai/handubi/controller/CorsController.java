package com.koreandubai.handubi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CorsController {

    @RequestMapping(method = RequestMethod.OPTIONS, path = "/**")
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }
}
