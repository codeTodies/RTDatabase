package com.example.rtdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class DetailActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailLang;
    Button detailPLay;
    ImageView detailImage;
    FloatingActionButton deleteBtn,editBtn;
    String key="";
    String imageURL="";
    String audioURL="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detailDesc=findViewById(R.id.detailDesc);
        detailTitle=findViewById(R.id.detailTitle);
        detailImage=findViewById(R.id.detailImage);
        detailLang=findViewById(R.id.detailLang);
        deleteBtn=findViewById(R.id.deleteBtn);
        editBtn=findViewById(R.id.EditBtn);
        detailPLay=findViewById(R.id.detailPlay);
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

        detailPLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}