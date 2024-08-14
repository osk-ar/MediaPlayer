package com.example.mediaplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import soup.neumorphism.NeumorphImageButton;

public class SoundListAdapter extends RecyclerView.Adapter<SoundListAdapter.SoundViewHolder>{
    private ArrayList<SoundsClass> soundList = new ArrayList<>();

    @NonNull
    @Override
    public SoundListAdapter.SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SoundViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SoundListAdapter.SoundViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv.setText(soundList.get(position).getName());
        holder.img.setBackgroundResource(soundList.get(position).imageResId);

        holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(holder.itemView.getContext(),MainActivity.class);
                    i.putExtra("soundID", position);
                    i.putExtra("soundTitle", soundList.get(position).getName());
                    holder.itemView.getContext().startActivity(i);
                }
            });
    }

    public void setSoundList(ArrayList<SoundsClass> soundlist){
        this.soundList = soundlist;
    }

    @Override
    public int getItemCount() {
        return soundList.size();
    }

    public static class SoundViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tv;

        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);

            tv = itemView.findViewById(R.id.sound_name);
            img = itemView.findViewById(R.id.small_img);


        }
    }
}
