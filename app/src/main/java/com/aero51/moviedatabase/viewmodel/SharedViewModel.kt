package com.aero51.moviedatabase.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aero51.moviedatabase.repository.model.epg.ChannelWithPrograms
import com.aero51.moviedatabase.repository.model.epg.EpgProgram
import com.aero51.moviedatabase.repository.model.tmdb.movie.Movie
import com.aero51.moviedatabase.repository.model.tmdb.movie.MoviesByGenrePage
import com.aero51.moviedatabase.repository.model.tmdb.movie.NowPlayingMoviesPage.NowPlayingMovie
import com.aero51.moviedatabase.repository.model.tmdb.movie.PopularMoviesPage.PopularMovie
import com.aero51.moviedatabase.repository.model.tmdb.movie.TopRatedMoviesPage.TopRatedMovie
import com.aero51.moviedatabase.repository.model.tmdb.movie.UpcomingMoviesPage.UpcomingMovie
import com.aero51.moviedatabase.repository.model.tmdb.tvshow.*
import com.aero51.moviedatabase.utils.SingleLiveEvent
import com.google.gson.Gson

class SharedViewModel : ViewModel() {
    private val liveEpgProgram = MutableLiveData<EpgProgram>()
    private val shouldSwitchToEpgDetailsFragment = SingleLiveEvent<Boolean>()
    private var epgIndex: Int? = null
    private val liveChannelWithPrograms = MutableLiveData<ChannelWithPrograms>()
    private val shouldSwitchToEpgAllProgramsFragment = SingleLiveEvent<Boolean>()
    private val shouldSwitchOtherChannelDetailFragment = SingleLiveEvent<Boolean>()
    private val otherChannelIndex: Int? = null
    private val liveMovie = MutableLiveData<Movie>()
    private val liveTvShow = MutableLiveData<TvShow>()
    private val shouldSwitchMovieDetailFragment = SingleLiveEvent<Boolean>()
    private val shouldSwitchTvShowDetailFragment = SingleLiveEvent<Boolean>()
    private var movieIndex: Int? = null
    private var tvShowIndex: Int? = null
    private val liveEpgActorId = MutableLiveData<Int>()
    private val liveMovieActorId = MutableLiveData<Int>()
    private val liveTvShowActorId = MutableLiveData<Int>()
    private val liveMovieAndTvShowActorSearchId = MutableLiveData<Int>()
    private val shouldSwitchEpgActorFragment = SingleLiveEvent<Boolean>()
    private val shouldSwitchMovieActorFragment = SingleLiveEvent<Boolean>()
    private val shouldSwitchTvActorFragment = SingleLiveEvent<Boolean>()
    private val shouldSwitchMovieAndTvShowActorFragment = SingleLiveEvent<Boolean>()
    private var castIndex: Int? = null
    private val liveGenreId = MutableLiveData<Int>()
    private val shouldSwitchMoviesByGenreListFragment = SingleLiveEvent<Boolean>()
    private val shouldSwitchMoviesByGenreListFragmentFromMovieDetailsFragment = SingleLiveEvent<Boolean>()
    private val shouldSwitchTvShowsByGenreListFragment = SingleLiveEvent<Boolean>()
    val hasEpgTvFragmentFinishedLoading = MutableLiveData<Boolean>()

    init {
        //used when process is killed and tv shows fragment is selected, otherwise tv shows fragment would be empty on relaunch
        hasEpgTvFragmentFinishedLoading.value = true
    }


    fun changeToEpgDetailsFragment(index: Int?, epgProgram: EpgProgram) {
        epgIndex = index
        liveEpgProgram.value = epgProgram
        shouldSwitchToEpgDetailsFragment.value = true
    }

    val liveDataProgram: LiveData<EpgProgram>
        get() = liveEpgProgram

    val singleLiveShouldSwitchToEpgTvDetailsFragment: LiveData<Boolean>
        get() = shouldSwitchToEpgDetailsFragment

    fun changeToEpgAllProgramsFragment(channelWithPrograms: ChannelWithPrograms) {
        liveChannelWithPrograms.value = channelWithPrograms
        shouldSwitchToEpgAllProgramsFragment.value = true
    }

    val liveDataChannelWithPrograms: LiveData<ChannelWithPrograms>
        get() = liveChannelWithPrograms

    val singleLiveShouldSwitchToEpgAllProgramsFragment: LiveData<Boolean>
        get() = shouldSwitchToEpgAllProgramsFragment

    //done like this to reduce code duplication(fragments, listeners, main activity identifiers
    fun changeToMoviedetailsFragment(movieObject: Any?, position: Int?) {
        var movie = Movie()
        if (movieObject is TopRatedMovie) {
            movie = transformTopRatedMovie(movieObject)
        }
        if (movieObject is NowPlayingMovie) {
            movie = transformNowPlayingMovie(movieObject)
        }
        if (movieObject is PopularMovie) {
            movie = transformPopularMovie(movieObject)
        }
        if (movieObject is UpcomingMovie) {
            movie = transformUpcomingMovie(movieObject)
        }
        if (movieObject is MoviesByGenrePage.MovieByGenre) {
            movie = transformMovieByGenre(movieObject)
        }
        movieIndex = position
        liveMovie.value = movie
        shouldSwitchMovieDetailFragment.value = true
    }

    //done like this to reduce code duplication(fragments, listeners, main activity identifiers
    fun changeToTvShowDetailsFragment(tvShowObject: Any?, position: Int?) {
        var tvShow = TvShow()
        if (tvShowObject is PopularTvShowsPage.PopularTvShow) {
            val popularTvShow = tvShowObject as PopularTvShowsPage.PopularTvShow
            tvShow = transformPopularTvShow(popularTvShow)
        }
        if (tvShowObject is TrendingTvShowsPage.TrendingTvShow) {
            val trendingTvShow = tvShowObject as TrendingTvShowsPage.TrendingTvShow
            tvShow = transformTrendingTvShow(trendingTvShow)
        }
        if (tvShowObject is AiringTvShowsPage.AiringTvShow) {
            val airingTvShow = tvShowObject as AiringTvShowsPage.AiringTvShow
            tvShow = transformAiringTvShow(airingTvShow)
        }
        if (tvShowObject is TvShowsByGenrePage.TvShowByGenre) {
            tvShow = transformTvShowByGenre(tvShowObject)
        }
        if (tvShowObject is TvShowSearchResult.TvShow) {
            tvShow = transformTvShowSearchResult(tvShowObject)
        }

        tvShowIndex = position
        liveTvShow.setValue(tvShow)
        shouldSwitchTvShowDetailFragment.setValue(true)
    }

    val liveDataMovie: LiveData<Movie>
        get() = liveMovie

    val liveDataTvShow: LiveData<TvShow>
        get() = liveTvShow

    val singleLiveShouldSwitchMovieDetailsFragment: LiveData<Boolean>
        get() = shouldSwitchMovieDetailFragment

    val singleLiveShouldSwitchTvShowDetailsFragment: LiveData<Boolean>
        get() = shouldSwitchTvShowDetailFragment

    fun changeToEpgActorFragment(position: Int?, actorId: Int) {
        castIndex = position
        liveEpgActorId.value = actorId
        shouldSwitchEpgActorFragment.value = true
    }
    fun changeToMovieActorFragment(position: Int?, actorId: Int) {
        castIndex = position
        liveMovieActorId.value = actorId
        shouldSwitchMovieActorFragment.value = true
    }
    fun changeToTvActorFragment(position: Int?, actorId: Int) {
        castIndex = position
        liveTvShowActorId.value = actorId
        shouldSwitchTvActorFragment.value = true
    }

    fun changeToMoviesByGenreListFragment(genreId: Int, position: Int) {
        liveGenreId.value = genreId
        shouldSwitchMoviesByGenreListFragment.value = true
    }
    fun changeToMoviesByGenreListFragmentFromMovieDetailsFragment(genreId: Int, position: Int) {
        liveGenreId.value = genreId
        shouldSwitchMoviesByGenreListFragmentFromMovieDetailsFragment.value = true
    }

    fun changeToTvShowsByGenreListFragment(genreId: Int, position: Int?) {
        liveGenreId.value = genreId
        shouldSwitchTvShowsByGenreListFragment.value = true
    }

    fun changeToMoviesAndTvShowsActorFragment(position: Int?, actorId: Int) {
        castIndex = position
        liveMovieAndTvShowActorSearchId.value = actorId
        shouldSwitchMovieAndTvShowActorFragment.value = true
    }



    val liveDataEpgActorId: LiveData<Int>
        get() = liveEpgActorId

    val liveDataMovieActorId: LiveData<Int>
        get() = liveMovieActorId

    val liveDataTvShowActorId: LiveData<Int>
        get() = liveTvShowActorId

    val liveDataMovieAndTvShowActorSearchId: LiveData<Int>
        get() = liveMovieAndTvShowActorSearchId

    val singleLiveShouldSwitchEpgActorFragment: LiveData<Boolean>
        get() = shouldSwitchEpgActorFragment

    val singleLiveShouldSwitchMovieActorFragment: LiveData<Boolean>
        get() = shouldSwitchMovieActorFragment

    val singleLiveShouldSwitchTvActorFragment: LiveData<Boolean>
        get() = shouldSwitchTvActorFragment

    val liveDataGenreId: LiveData<Int>
        get() = liveGenreId

    val singleLiveShouldSwitchMoviesByGenreListFragment: LiveData<Boolean>
        get() = shouldSwitchMoviesByGenreListFragment

    val singleLiveShouldSwitchMoviesByGenreListFragmentFromMovieDetailsFragment: LiveData<Boolean>
        get() = shouldSwitchMoviesByGenreListFragmentFromMovieDetailsFragment

    val singleLiveShouldSwitchTvShowsByGenreListFragment: LiveData<Boolean>
        get() = shouldSwitchTvShowsByGenreListFragment

    val singleLiveShouldSwitchMoviesAndTvShowsActorFragment: LiveData<Boolean>
        get() = shouldSwitchMovieAndTvShowActorFragment

    fun setHasEpgTvFragmentFinishedLoading(hasEpgTvFragmentFinishedLoading: Boolean) {
        this.hasEpgTvFragmentFinishedLoading.value = hasEpgTvFragmentFinishedLoading
    }

    fun getHasEpgTvFragmentFinishedLoading(): LiveData<Boolean> {
        return hasEpgTvFragmentFinishedLoading
    }

    //deserialization and serialization
    private fun transformTopRatedMovie(original: TopRatedMovie?): Movie {
        val gson = Gson()
        return gson.fromJson(gson.toJson(original), Movie::class.java)
    }

    private fun transformNowPlayingMovie(original: NowPlayingMovie?): Movie {
        val gson = Gson()
        return gson.fromJson(gson.toJson(original), Movie::class.java)
    }

    private fun transformPopularMovie(original: PopularMovie?): Movie {
        val gson = Gson()
        return gson.fromJson(gson.toJson(original), Movie::class.java)
    }

    private fun transformUpcomingMovie(original: UpcomingMovie?): Movie {
        val gson = Gson()
        return gson.fromJson(gson.toJson(original), Movie::class.java)
    }
    private fun transformMovieByGenre(original: MoviesByGenrePage.MovieByGenre?): Movie {
        val gson = Gson()
        return gson.fromJson(gson.toJson(original), Movie::class.java)
    }


    private fun transformPopularTvShow(original: PopularTvShowsPage.PopularTvShow?): TvShow {
        val gson = Gson()
        return gson.fromJson(gson.toJson(original), TvShow::class.java)
    }
    private fun transformTrendingTvShow(original: TrendingTvShowsPage.TrendingTvShow?): TvShow {
        val gson = Gson()
        return gson.fromJson(gson.toJson(original), TvShow::class.java)
    }
    private fun transformAiringTvShow(original: AiringTvShowsPage.AiringTvShow?): TvShow {
        val gson = Gson()
        return gson.fromJson(gson.toJson(original), TvShow::class.java)
    }
    private fun transformTvShowByGenre(original: TvShowsByGenrePage.TvShowByGenre?): TvShow {
        val gson = Gson()
        return gson.fromJson(gson.toJson(original), TvShow::class.java)
    }
    private fun transformTvShowSearchResult(original: TvShowSearchResult.TvShow?): TvShow {
        val gson = Gson()
        return gson.fromJson(gson.toJson(original), TvShow::class.java)
    }
}