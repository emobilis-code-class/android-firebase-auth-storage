package com.nix.firebaseauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

public class ImageUploadActivity extends AppCompatActivity {
    FirebaseStorage storage;
    StorageReference storageRef;

    FirebaseDatabase database;
    DatabaseReference dbReference;
    private EditText edtname;
    private ImageButton imgAttachImage;
    private ImageView img_image;
    private Button btn_post;
    private Context context;
    private Uri image_uri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
         storage = FirebaseStorage.getInstance();

         database = FirebaseDatabase.getInstance();
         //get database
         dbReference = database.getReference().child("my_images");


        context = ImageUploadActivity.this;
        edtname = findViewById(R.id.edt_image_name);
        btn_post = findViewById(R.id.btn_post);
        img_image = findViewById(R.id.imageView);
        imgAttachImage = findViewById(R.id.img_button_select);

        imgAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this will open the gallery
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 464);
            }
        });

        /*
        * we need get the image within phone - gallery
        * we need to now upload to firebase - get the url
        * - upload the image name and the link returned
        * we post this image - name , and the link to firebase database
        * finish
        * */

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image_uri!=null){
                    UploadFile(image_uri);
                }else{
                    Toast.makeText(context,"Kindly select an image to upload",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void UploadFile(Uri uri){
        storageRef= storage.getReference().child("my_images"+ new Date().getTime());
        UploadTask  uploadTask = storageRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //check if there is any failure
                //we can log error
                Log.e("Upload Error ","=="+e.getMessage());
                Toast.makeText(context,"Upload Failed.Try again",Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //if success
                Toast.makeText(context,"Upload Success",Toast.LENGTH_LONG).show();
            }
        });
        //upload to firebase-
        //download uri
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    //post to storage - it
                    dbReference.push().setValue(downloadUri.toString());
                    finish();
                    //url
                    Log.d("File Location ","---"+downloadUri);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode ==RESULT_OK){
            if (requestCode==464){
                //get data
                img_image.setImageURI(data.getData());
                image_uri = data.getData();
                //create an object of upload task
                                //download url
            }
        }
    }
}