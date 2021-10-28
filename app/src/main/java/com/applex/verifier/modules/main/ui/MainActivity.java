package com.applex.verifier.modules.main.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applex.verifier.R;
import com.applex.verifier.modules.scanner.ScannerActivity;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button buttonqr;
    private String data="";
    private long mLastClickTime = 0;
    private TextView qr_text;

    public static Uri resultUri;
    private String[] cameraPermission;
    private static final int CAMERA_REQUEST_CODE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);

        buttonqr = findViewById(R.id.qr);
        qr_text = findViewById(R.id.scanned_text);
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        buttonqr.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

//            Animation bounce = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
//            btnScan.startAnimation(bounce);
            if(checkCameraPermission()){
                startActivity(new Intent(MainActivity.this, ScannerActivity.class));
            }
            else {
                requestCameraPermission();
            }
        });

        Intent i = getIntent();
        if(i.getStringExtra("text") != null){
            qr_text.setText(i.getStringExtra("text"));
        }
    }
    private boolean checkCameraPermission() {
        boolean result= ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE )== (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, cameraPermission, CAMERA_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {

            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    //File file = new File(Environment.getExternalStorageDirectory() + "/Snaplingo/.ocr", "ocr_database.json");
                    //if (!file.exists()) {
//                        new MoveToFolders().execute();
                    Toast.makeText(this, "camera accepted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "camera denied", Toast.LENGTH_SHORT).show();
//                    if (introPref.isFirstTimeLaunchAfterUpdate() || !file.exists()) {
//                        new MoveToFolders().execute();
//                        Toast.makeText(this, "hellooo", Toast.LENGTH_SHORT).show();
//                    }
                }
            }
                else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }
    //////////////////PERMISSION REQUESTS/////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = Objects.requireNonNull(result).getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                TextRecognizer recognizer = new TextRecognizer.Builder(MainActivity.this).build();

                if (!recognizer.isOperational()) {
                    Toast.makeText(getApplicationContext(), "Text not recognisable", Toast.LENGTH_SHORT).show();
                }
                else {
                    Frame frame = new Frame.Builder().setBitmap(Objects.requireNonNull(bitmap)).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        if (i != items.size() - 1) {
                            sb.append("\n");
                        }
                    }

                    Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                    intent.putExtra("Text",sb.toString().trim());
                    intent.putExtra("selection", "1");
                    startActivity(intent);
                }
            }
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(MainActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }
}