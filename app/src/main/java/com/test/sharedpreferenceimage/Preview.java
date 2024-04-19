package com.test.sharedpreferenceimage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Preview extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Uri> imageList;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        imageList = getAllImageUrisFromSharedPreferences();
        imageAdapter = new ImageAdapter(this, imageList);
        recyclerView.setAdapter(imageAdapter);
    }

    private List<Uri> getAllImageUrisFromSharedPreferences() {
        List<Uri> uriList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("image_data", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String uriString = (String) entry.getValue();
            Uri uri = Uri.parse(uriString);
            uriList.add(uri);
        }
        return uriList;
    }
}