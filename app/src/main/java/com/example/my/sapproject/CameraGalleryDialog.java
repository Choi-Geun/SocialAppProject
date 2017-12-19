package com.example.my.sapproject;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.security.Permission;
import java.util.ArrayList;


public class CameraGalleryDialog extends DialogFragment implements View.OnClickListener{

    Button camera, gallery;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cameragallery_dialog, container, false);
        camera = view.findViewById(R.id.camera_btn);
        gallery = view.findViewById(R.id.gallery_btn);

        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
        return view;
    }

    public static CameraGalleryDialog newInstance(){
        Bundle args = new Bundle();
        CameraGalleryDialog dialog = new CameraGalleryDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.camera_btn){
            dismiss();


        }else if(i == R.id.gallery_btn){
            dismiss();

        }
    }
}
