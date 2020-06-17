package com.aero51.moviedatabase.ui.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aero51.moviedatabase.R;
import com.aero51.moviedatabase.repository.model.TopRatedMovie;
import com.aero51.moviedatabase.utils.TopRatedItemClickListener;
import com.squareup.picasso.Picasso;

public class TopRatedMovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TopRatedMovie result;
    private int position;

    private ImageView imageView;
    private TextView textViewPosition;
    private TextView textViewtitle;
    private TextView textViewVoteAverage;
    private TopRatedItemClickListener itemClickListener;


    public TopRatedMovieHolder(@NonNull View itemView, TopRatedItemClickListener itemClickListener) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image_view);
        textViewPosition = itemView.findViewById(R.id.text_view_position);
        textViewtitle = itemView.findViewById(R.id.text_view_title);

        this.itemClickListener = itemClickListener;
        itemView.setOnClickListener(this);

    }

    public void bindTo(TopRatedMovie result, int position) {
        this.result = result;
        this.position=position;

        textViewPosition.setText(String.valueOf(position + 1));
        textViewtitle.setText(result.getTitle());


        String baseUrl = "https://image.tmdb.org/t/p/w92";
        String imageUrl = baseUrl + result.getPoster_path();
        //    Log.d("moviesadapter", "imageUrl: " + imageUrl);
           Picasso.get().load(imageUrl).into(imageView);


/*
        poster
        https://image.tmdb.org/t/p/w92/5KCVkau1HEl7ZzfPsKAPM0sMiKc.jpg

        backdrop
        w300
        /avedvodAZUcwqevBfm8p4G2NziQ.jpg
        https://image.tmdb.org/t/p/w300/avedvodAZUcwqevBfm8p4G2NziQ.jpg
 */
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null&& position != RecyclerView.NO_POSITION) {
            itemClickListener.OnItemClick(result,position); // call the onClick in the OnItemClickListener
            Log.d("moviedatabaselog", " Item clicked inside top rated holder : " + position);
        }
    }
}