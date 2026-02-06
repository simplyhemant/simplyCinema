package com.simply.Cinema.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/")
@Tag(name = "Home API", description = "Basic application entry endpoint")
public class HomeController {

    @Operation(
            summary = "Home Endpoint",
            description = "Returns welcome message for SimplyCinema application"
    )
    @GetMapping()
    public String Home(){
        return "Welcome to simplyCinema.";
    }

}
