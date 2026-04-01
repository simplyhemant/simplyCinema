package com.simply.Cinema.controller;

import com.simply.Cinema.core.movieManagement.entity.Genre;
import com.simply.Cinema.core.movieManagement.repository.GenreRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@Tag(name = "Genre API", description = "Operations related to movie genres")
public class GenreController {

    private final GenreRepo genreRepo;

    @Operation(summary = "Get All Genres", description = "Fetch all movie genres")
    @PreAuthorize("hasAnyRole('THEATRE_OWNER', 'ADMIN', 'CUSTOMER')")
    @GetMapping("/all")
    public ResponseEntity<List<Genre>> getAllGenres() {
        return ResponseEntity.ok(genreRepo.findAll());
    }
}
