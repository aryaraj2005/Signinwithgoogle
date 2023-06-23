package com.example.usersignup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    EditText roll, name, course, contact;
    Uri filepath;
    ImageView img;
    Button brows, signup;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.userimg);


        brows = findViewById(R.id.browsetext);
        signup = (Button) findViewById(R.id.signbtn);
        // browser btn is used for when we click over the brow then it fetch the img from img view and show it
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dexter is used for permission purpose
                Dexter.withActivity(MainActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Choose the image"), 1);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                // when once app get closed then for continue ask for permission we used token
                                permissionToken.continuePermissionRequest();
                            }
                            // here we have to check
                        }).check();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimagefirebase();
            }
        });
    }




    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode== 1 && resultCode==RESULT_OK){
            filepath=data.getData();
            try {
             // image is fetch when click over browser through uri
                InputStream  inputStream =getContentResolver().openInputStream(filepath);
                 bitmap = BitmapFactory.decodeStream(inputStream);
                img.setImageBitmap(bitmap);
            }catch (Exception e){

            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadimagefirebase() {
        ProgressDialog dialog =new ProgressDialog(this);
        dialog.setTitle("file is uploaded");
        dialog.show();
        // here gor everything but image not get for that we have excess it from firabse storage
        name=findViewById(R.id.nametext);
        course=findViewById(R.id.coursetext);
        contact =findViewById(R.id.contacttext);
        roll =findViewById(R.id.rolltext);

        FirebaseStorage storage =FirebaseStorage.getInstance();
        StorageReference uploader = storage.getReference("image1" + new Random().nextInt(50));
        uploader.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                               // once completed then dailog get dismiss
                               dialog.dismiss();
                              FirebaseDatabase db = FirebaseDatabase.getInstance();
                              // if here we direct pass the root node then directly get
                              DatabaseReference root = db.getReference("users");

                              mydata obj = new mydata(name.getText().toString() , course.getText().toString() ,contact.getText().toString() , uri.toString());
                              // creating node
                               root.child(roll.getText().toString()).setValue(obj);
                               // once node is created the all data make empty
                               name.setText("");
                               course.setText("");
                               contact.setText("");
                               roll.setText("");
                               img.setImageResource(R.drawable.users);
                               Toast.makeText(getApplicationContext() , "uploaded " ,Toast.LENGTH_LONG).show();

                           }
                       });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {


                    // % of uploaded
                        float percent = (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        dialog.setMessage("uploaded" + (int)percent + "%");
                    }
                });

    }

}