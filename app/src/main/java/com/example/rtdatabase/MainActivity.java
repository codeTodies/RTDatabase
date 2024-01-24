package com.example.rtdatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    RecyclerView recyclerView;
    List<Data> dataList;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    SearchView searchView;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab=findViewById(R.id.fab);
        recyclerView=findViewById(R.id.recyclerView);
        searchView=findViewById(R.id.search);
        searchView.clearFocus();
        GridLayoutManager gridLayoutManager= new GridLayoutManager(MainActivity.this,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog=builder.create();
        dialog.show();

        dataList=new ArrayList<>();
        myAdapter=new MyAdapter(MainActivity.this,dataList);
        recyclerView.setAdapter(myAdapter);
        databaseReference= FirebaseDatabase.getInstance().getReference("dtb");
        dialog.show();

        //lấy dữ liệu từ database
        valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for(DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Data data=itemSnapshot.getValue(Data.class);
                    data.setKey(itemSnapshot.getKey());
                    dataList.add(data);
                }
                myAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchList(newText);
                    return true;
                }
            });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,UploadActivity.class);
                startActivity(intent);
            }
        });
    }
    public void searchList(String text)
    {
        ArrayList<Data> searchList=new ArrayList<>();
        for(Data data:dataList)
        {
            if(data.getDataTitle().toLowerCase().contains(text.toLowerCase()))
            {
                searchList.add(data);
            }
        }
        myAdapter.searchDataLst(searchList);
    }

}