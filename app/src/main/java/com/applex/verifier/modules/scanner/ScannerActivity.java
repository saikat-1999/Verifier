package com.applex.verifier.modules.scanner;

import static com.applex.verifier.utilities.CommonUtils.Constants.READ_WRITE_CAMERA_PERMISSIONS;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.applex.verifier.R;
import com.applex.verifier.modules.main.ui.MainActivity;
import com.applex.verifier.utilities.CommonUtils.PermissionsUtils;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.io.IOException;
import java.util.Objects;

import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private Bitmap bitmap;
    private boolean mPermissionGranted = false;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int REQUEST_PERMISSIONS_CODE = 124;
    private static final int CAMERA_REQUEST_CODE = 200;
    private String[] cameraPermission;
    private boolean flashState = false;
    //private IntroPref introPref;
    private ZXingScannerView scannerView;
    private ViewFinderView viewFinderView;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        mPermissionGranted = PermissionsUtils.getInstance().checkRuntimePermissions(this, READ_WRITE_CAMERA_PERMISSIONS);
        scannerView = findViewById(R.id.zxing_scanner);
        //introPref = new IntroPref(ScannerActivity.this);
        viewFinderView = new ViewFinderView(ScannerActivity.this);
        viewFinderView.setBorderColor(getResources().getColor(R.color.colorPrimary));

//        ImageView scan_gallery = findViewById(R.id.scan_gallery);
//        scan_gallery.setOnClickListener(v -> {
//            if (SystemClock.elapsedRealtime() - mLastClickTime < 1500){
//                return;
//            }
//            mLastClickTime = SystemClock.elapsedRealtime();
//
//            if (!checkCameraPermission()) {
//                requestCameraPermission();
//            }
//            else {
//                startAddingImages();
//            }
//        });

        ImageView scan_flash = findViewById(R.id.scan_flash);
        scan_flash.setOnClickListener(v -> {
            if(flashState) {
                scannerView.setFlash(false);
                flashState = false;
                scan_flash.setImageResource(R.drawable.ic_baseline_flash_off_24);
            }
            else {
                scannerView.setFlash(true);
                flashState = true;
                scan_flash.setImageResource(R.drawable.ic_baseline_flash_on_24);
            }
        });

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        //////////////SHARED CONTENT////////////////////
        if(Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri image_uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (image_uri != null) {
                    CropImage.activity(image_uri)
                            .setActivityTitle("Crop QR")
                            .setCropMenuCropButtonTitle("Set")
                            .setAllowRotation(TRUE)
                            .setAspectRatio(1,1)
                            .setAllowCounterRotation(TRUE)
                            .setAllowFlipping(TRUE)
                            .setAutoZoomEnabled(TRUE)
                            .setMultiTouchEnabled(FALSE)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                }
            }
        }
        //////////////SHARED CONTENT////////////////////
    }

    @Override
    public void handleResult(Result result) {
        Intent intent = new Intent(ScannerActivity.this, MainActivity.class);
        intent.putExtra("text",result.getText());
        intent.putExtra("selection", "2");
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    /////gallery
    private void startAddingImages() {
        mPermissionGranted = PermissionsUtils.getInstance().checkRuntimePermissions(this,
                READ_WRITE_CAMERA_PERMISSIONS);
        if (!mPermissionGranted) {
            getRuntimePermissions();
            return;
        }
        selectImages();
    }

    private void selectImages() {
         Matisse.from(this)
                    .choose(MimeType.ofImage(), true)
                    .countable(true)
                    .maxSelectable(25)
                    .thumbnailScale(1.0f)
                    .theme(R.style.Matisse_Zhihu)
                    .imageEngine(new PicassoEngine())
                    .forResult(INTENT_REQUEST_GET_IMAGES);
    }
    /////gallery

    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                READ_WRITE_CAMERA_PERMISSIONS,
                REQUEST_PERMISSIONS_CODE);

        mPermissionGranted = PermissionsUtils.getInstance().checkRuntimePermissions(this,
                READ_WRITE_CAMERA_PERMISSIONS);
    }

//    private void requestCameraPermission(){
//        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
//    }
//
//    private boolean checkCameraPermission(){
//        boolean result= ContextCompat.checkSelfPermission(this,
//                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
//        boolean result1= ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE )== (PackageManager.PERMISSION_GRANTED);
//        return result && result1;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        if(requestCode == INTENT_REQUEST_GET_IMAGES) {
            CropImage.activity(Matisse.obtainResult(data).get(0))
                    .setActivityTitle("Crop QR")
                    .setCropMenuCropButtonTitle("Set")
                    .setAllowRotation(TRUE)
                    .setAllowCounterRotation(TRUE)
                    .setAllowFlipping(TRUE)
                    .setAspectRatio(1,1)
                    .setAutoZoomEnabled(TRUE)
                    .setMultiTouchEnabled(FALSE)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(result == null) {
                ScannerActivity.super.onBackPressed();
            }
            Uri resultUri = Objects.requireNonNull(result).getUri();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int[] intArray = new int[bitmap.getWidth()*bitmap.getHeight()];
            //copy pixel data from the Bitmap into the 'intArray' array
            bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

            LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Reader reader = new MultiFormatReader();
            Result resultQR = null;
            try {
                resultQR = reader.decode(bitmap);
            }
            catch (NotFoundException | ChecksumException | FormatException e) {
                e.printStackTrace();
            }

            if(resultQR != null) {
                Intent intent = new Intent(ScannerActivity.this, MainActivity.class);
                intent.putExtra("text",resultQR.getText());
                intent.putExtra("selection", "2");
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(getApplicationContext(), "QR/Bar unrecognisable", Toast.LENGTH_SHORT).show();
            }
        }
    }
}