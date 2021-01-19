package com.aero51.moviedatabase.repository.retrofit;

import androidx.lifecycle.LiveData;

import com.aero51.moviedatabase.repository.model.tmdb.credits.Actor;
import com.aero51.moviedatabase.repository.model.tmdb.credits.ActorImagesResponse;
import com.aero51.moviedatabase.repository.model.tmdb.credits.ActorSearchResponse;
import com.aero51.moviedatabase.repository.model.tmdb.credits.MovieCredits;
import com.aero51.moviedatabase.repository.model.tmdb.movie.MovieSearchResult;
import com.aero51.moviedatabase.repository.model.tmdb.movie.PopularMoviesPage;
import com.aero51.moviedatabase.repository.model.tmdb.movie.NowPlayingMoviesPage;

import com.aero51.moviedatabase.repository.model.tmdb.movie.UpcomingMoviesPage;
import com.aero51.moviedatabase.repository.model.tmdb.tvshow.AiringTvShowsPage;
import com.aero51.moviedatabase.repository.model.tmdb.tvshow.PopularTvShowsPage;
import com.aero51.moviedatabase.repository.model.tmdb.tvshow.TvShowSearchResult;
import com.aero51.moviedatabase.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDbApi {

    //region is optional
    @GET("movie/popular")
    Call<PopularMoviesPage> getPopularMovies(@Query("api_key") String api_key
            , @Query("page") Integer page, @Query("region") String region);

    //region is optional
    @GET("movie/now_playing")
    Call<NowPlayingMoviesPage> getNowPlayingMovies(@Query("api_key") String api_key
            , @Query("page") Integer page, @Query("region") String region);

    //region is optional
    @GET("movie/upcoming")
    Call<UpcomingMoviesPage> getUpcomingMovies(@Query("api_key") String api_key
            , @Query("page") Integer page, @Query("region") String region);

    //region is optional
    @GET("tv/popular")
    Call<PopularTvShowsPage> getPopularTvShows(@Query("api_key") String api_key
            , @Query("page") Integer page, @Query("region") String region);

    //region is optional
    @GET("tv/on_the_air")
    Call<AiringTvShowsPage> getAiringTvShows(@Query("api_key") String api_key
            , @Query("page") Integer page, @Query("region") String region);

    @GET("movie/{movie_id}/credits")
    Call<MovieCredits> getMovieCredits(@Query("api_key") String api_key, @Path("movie_id") Integer movie_id);

    @GET("movie/{movie_id}/credits")
    LiveData<ApiResponse<MovieCredits>> getLiveMovieCredits(@Path("movie_id") Integer movie_id, @Query("api_key") String api_key);

    @GET("person/{person_id}")
    LiveData<ApiResponse<Actor>> getLivePerson(@Path("person_id") Integer person_id, @Query("api_key") String api_key);

    // GET /person/{person_id}/images
    @GET("person/{person_id}/images")
    LiveData<ApiResponse<ActorImagesResponse>> getLivePersonImages(@Path("person_id") Integer person_id, @Query("api_key") String api_key);


    @GET("search/person")
    LiveData<ApiResponse<ActorSearchResponse>> getLivePersonSearch(@Query("api_key") String api_key, @Query("query") String person);

    @GET("search/person")
    Call<ActorSearchResponse> getPersonsSearch(@Query("api_key") String api_key, @Query("query") String person, @Query("page") Integer page);

    @GET("search/movie")
    Call<MovieSearchResult> getMoviesSearch(@Query("api_key") String api_key, @Query("query") String movieSearch, @Query("page") Integer page);

    @GET("search/tv")
    Call<TvShowSearchResult> getTvShowsSearch(@Query("api_key") String api_key, @Query("query") String movieSearch, @Query("page") Integer page);


    //region is optional
    //  @GET("movie/top_rated")
    // Call<MoviesPage> getNewTopRatedMovies(@Query("api_key") String api_key
    //   , @Query("page") Integer page, @Query("region") String region);


}
