package com.aero51.moviedatabase.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.aero51.moviedatabase.R;
import com.aero51.moviedatabase.repository.model.NetworkState;
import com.aero51.moviedatabase.repository.model.tmdb.movie.PopularMoviesPage;
import com.aero51.moviedatabase.ui.viewholder.PopularMovieHolder;
import com.aero51.moviedatabase.utils.MovieClickListener;


public class PopularMoviesPagedListAdapter extends PagedListAdapter<PopularMoviesPage.PopularMovie, RecyclerView.ViewHolder> {
    private MovieClickListener itemClickListener;
    private NetworkState networkState;

    public PopularMoviesPagedListAdapter(MovieClickListener itemClickListener) {
        super(DIFF_CALLBACK);
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.movie_item, parent, false);
        PopularMovieHolder viewHolder = new PopularMovieHolder(view, itemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PopularMoviesPage.PopularMovie currentResult = getItem(position);
        ((PopularMovieHolder) holder).bindTo(currentResult, position);
    }


    private static DiffUtil.ItemCallback<PopularMoviesPage.PopularMovie> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<PopularMoviesPage.PopularMovie>() {
                @Override
                public boolean areItemsTheSame(PopularMoviesPage.PopularMovie oldItem, PopularMoviesPage.PopularMovie newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(PopularMoviesPage.PopularMovie oldItem, PopularMoviesPage.PopularMovie newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle());
                }
            };
}
