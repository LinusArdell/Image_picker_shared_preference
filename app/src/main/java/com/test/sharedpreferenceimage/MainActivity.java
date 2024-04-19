package com.test.sharedpreferenceimage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    ImageButton btnCapture;
    ImageView ivImage;
    Button btnPreview, btnSave;
    private Uri uri;

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCapture = findViewById(R.id.btn_capture);
        ivImage = findViewById(R.id.upload_image);
        btnPreview = findViewById(R.id.btn_preview);
        btnSave = findViewById(R.id.btnSave);

        ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        ivImage.setImageBitmap(imageBitmap);
                        uri = getImageUri(this, imageBitmap);
                    } else {
                        Toast.makeText(MainActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri =data.getData();
                            ivImage.setImageURI(uri);
                        } else {
                            Toast.makeText(MainActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        btnCapture.setOnClickListener(View -> dispatchTakePictureIntent(takePictureLauncher));
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uri != null) {
                    saveImageUriToSharedPreferences(uri);
                    Toast.makeText(MainActivity.this, "Image saved to SharedPreferences", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Preview.class);
                startActivity(i);
            }
        });

    }

    private void dispatchTakePictureIntent(ActivityResultLauncher<Intent> takePictureLauncher) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageUriToSharedPreferences(Uri imageUri) {
        SharedPreferences sharedPreferences = getSharedPreferences("image_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Membuat kunci unik untuk setiap gambar dengan menggunakan timestamp
        String uniqueKey = "imageUri_" + System.currentTimeMillis();
        editor.putString(uniqueKey, imageUri.toString());
        editor.apply();
    }

    private Uri getImageUriFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("image_data", MODE_PRIVATE);
        String uriString = sharedPreferences.getString("imageUri", null);
        if (uriString != null) {
            return Uri.parse(uriString);
        } else {
            return null;
        }
    }

}