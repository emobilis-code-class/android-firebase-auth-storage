package com.nix.firebaseauth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImagesRecyclerViewAdapter extends RecyclerView.Adapter<ImagesRecyclerViewAdapter.MyViewHolder> {
    List<String> imageList;
    Context context;
    public ImagesRecyclerViewAdapter(List<String> images, Context context){
        this.imageList = images;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        alt + enter
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.images_view_item_layout,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        //this is we bind view with actual content
        Glide.with(context)
                .load(imageList.get(position))
                .into(holder.myImage);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView myImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myImage = itemView.findViewById(R.id.imageView);

        }
    }
}