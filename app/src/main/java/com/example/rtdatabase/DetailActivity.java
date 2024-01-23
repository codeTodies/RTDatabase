package com.example.rtdatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class DetailActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailLang;
    Button detailPLay;
    ImageView detailImage;
    TextView tvDuration,tvTime;

    ImageView nextBtn, previousBtn;
    SeekBar seekBarTime;
    SeekBar seekBarVolume;
    FloatingActionButton deleteBtn,editBtn;
    String key="";
    String imageURL="";
    String audioURL="";
    Handler handler=new Handler();
    private MutableLiveData<Integer> currentPosition = new MutableLiveData<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        tvTime = findViewById(R.id.tvTime);
        detailDesc=findViewById(R.id.detailDesc);
        detailTitle=findViewById(R.id.detailTitle);
        detailImage=findViewById(R.id.detailImage);
        detailLang=findViewById(R.id.detailLang);
        deleteBtn=findViewById(R.id.deleteBtn);
        editBtn=findViewById(R.id.EditBtn);
        detailPLay=findViewById(R.id.detailPlay);
        tvDuration = findViewById(R.id.tvDuration);
        seekBarTime = findViewById(R.id.seekBarTime);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            detailLang.setText(bundle.getString("Language"));
            key=bundle.getString("Key");
            imageURL=bundle.getString("Image");
            audioURL=bundle.getString("Audio");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }

        MediaPlayer mediaPlayer=new MediaPlayer();
        try
        {
            mediaPlayer.setDataSource(audioURL);
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
        detailPLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.detailPlay) {
                    if (mediaPlayer.isPlaying()) {
                        // dang nghe nhac
                        mediaPlayer.pause();
                        detailPLay.setBackgroundResource(R.drawable.ic_play);
                    } else {
                        // dang tam dung
                        mediaPlayer.start();
                        detailPLay.setBackgroundResource(R.drawable.ic_pause);
                    }
                }
            }

        });
        mediaPlayer.setLooping(true); // lap lai
        mediaPlayer.seekTo(0);
        mediaPlayer.setVolume(0.5f, 0.5f); // am luong
        String duration = millisecondsToString(mediaPlayer.getDuration());
        tvDuration.setText(duration);
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());
        // thanh am luong
        seekBarVolume.setProgress(50);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                // Handle progress change
                // You can use 'progress' to get the current progress value
                // tang giam am luong
                float volume = (float) progress / 100f;
                mediaPlayer.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Handle the start of tracking touch
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Handle the stop of tracking touch
            }
        });
        // thanh thoi gian bai hat
        seekBarTime.setMax(mediaPlayer.getDuration());
        Handler handler = new Handler();
        // Update seek bar and time TextView periodically
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBarTime.setProgress(currentPosition);
                    tvTime.setText(millisecondsToString(currentPosition));
                }
                handler.postDelayed(this, 1000); // Update every 1 second
            }
        }, 0);
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    tvTime.setText(millisecondsToString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Handle the start of tracking touch
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Handle the stop of tracking touch
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference= FirebaseDatabase.getInstance().getReference("dtb");
                FirebaseStorage storage=FirebaseStorage.getInstance();
                StorageReference storageReference=storage.getReferenceFromUrl(imageURL);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(DetailActivity.this,"Delete Success",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                });
                StorageReference storageReferenceAu=storage.getReferenceFromUrl(audioURL);
                storageReferenceAu.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                });
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DetailActivity.this,UpdateActivity.class)
                        .putExtra("Title",detailTitle.getText().toString())
                        .putExtra("Description",detailDesc.getText().toString())
                        .putExtra("Language",detailLang.getText().toString())
                        .putExtra("Image",imageURL)
                        .putExtra("Audio",audioURL)
                        .putExtra("Key",key);
                startActivity(intent);
            }
        });
    }

    public String millisecondsToString(int time) {
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    private void playNextSong() {

    }

    private void playPreviousSong() {

    }
}