package com.example.rtdatabase;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<Data> dataList;

    public MyAdapter(Context context, List<Data> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recTitle.setText(dataList.get(position).getDataTitle());
        holder.recDesc.setText(dataList.get(position).getDataDesc());
        holder.recLang.setText(dataList.get(position).getDataLang());
        String link=dataList.get(position).getDataAudio();
        holder.play_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    MediaPlayer mediaPlayer=new MediaPlayer();
                    try
                    {
                        mediaPlayer.setDataSource(link);
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });
                        mediaPlayer.prepare();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }
        });
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,DetailActivity.class);
                intent.putExtra("Image",dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Description",dataList.get(holder.getAdapterPosition()).getDataDesc());
                intent.putExtra("Title",dataList.get(holder.getAdapterPosition()).getDataTitle());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage;
    TextView recTitle, recLang, recDesc;
    CardView recCard;
    Button play_song;
    public MyViewHolder(@NonNull View v)
    {
        super(v);
        play_song=v.findViewById(R.id.play_Songs);
        recImage=v.findViewById(R.id.recImage);
        recTitle=v.findViewById(R.id.recTitle);
        recLang=v.findViewById(R.id.recLang);
        recDesc=v.findViewById(R.id.recDesc);
        recCard=v.findViewById(R.id.recCard);
    }
}
