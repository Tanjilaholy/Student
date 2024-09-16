package com.example.signuplogin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class calenderActivity extends AppCompatActivity {
    private CardView calender;
    private final int REQ=1;
    private EditText calendertitle;
    private Button uploadcalender;
    private Bitmap bitmap;
    private ImageView calenderimageview;
    private DatabaseReference reference;
    private StorageReference storageReference;
    String downloadUrl="";
    private ProgressDialog pd;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        pd=new ProgressDialog(this);

        reference= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        calender=findViewById(R.id.calender);
        calenderimageview=findViewById(R.id.calenderimageview);
        calendertitle=findViewById(R.id.calendertitle);
        uploadcalender=findViewById(R.id.uploadcalender);
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();

            }
        });
        uploadcalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calendertitle.getText().toString().isEmpty()){
                    calendertitle.setError("Empty");
                    calendertitle.requestFocus();
                } else if (bitmap==null) {
                    uploadData();

                } else  {
                    uploadImage();

                }
            }
        });
        }

    private void uploadImage() {
        pd.setMessage("Uploading...");
        pd.show();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimg=baos.toByteArray();
        final StorageReference filePath;
        filePath=storageReference.child("calender").child(finalimg+"jpg");
        final UploadTask uploadTask=filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(calenderActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl=String.valueOf(uri);
                                    uploadData();

                                }
                            });
                        }
                    });
                }else{
                    pd.dismiss();
                    Toast.makeText(calenderActivity.this,"something went wrong",Toast.LENGTH_SHORT);

                }


            }
        });
    }
    private void uploadData() {
        reference=reference.child("calender");
        final String uniqueKey=reference.push().getKey();
        String title=calendertitle.getText().toString();

        Calendar calDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MM-yy");
        String date=currentDate.format(calDate.getTime());

        Calendar calTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        String time=currentTime.format(calTime.getTime());

        calenderData calenderData=new calenderData(title,downloadUrl,date,time,uniqueKey);
        reference.child(uniqueKey).setValue(calenderData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(calenderActivity.this,"Calender Uploaded",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(calenderActivity.this,"something went wrong",Toast.LENGTH_SHORT).show();

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