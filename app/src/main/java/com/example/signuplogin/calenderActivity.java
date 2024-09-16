package com.example.signuplogin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.annotations.Nullable;

import java.io.IOException;

public class calenderActivity extends AppCompatActivity {
    private CardView calender;
    private final int REQ=1;
    private Bitmap bitmap;
    private ImageView calenderimageview;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        calender=findViewById(R.id.calender);
        calenderimageview=findViewById(R.id.calenderimageview);
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();

            }
        });
        }

    private void openGallery() {
        Intent pickimage=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickimage,REQ);
    }

   @Override
   public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

       super.onActivityResult(requestCode, resultCode, data);
       if(requestCode==REQ && resultCode == RESULT_OK){
           Uri uri = data.getData();
           try {
               bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
           } catch (IOException e) {
               e.printStackTrace();
           }
           calenderimageview.setImageBitmap(bitmap);
       }
   }
}