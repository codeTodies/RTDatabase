package com.example.rtdatabase;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class UploadActivity extends AppCompatActivity {

//    ImageView uploadImage;
    Button saveButton,uploadFile;
    TextView file;
    EditText uploadTopic, uploadDesc, uploadLang;
    String imageURL,audioURL;
    Uri uri,uriAu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
//        uploadImage=findViewById(R.id.uploadImage);
        uploadDesc=findViewById(R.id.uploadDesc);
        uploadLang=findViewById(R.id.uploadLang);
        uploadTopic=findViewById(R.id.uploadTopic);
        saveButton=findViewById(R.id.saveButton);
        uploadFile=findViewById(R.id.btnUploadFile);
        file=findViewById(R.id.file);
//        ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult o) {
//                        if(o.getResultCode()== Activity.RESULT_OK){
//                            Intent data=o.getData();
//                            uri=data.getData();
//                            uploadImage.setImageURI(uri);
//                        }
//                        else {
//                            Toast.makeText(UploadActivity.this,"No Image selected",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//        );
        ActivityResultLauncher<Intent> activityResultLauncherAu=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode()== Activity.RESULT_OK){
                            Intent data=o.getData();
                            uriAu=data.getData();
                            file.setText("b");
                        }
                        else {
                            Toast.makeText(UploadActivity.this,"No File selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
//        uploadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent photopicker=new Intent(Intent.ACTION_GET_CONTENT);
//                photopicker.setType("image/*");
//                activityResultLauncher.launch(photopicker);
//            }
//        });
        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photopicker=new Intent(Intent.ACTION_GET_CONTENT);
                photopicker.setType("audio/*");
                activityResultLauncherAu.launch(photopicker);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }
    public void saveData(){
//        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Android Images")
//                .child(uri.getLastPathSegment());
        AlertDialog.Builder builder=new AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog= builder.create();
        dialog.show();
//        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uria) {
//                        imageURL = uria.toString();
//                        dialog.dismiss();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        dialog.dismiss();
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                dialog.dismiss();
//            }
//        });

        StorageReference storageReferenceAu= FirebaseStorage.getInstance().getReference().child("Audio")
                .child(uriAu.getLastPathSegment());
        storageReferenceAu.putFile(uriAu).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uris) {
                        audioURL = uris.toString();
                        dialog.dismiss();
                        UploadData();
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
    public void UploadData()
    {
        String title=uploadTopic.getText().toString();
        String desc=uploadDesc.getText().toString();
        String lang =uploadLang.getText().toString();
        Data data=new Data(title,desc,lang,audioURL);

        LocalDateTime now=LocalDateTime.now();
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd MM yyyy HH:mm:ss");
        String dateTime=now.format(formatter);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("dtb");
        databaseReference.child(dateTime).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful())
            {
                Toast.makeText(UploadActivity.this,"Saved",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(UploadActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
        }
    });
    }
}