package com.mv.jura.firebase_example;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

public class PostActivity extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView photoView;
    private VideoView videoView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private int photoVideoMode = 0; //1-photo, 2-video
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.photoView = (ImageView)this.findViewById(R.id.photoView);
        this.videoView = (VideoView) this.findViewById(R.id.videoView);
        photoView.setVisibility(View.INVISIBLE);
        videoView.setVisibility(View.INVISIBLE);
        Button photoButton = (Button) this.findViewById(R.id.photoButton);
        //https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity
        photoButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                photoVideoMode = 0;
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    photoVideoMode = 1;
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
        Button videoButton = (Button) this.findViewById(R.id.videoButton);
        videoButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                photoVideoMode = 0;
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    photoVideoMode = 2;
                    Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
        Button shareButton = (Button) this.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //tu sa postne video
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if(photoVideoMode == 1) {
                photoView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photoView.setImageBitmap(photo);
            }else if (photoVideoMode == 2){
                //https://stackoverflow.com/questions/41531736/loading-video-from-gallery-to-videoview-through-a-button/41663247
                photoView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);
                Uri uri = data.getData();
                try{
                    videoView.setVideoURI(uri);
                    videoView.start();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
                photoView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
