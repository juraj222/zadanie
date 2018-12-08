package com.mv.jura.firebase_example;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.mv.jura.firebase_example.adapters.PostType;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PostActivity extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1880;
    private static final int STORAGE_REQUEST = 1889;
    private ImageView photoView;
    private VideoView videoView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int MY_STORAGE_PERMISSION_CODE = 101;
    private int photoVideoMode = 0; //1-photo, 2-video
    Bitmap photo;
    Uri videoUri;
    private FirebaseFirestore db;
    String userId;
    Item userProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        userId = getIntent().getSerializableExtra("userId").toString();
        userProfile = (Item) getIntent().getSerializableExtra("userProfile");
        initDB();
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
        Button galleryButton = (Button) this.findViewById(R.id.gallery);
        galleryButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                photoVideoMode = 0;
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/* video/*");
                    //Intent cameraIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GALLERY_REQUEST);
                }
            }
        });
        Button shareButton = (Button) this.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_STORAGE_PERMISSION_CODE);
                }
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_STORAGE_PERMISSION_CODE);
                }
                if(photoVideoMode == 1) {

                    sendPhotoToServer();
                    onBackPressed();

                }else if (photoVideoMode == 2){
                    sendVideoToServer();
                    onBackPressed();
                }
            }
        });
    }
    private void initDB(){
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    private void sendVideoToServer() {
        new SendFileToServer().execute(getPath(videoUri));
    }

    private void sendPhotoToServer() {
        new SendFileToServer().execute(photo);
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
                this.photo = photo;
                photoView.setImageBitmap(photo);
            }else if (photoVideoMode == 2){
                //https://stackoverflow.com/questions/41531736/loading-video-from-gallery-to-videoview-through-a-button/41663247
                photoView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);
                Uri uri = data.getData();
                this.videoUri = uri;
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
        }else if(requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            String mimeType = getMimeType(uri);
            if(mimeType != null && mimeType.startsWith("image")){
                photoView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                photoView.setImageURI(uri);
                photoVideoMode = 1;
            }else if(mimeType != null && mimeType.startsWith("video")){
                photoView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(uri);
                videoView.start();
                photoVideoMode = 2;
            }else{
                photoView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = this.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //https://stackoverflow.com/questions/11164398/android-upload-video-to-remote-server-using-http-multipart-form-data
    private String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        cursor.moveToFirst();
        String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
        int fileSize = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
        long duration = TimeUnit.MILLISECONDS.toSeconds(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
        //some extra potentially useful data to help with filtering if necessary
        System.out.println("size: " + fileSize);
        System.out.println("path: " + filePath);
        System.out.println("duration: " + duration);

        return filePath;
    }

    private class SendFileToServer extends AsyncTask<Object,Integer,String> {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected String doInBackground(Object... objects) {
            //createpost(PostType.image, "", "", "pokus_meno", Calendar.getInstance(), "user2");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost("http://mobv.mcomputing.eu/upload/index.php");
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE); //MultipartEntityBuilder je aktualna, ale nefunguje mi s httppost
            try {
                if(objects[0] instanceof Bitmap) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ((Bitmap) objects[0]).compress(Bitmap.CompressFormat.JPEG, 75, bos);
                    byte[] data = bos.toByteArray();
                    ByteArrayBody bab = new ByteArrayBody(data, "photo.jpg");
                    reqEntity.addPart("upfile", bab);
                }else{
                    FileBody filebodyVideo = new FileBody(new File((String) objects[0]));
                    reqEntity.addPart("upfile", filebodyVideo);
                }
                postRequest.setEntity(reqEntity);
                HttpResponse response = httpClient.execute(postRequest);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                JSONObject responseMessage = new JSONObject(reader.readLine());
                if (responseMessage.getString("status").equals("ok")) {
                    return responseMessage.getString("message");
                } else {
                    System.out.println(responseMessage.getString("message")); //error message
                    return null;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                //Log.v("Exception in Image", ""+e);
                //reqEntity.addPart("upfile", new StringBody(""));
            }

            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            //tu sa da nieco urobit ak sa dokonci upload fotky, result je nazov suboru
            if(result != null) {
                String filePath = "http://mobv.mcomputing.eu/upload/v/" + result;

                if(photoVideoMode == 1) {
                    createpost(PostType.image, "", filePath, userProfile.name, Calendar.getInstance(), userId);
                }else if(photoVideoMode == 2){
                    createpost(PostType.video, filePath, "", userProfile.name, Calendar.getInstance(), userId);
                }
            }

        }
        private void createpost(PostType type, String videourl, String imageurl, String username, Calendar date, String userid){
            Map<String, Object> newItem = new HashMap<>();
            newItem.put("type", type.toString());
            newItem.put("videourl", videourl);
            newItem.put("imageurl", imageurl);
            newItem.put("username", username);
            newItem.put("userid", userid);
            newItem.put("date",  FieldValue.serverTimestamp());
            db.collection("posts").document().set(newItem)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //tu by sa mala potom aktualizovat appka o novy prispevok
                            System.out.println("upload success");
                            UpdateRegistration(Integer.parseInt(userProfile.getPostCount()) + 1, userId);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("error");
                        }
                    });
        }
        private void UpdateRegistration(int numberOfPosts, String userId) {
            DocumentReference contact = db.collection("users").document(userId);
            contact.update("numberOfPosts", numberOfPosts)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //userProfile.setPostCount(((int)(Integer.parseInt(userProfile.getPostCount()) + 1)).toString());
                            //onBackPressed();
                        }
                    });
        }

    }

}
