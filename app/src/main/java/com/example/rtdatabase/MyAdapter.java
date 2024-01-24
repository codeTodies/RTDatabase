package com.example.rtdatabase;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
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
        String link=dataList.get(position).getDataAudio();
        MediaPlayer mediaPlayer=new MediaPlayer();
        try
        {
            mediaPlayer.setDataSource(link);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.pause();
                }
            });
            mediaPlayer.prepare();
        }
        catch (IOException e){
            e.printStackTrace();
        }
//        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recTitle.setText(dataList.get(position).getDataTitle());
        holder.recDesc.setText(dataList.get(position).getDataArt());
        holder.recLang.setText(dataList.get(position).getDataAlbum());
        holder.recDuration.setText(millisecondsToString(mediaPlayer.getDuration()));
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent=new Intent(context,DetailActivity.class);
//                    intent.putExtra("Image",dataList.get(holder.getAdapterPosition()).getDataImage());
                    intent.putExtra("Audio",dataList.get(holder.getAdapterPosition()).getDataAudio());
                    intent.putExtra("Description",dataList.get(holder.getAdapterPosition()).getDataArt());
                    intent.putExtra("Title",dataList.get(holder.getAdapterPosition()).getDataTitle());
                    intent.putExtra("Language",dataList.get(holder.getAdapterPosition()).getDataAlbum());
                    intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());

//                    intent.putExtra("ImageN",dataList.get(holder.getAdapterPosition()+1).getDataImage());


//                    intent.putExtra("AudioN",dataList.get(holder.getAdapterPosition()+1).getDataAudio());
//                    intent.putExtra("DescriptionN",dataList.get(holder.getAdapterPosition()+1).getDataDesc());
//                    intent.putExtra("TitleN",dataList.get(holder.getAdapterPosition()+1).getDataTitle());
//                    intent.putExtra("LanguageN",dataList.get(holder.getAdapterPosition()+1).getDataLang());
//                    intent.putExtra("KeyN",dataList.get(holder.getAdapterPosition()+1).getKey());
                    context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public void searchDataLst(ArrayList<Data> searchList)
    {
        dataList=searchList;
        notifyDataSetChanged();
    }
    public String millisecondsToString(int time) {
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }
}
class MyViewHolder extends RecyclerView.ViewHolder{

//    ImageView recImage;
    TextView recTitle, recLang, recDesc,recDuration;
    LinearLayout recCard;
    public MyViewHolder(@NonNull View v)
    {
        super(v);
//        recImage=v.findViewById(R.id.recImage);
        recTitle=v.findViewById(R.id.recTitle);
        recLang=v.findViewById(R.id.recLang);
        recDesc=v.findViewById(R.id.recDesc);
        recCard=v.findViewById(R.id.recCard);
        recDuration=v.findViewById(R.id.recDuration);
    }
}
