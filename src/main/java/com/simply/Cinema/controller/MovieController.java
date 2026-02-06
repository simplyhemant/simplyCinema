package com.simply.Cinema.controller;

import com.simply.Cinema.core.movieManagement.dto.MovieDto;
import com.simply.Cinema.service.movieManagement.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
@Slf4j
@Tag(name = "Movie API", description = "Operations related to movie management")
public class MovieController {

    private final MovieService movieService;

    @Operation(
            summary = "Create Movie",
            description = "Creates a new movie (THEATRE_OWNER or ADMIN only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('THEATRE_OWNER', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<MovieDto> createMovie(@RequestBody MovieDto movieDto) {
        log.info("Creating movie: {}", movieDto.getTitle());
        MovieDto created = movieService.createMovie(movieDto);
        log.info("Movie created successfully with ID: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @Operation(
            summary = "Update Movie",
            description = "Updates an existing movie (THEATRE_OWNER only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovie(@RequestBody MovieDto movieDto,
                                                @PathVariable Long movieId) {
        log.info("Updating movie with ID: {}", movieId);
        MovieDto updated = movieService.updateMovie(movieId, movieDto);
        log.info("Movie updated successfully with ID: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete Movie",
            description = "Deletes a movie by ID (THEATRE_OWNER only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId) {
        log.warn("Deleting movie with ID: {}", movieId);
        movieService.deleteMovie(movieId);
        log.info("Movie with ID {} deleted successfully", movieId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get Movie By ID",
            description = "Fetch movie details using movie ID"
    )
    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long movieId) {
        log.debug("Fetching movie with ID: {}", movieId);
        MovieDto movieDto = movieService.getMovieById(movieId);
        return ResponseEntity.ok(movieDto);
    }

    @Operation(
            summary = "Get All Movies",
            description = "Fetch paginated list of all movies"
    )
    @GetMapping("/all")
    public ResponseEntity<Page<MovieDto>> getAllMovies(@RequestParam(defaultValue = "0") int pageNo,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        log.debug("Fetching all movies, pageNo={}, pageSize={}", pageNo, pageSize);
        Page<MovieDto> movieDtoPage = movieService.getAllMovies(pageNo, pageSize);
        return ResponseEntity.ok(movieDtoPage);
    }

    @Operation(
            summary = "Search Movies",
            description = "Search movies by keyword with pagination"
    )
    @GetMapping("/search")
    public ResponseEntity<Page<MovieDto>> searchMovie(@RequestParam String keyword,
                                                      @RequestParam(defaultValue = "0") int pageNo,
                                                      @RequestParam(defaultValue = "10") int pageSize) {
        log.debug("Searching movies with keyword='{}', pageNo={}, pageSize={}", keyword, pageNo, pageSize);
        Page<MovieDto> search = movieService.searchMovies(keyword, pageNo, pageSize);
        return ResponseEntity.ok(search);
    }
}
