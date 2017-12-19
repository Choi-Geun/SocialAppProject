package com.example.my.sapproject;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class AddPictureAcitivity extends AppCompatActivity implements View.OnClickListener {

    ImageView photo;
    EditText text;
    Button confirm,cancle;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabase;
    StorageReference mStorage;
    StorageReference storageRef, imageRef;
    Uri selectedImage;
    String key;
    Random random = new Random();
    int ran;
    byte[] databyte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpicture);

        photo = findViewById(R.id.addphoto);
        text = findViewById(R.id.addphototext);
        confirm = findViewById(R.id.addphoto_confirm);
        cancle = findViewById(R.id.addphoto_cancle);

        confirm.setOnClickListener(this);
        cancle.setOnClickListener(this);
        photo.setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();

        key = mDatabase.push().getKey();

        random.setSeed(System.currentTimeMillis());
        ran = random.nextInt(10000);


    }

    private void selectImage() {
        final CharSequence[] items = { "카메라", "갤러리",
                "취소" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("카메라")) {

                    PermissionListener permissionlistener = new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        }
                        @Override
                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        }
                    };

                    TedPermission.with(getApplicationContext())
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                            .setPermissions(android.Manifest.permission.CAMERA)
                            .check();

                } else if (items[item].equals("갤러리")) {

                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (items[item].equals("취소")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    //이미지 회전을 위한 matrix
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);

                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap,imageBitmap.getWidth(),imageBitmap.getHeight(),true);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
                    photo.setImageBitmap(rotatedBitmap);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    databyte = baos.toByteArray();

                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    selectedImage = imageReturnedIntent.getData();
                    photo.setImageURI(selectedImage);

                    InputStream Stream = null;
                    try {
                        Stream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    try {
                        databyte  = getBytes(Stream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void uploadDataImage(){
        StorageReference Filepath = mStorage.child("images").child(String.valueOf(ran));
        UploadTask uploadTask = Filepath.putBytes(databyte);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        User userdb = new User(user.getUid(), String.valueOf(ran), text.getText().toString());
        mDatabase.child("user").child(user.getUid()).child(key).setValue(userdb);

    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.addphoto){
            selectImage();
        }
        else if(view.getId() == R.id.addphoto_confirm){
            uploadDataImage();
        }
        else if(view.getId() == R.id.addphoto_cancle){
            finish();
        }
    }
}
