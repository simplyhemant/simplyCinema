package com.simply.Cinema.service.movieManagement;

import com.simply.Cinema.core.movieManagement.dto.MovieDto;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;
import org.springframework.data.domain.Page;


public interface MovieService {

    MovieDto createMovie(MovieDto movieDto) throws ValidationException, BusinessException;

    MovieDto updateMovie(Long movieId, MovieDto movieDto) throws ResourceNotFoundException, AuthorizationException;

    void deleteMovie(Long movieId)
            throws ResourceNotFoundException, BusinessException;

    MovieDto getMovieById(Long movieId)
            throws ResourceNotFoundException;

    Page<MovieDto> searchMovies(String keyword, int page, int size) throws ValidationException ;

    Page<MovieDto> getAllMovies(int page, int size) ;

    Page<MovieDto> getNowShowingMovies(int page, int size);

    Page<MovieDto> getUpcomingMovies(int page, int size);

    Page<MovieDto> getMoviesByGenre(Long genreId, int page, int size);
}
