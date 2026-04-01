package com.simply.Cinema.controller;

import com.simply.Cinema.core.movieManagement.entity.Language;
import com.simply.Cinema.core.movieManagement.repository.LanguageRepo;
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
@RequestMapping("/api/languages")
@RequiredArgsConstructor
@Tag(name = "Language API", description = "Operations related to movie languages")
public class LanguageController {

    private final LanguageRepo languageRepo;

    @Operation(summary = "Get All Languages", description = "Fetch all available movie languages")
    @PreAuthorize("hasAnyRole('THEATRE_OWNER', 'ADMIN', 'CUSTOMER')")
    @GetMapping("/all")
    public ResponseEntity<List<Language>> getAllLanguages() {
        return ResponseEntity.ok(languageRepo.findAll());
    }
}
