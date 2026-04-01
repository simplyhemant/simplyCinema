package com.simply.Cinema.controller;

import com.simply.Cinema.core.movieManagement.entity.Language;
import com.simply.Cinema.core.movieManagement.repository.LanguageRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
@RequiredArgsConstructor
@Tag(name = "Language API", description = "Operations related to movie languages")
public class LanguageController {

    private final LanguageRepo languageRepo;

    @Operation(summary = "Create Language", description = "Create a new movie language (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Language> createLanguage(@RequestBody Language language) {
        return ResponseEntity.ok(languageRepo.save(language));
    }

    @Operation(summary = "Get All Languages", description = "Fetch all available movie languages")
    @GetMapping("/all")
    public ResponseEntity<List<Language>> getAllLanguages() {
        return ResponseEntity.ok(languageRepo.findAll());
    }
}
