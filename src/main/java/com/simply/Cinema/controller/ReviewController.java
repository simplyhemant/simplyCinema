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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Slf4j
@Tag(name = "Review API", description = "Operations related to movie reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(
            summary = "Submit Review",
            description = "Submit a new review for a specific movie"
    )
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

    @Operation(
            summary = "Update Review",
            description = "Update an existing review (Only review owner allowed)"
    )
    @PutMapping("/{id}")
    public ResponseEntity<MovieReviewDto> updateReview(@PathVariable Long id,
                                                       @RequestBody MovieReviewDto reviewDto)
            throws ResourceNotFoundException, AuthorizationException {
        log.info("Updating review with ID={}", id);
        MovieReviewDto updated = reviewService.updateReview(id, reviewDto);
        log.info("Review updated successfully with ID={}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete Review",
            description = "Delete a review (Only review owner allowed)"
    )
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id)
            throws ResourceNotFoundException, AuthorizationException {
        log.warn("Deleting review with ID={}", id);
        reviewService.deleteReview(id);
        log.info("Review with ID={} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get Reviews By Movie",
            description = "Fetch all reviews for a specific movie"
    )
    @GetMapping("/{movieId}")
    public ResponseEntity<List<MovieReviewDto>> getReviews(@PathVariable Long movieId)
            throws ResourceNotFoundException {
        log.debug("Fetching reviews for movieId={}", movieId);
        List<MovieReviewDto> reviews = reviewService.getReviewsByMovie(movieId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(
            summary = "Moderate Review",
            description = "Approve or moderate a review (Admin only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/moderate")
    public ResponseEntity<MovieReviewDto> moderateReview(@PathVariable Long id)
            throws ResourceNotFoundException {
        log.warn("Moderating review with ID={}", id);
        MovieReviewDto moderated = reviewService.moderateReview(id);
        log.info("Review with ID={} moderated successfully", id);
        return ResponseEntity.ok(moderated);
    }

    @Operation(
            summary = "Get Average Rating",
            description = "Calculate and return the average rating of a movie"
    )
    @GetMapping("/{movieId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long movieId) {
        log.debug("Calculating average rating for movieId={}", movieId);
        Double averageRating = reviewService.calculateAverageRating(movieId);
        log.info("Average rating for movieId={} is {}", movieId, averageRating);
        return ResponseEntity.ok(averageRating);
    }
}
