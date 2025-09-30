package com.simply.Cinema.controller;

import com.simply.Cinema.core.movieManagement.dto.MovieDto;
import com.simply.Cinema.service.movieManagement.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
@Slf4j
public class MovieController {

    private final MovieService movieService;

    @PreAuthorize("hasAnyRole('THEATRE_OWNER', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<MovieDto> createMovie(@RequestBody MovieDto movieDto) {
        log.info("Creating movie: {}", movieDto.getTitle());
        MovieDto created = movieService.createMovie(movieDto);
        log.info("Movie created successfully with ID: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovie(@RequestBody MovieDto movieDto,
                                                @PathVariable Long movieId) {
        log.info("Updating movie with ID: {}", movieId);
        MovieDto updated = movieService.updateMovie(movieId, movieDto);
        log.info("Movie updated successfully with ID: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId) {
        log.warn("Deleting movie with ID: {}", movieId);
        movieService.deleteMovie(movieId);
        log.info("Movie with ID {} deleted successfully", movieId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long movieId) {
        log.debug("Fetching movie with ID: {}", movieId);
        MovieDto movieDto = movieService.getMovieById(movieId);
        return ResponseEntity.ok(movieDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<MovieDto>> getAllMovies(@RequestParam(defaultValue = "0") int pageNo,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        log.debug("Fetching all movies, pageNo={}, pageSize={}", pageNo, pageSize);
        Page<MovieDto> movieDtoPage = movieService.getAllMovies(pageNo, pageSize);
        return ResponseEntity.ok(movieDtoPage);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MovieDto>> searchMovie(@RequestParam String keyword,
                                                      @RequestParam(defaultValue = "0") int pageNo,
                                                      @RequestParam(defaultValue = "10") int pageSize) {
        log.debug("Searching movies with keyword='{}', pageNo={}, pageSize={}", keyword, pageNo, pageSize);
        Page<MovieDto> search = movieService.searchMovies(keyword, pageNo, pageSize);
        return ResponseEntity.ok(search);
    }
}

