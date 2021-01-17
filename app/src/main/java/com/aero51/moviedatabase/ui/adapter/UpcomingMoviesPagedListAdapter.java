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
import com.aero51.moviedatabase.repository.model.tmdb.movie.UpcomingMoviesPage;
import com.aero51.moviedatabase.ui.viewholder.UpcomingMovieHolder;
import com.aero51.moviedatabase.utils.MovieClickListener;

public class UpcomingMoviesPagedListAdapter extends PagedListAdapter<UpcomingMoviesPage.UpcomingMovie, RecyclerView.ViewHolder> {
    private MovieClickListener itemClickListener;
    private NetworkState networkState;

    public UpcomingMoviesPagedListAdapter(MovieClickListener itemClickListener) {
        super(DIFF_CALLBACK);
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.movie_item, parent, false);
        UpcomingMovieHolder viewHolder = new UpcomingMovieHolder(view, itemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UpcomingMoviesPage.UpcomingMovie currentResult = getItem(position);
        ((UpcomingMovieHolder) holder).bindTo(currentResult, position);
    }


    private static DiffUtil.ItemCallback<UpcomingMoviesPage.UpcomingMovie> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UpcomingMoviesPage.UpcomingMovie>() {
                @Override
                public boolean areItemsTheSame(UpcomingMoviesPage.UpcomingMovie oldItem, UpcomingMoviesPage.UpcomingMovie newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(UpcomingMoviesPage.UpcomingMovie oldItem, UpcomingMoviesPage.UpcomingMovie newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle());
                }
            };

}
