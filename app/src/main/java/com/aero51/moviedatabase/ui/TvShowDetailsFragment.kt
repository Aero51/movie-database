package com.aero51.moviedatabase.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aero51.moviedatabase.R
import com.aero51.moviedatabase.databinding.FragmentMovieDetailsBinding
import com.aero51.moviedatabase.ui.adapter.MovieCastAdapter
import com.aero51.moviedatabase.ui.adapter.TvShowCastAdapter
import com.aero51.moviedatabase.utils.Constants
import com.aero51.moviedatabase.utils.Status
import com.aero51.moviedatabase.viewmodel.DetailsViewModel
import com.aero51.moviedatabase.viewmodel.SharedViewModel
import com.squareup.picasso.Picasso

class TvShowDetailsFragment: Fragment(), MovieCastAdapter.ItemClickListener {
    private var binding: FragmentMovieDetailsBinding? = null
    private var detailsViewModel: DetailsViewModel? = null
    private var tvShowCastAdapter: TvShowCastAdapter? = null
    private var sharedViewModel: SharedViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        detailsViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(DetailsViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        // Log.d(Constants.LOG, "TopRatedMovieDetailsFragment onCreateView " );
        //cover_image_view = getActivity().findViewById(R.id.expandedImage);
        binding!!.castRecyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding!!.castRecyclerView.layoutManager = linearLayoutManager
        //castRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        registerSharedViewModelObserver()
        val toolbar = requireActivity().findViewById<View>(R.id.toolbar) as Toolbar
        //toolbar.setTitle("text");
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
            Log.d(Constants.LOG, "Toolbar clicked!")
            showBackButton(false)
        }
        showBackButton(true)
        return binding!!.root
    }

    private fun registerSharedViewModelObserver() {
        sharedViewModel!!.liveDataTvShow.observe(viewLifecycleOwner, Observer { tvShow ->
            binding!!.title.text = tvShow.name
            binding!!.releaseDate.text = tvShow.id.toString()
            binding!!.overview.text = tvShow.overview
            binding!!.tmdbRating.text = tvShow.vote_average.toString()
            val imageUrl: String = Constants.BASE_IMAGE_URL + Constants.BACKDROP_SIZE_W780 + tvShow.backdrop_path
            val posterUrl = Constants.BASE_IMAGE_URL + Constants.POSTER_SIZE_W154 + tvShow.poster_path
            Picasso.get().load(imageUrl).fit().centerCrop().placeholder(R.drawable.picture_template).into(binding!!.coverImageView)
            Picasso.get().load(posterUrl).fit().centerCrop().placeholder(R.drawable.picture_template).into(binding!!.posterImageView)
            binding!!.coverImageView.visibility = View.VISIBLE
            registerTvShowCastObserver(tvShow.id)
            registerOmdbDetailsObserver(tvShow.name)
        })
    }
    private fun registerTvShowCastObserver(tvShowId: Int) {
        detailsViewModel!!.getTvShowCast(tvShowId).observe(viewLifecycleOwner, Observer { (status, data) -> // movieDetailsViewModel.getMovieCast(topRatedMovieId).removeObserver(this);
            if (data != null) {
                Log.d(Constants.LOG, " status: " + status + " list size: " + data.size)
                tvShowCastAdapter = TvShowCastAdapter(context, data)
                tvShowCastAdapter!!.setClickListener { view: View, actorId: Int, position: Int -> onItemClick(view, actorId, position) }
                binding!!.castRecyclerView.adapter = tvShowCastAdapter
            }
        })
    }
    private fun registerOmdbDetailsObserver(tvShowTitle: String) {
        //TODO  upcoming movies are not yet present on omd api
        detailsViewModel!!.getOmbdDetails(tvShowTitle).observe(viewLifecycleOwner, Observer { (status, data,errorMsg) ->
            if (data != null && status== Status.SUCCESS) {
                Log.d("nikola", "imdbRating: " + (data.imdbRating))
                Log.d("nikola", "ratings: " + (data.ratings?.get(0)?.source) + " , " + data.ratings?.get(0)?.value)

                //TODO   implement hiding of different rating text views (drawables) based if they are present in the list
                val tvShowRatingsList= data.ratings
                for(tvShowRating in tvShowRatingsList)
                {
                    if(tvShowRating.source.equals("Internet Movie Database")){
                        binding!!.imdbRating.text = tvShowRating.value
                    }
                    if(tvShowRating.source.equals("Rotten Tomatoes")){
                        binding!!.rottenTomatoesRating.text = tvShowRating.value
                    }
                    if(tvShowRating.source.equals("Metacritic")){
                        binding!!.metacriticRating.text = tvShowRating.value
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun showBackButton(show: Boolean) {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(show)
        }
    }

    override fun onItemClick(view: View?, actorId: Int, position: Int) {
        sharedViewModel!!.changeToActorFragment(position, actorId)
    }
}
