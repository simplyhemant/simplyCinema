package com.simply.Cinema.controller;

import com.simply.Cinema.core.movieManagement.dto.MovieReviewDto;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;
import com.simply.Cinema.service.movieManagement.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/movies/{movieId}")
    public ResponseEntity<MovieReviewDto> submitReview(@PathVariable Long movieId,
                                                       @RequestBody MovieReviewDto reviewDto)
            throws ValidationException, ResourceNotFoundException {
        log.info("Submitting new review for movieId={}", movieId);
        reviewDto.setMovieId(movieId);
        MovieReviewDto created = reviewService.submitReview(reviewDto);
        log.info("Review submitted successfully with ID={} for movieId={}", created.getId(), movieId);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieReviewDto> updateReview(@PathVariable Long id,
                                                       @RequestBody MovieReviewDto reviewDto)
            throws ResourceNotFoundException, AuthorizationException {
        log.info("Updating review with ID={}", id);
        MovieReviewDto updated = reviewService.updateReview(id, reviewDto);
        log.info("Review updated successfully with ID={}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id)
            throws ResourceNotFoundException, AuthorizationException {
        log.warn("Deleting review with ID={}", id);
        reviewService.deleteReview(id);
        log.info("Review with ID={} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<List<MovieReviewDto>> getReviews(@PathVariable Long movieId)
            throws ResourceNotFoundException {
        log.debug("Fetching reviews for movieId={}", movieId);
        List<MovieReviewDto> reviews = reviewService.getReviewsByMovie(movieId);
        return ResponseEntity.ok(reviews);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/moderate")
    public ResponseEntity<MovieReviewDto> moderateReview(@PathVariable Long id)
            throws ResourceNotFoundException {
        log.warn("Moderating review with ID={}", id);
        MovieReviewDto moderated = reviewService.moderateReview(id);
        log.info("Review with ID={} moderated successfully", id);
        return ResponseEntity.ok(moderated);
    }

    @GetMapping("/{movieId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long movieId) {
        log.debug("Calculating average rating for movieId={}", movieId);
        Double averageRating = reviewService.calculateAverageRating(movieId);
        log.info("Average rating for movieId={} is {}", movieId, averageRating);
        return ResponseEntity.ok(averageRating);
    }
}

