package com.example.android.movieapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.movieapp.model.VideoModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.TrailerListViewHolder>{

    private List<VideoModel> trailers;
    private Context context;

    private final TrailerListAdapterOnClickHandler clickHandler;

    public interface TrailerListAdapterOnClickHandler{
         void onClick(VideoModel trailer);
    }

    public TrailerListAdapter(Context context, TrailerListAdapterOnClickHandler clickHandler){
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @Override
    public void onBindViewHolder(TrailerListViewHolder holder, int position) {
        String thumbnailPath = "http://img.youtube.com/vi/" +
                trailers.get(position).getKey() + "/0.jpg";
        Picasso.with(context)
                .load(thumbnailPath)
                .into(holder.thumbnail);
    }

    @Override
    public TrailerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_recycler_view_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TrailerListViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if(trailers == null) return 0;
        return trailers.size();
    }



    public class TrailerListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView thumbnail;

        public TrailerListViewHolder(View view){
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            VideoModel trailer = trailers.get(adapterPosition);
            clickHandler.onClick(trailer);
        }
    }

    public void setTrailerData(List<VideoModel> trailers){
        this.trailers = trailers;
        notifyDataSetChanged();
    }
}
