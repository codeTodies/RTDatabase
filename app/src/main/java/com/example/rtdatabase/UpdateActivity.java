package com.example.rtdatabase;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {

    ImageView updateImage;
    Button updateBtn;
    Button updateFile;
    EditText updateDesc, updateTitle, updateLang;
    String title,desc,lang;
    String imageURL, audioURL;
    Uri uri,uriAu;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String key,oldImageURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        updateBtn=findViewById(R.id.updateButton);
        updateDesc=findViewById(R.id.updateDesc);
        updateImage=findViewById(R.id.updateImage);
        updateLang=findViewById(R.id.updateLang);
        updateTitle=findViewById(R.id.updateTopic);
        updateFile=findViewById(R.id.btnUpdateFile);
        ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode()== Activity.RESULT_OK)
                        {
                            Intent data=o.getData();
                            uri=data.getData();
                            updateImage.setImageURI(uri);
                        }
                        else {
                            Toast.makeText(UpdateActivity.this,"No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        ActivityResultLauncher<Intent> activityResultLauncherAu=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode()== Activity.RESULT_OK)
                        {
                            Intent data=o.getData();
                            uriAu=data.getData();
                        }
                        else {
                            Toast.makeText(UpdateActivity.this,"No audio selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateImage);
            updateTitle.setText(bundle.getString("Title"));
            updateLang.setText(bundle.getString("Language"));
            updateDesc.setText(bundle.getString("Description"));
            key=bundle.getString("Key");
            oldImageURL=bundle.getString("Image");
            audioURL=bundle.getString("Audio");
        }
        databaseReference= FirebaseDatabase.getInstance().getReference("dtb").child(key);
        updateFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photo=new Intent(Intent.ACTION_GET_CONTENT);
                photo.setType("audio/*");
                activityResultLauncherAu.launch(photo);
            }
        });
        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photo=new Intent(Intent.ACTION_GET_CONTENT);
                photo.setType("image/*");
                activityResultLauncher.launch(photo);
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                Intent intent=new Intent(UpdateActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }


    public void saveData()
    {
        storageReference= FirebaseStorage.getInstance().getReference("Android Images").child(uri.getLastPathSegment());
        AlertDialog.Builder builder=new AlertDialog.Builder(UpdateActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog= builder.create();
        dialog.show();
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageURL = uri.toString();
                        UpdateData();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });



        StorageReference storageReferenceAu= FirebaseStorage.getInstance().getReference().child("Audio")
                .child(uriAu.getLastPathSegment());
        storageReferenceAu.putFile(uriAu).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        audioURL = uri.toString();
                        UpdateData();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });

    }
    public void UpdateData()
    {
        String title=updateTitle.getText().toString();
        String desc=updateDesc.getText().toString();
        String lang =updateLang.getText().toString();

        Data data= new Data(title,desc,lang,imageURL,audioURL);
        
        databaseReference.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    StorageReference ref=FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                    ref.delete();
                    Toast.makeText(UpdateActivity.this,"Updated",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}