package com.example.photo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int ACTION_TAKE_PHOTO_S = 2;
    private static final int ACTION_TAKE_VIDEO = 3;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private static final String VIDEO_STORAGE_KEY = "viewvideo";
    private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";

    private String mCurrentPhotoPath;
    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private VideoView mVideoView;
    private Uri mVideoUri;

    /**
     * Called when the activity is first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView1);
        mVideoView = (VideoView) findViewById(R.id.videoView1);
        mImageBitmap = null;
        mVideoUri = null;

        Button picBtnB = (Button) findViewById(R.id.btnIntendB);
        picBtnB.setOnClickListener(mTakePicBOnClickListener);

        Button picBtnS = (Button) findViewById(R.id.btnIntendS);
        picBtnS.setOnClickListener(mTakePicSOnClickListener);

        Button vidBtnV = (Button) findViewById(R.id.btnIntendV);
        vidBtnV.setOnClickListener(mTakeVidOnClickListener);
    }

    Button.OnClickListener mTakePicBOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            dispatchTakePictureBIntent();
        }
    };

    Button.OnClickListener mTakePicSOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            dispatchTakePictureSIntent();
        }
    };

    Button.OnClickListener mTakeVidOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            dispatchTakeVideoIntent();
        }
    };

    // The Android way of delegating actions to other applications is to invoke an Intent that describes what you want done. Three components: the Intent itself, a call to start the external Activity, some code to handle the image data when focus returns to your activity.
    private void dispatchTakePictureBIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";
        File albumF = getAlbumDir();
        File f = new File(albumF, imageFileName);
        mCurrentPhotoPath = f.getAbsolutePath();
        Uri contentUri = FileProvider.getUriForFile(this, "com.example.photo.provider", f);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO_B);
    }

    private void dispatchTakePictureSIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "IMG_" + timeStamp + ".jpg";
//        File albumF = getAlbumDir();
//        File f = new File(albumF, imageFileName);
//        mCurrentPhotoPath = f.getAbsolutePath();
//        Uri contentUri = FileProvider.getUriForFile(this, "com.example.photo.provider", f);
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO_S);
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "VID_" + timeStamp + ".mp4";
//        File albumF = getAlbumDir();
//        File f = new File(albumF, imageFileName);
//        mVideoUri = f.getAbsolutePath();
//        Uri contentUri = FileProvider.getUriForFile(this, "com.example.photo.provider", f);
//        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
    }

    private File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraSample");
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    // The Android Camera application encodes the photo/video in the return Intent delivered to onActivityResult()
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            }
            case ACTION_TAKE_PHOTO_S: {
                if (resultCode == RESULT_OK) {
                    handleSmallCameraPhoto(data);
                }
                break;
            }
            case ACTION_TAKE_VIDEO: {
                if (resultCode == RESULT_OK) {
                    handleCameraVideo(data);
                }
                break;
            }
        }
    }

    // retrieves the image from path and displays it in an ImageView
    private void handleBigCameraPhoto() {
        if (mCurrentPhotoPath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            mImageView.setImageBitmap(bitmap);
            mVideoUri = null;
            mImageView.setVisibility(View.VISIBLE);
            mVideoView.setVisibility(View.INVISIBLE);
            mCurrentPhotoPath = null;
        }
    }

    // The photo is encoded as a small Bitmap in the extras, under the key "data"
    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        mImageBitmap = (Bitmap) extras.get("data");

        if (mImageBitmap != null) {
            mImageView.setImageBitmap(mImageBitmap);
            mVideoUri = null;
            mImageView.setVisibility(View.VISIBLE);
            mVideoView.setVisibility(View.INVISIBLE);
            mCurrentPhotoPath = null;
        }
    }

    private void handleCameraVideo(Intent intent) {
        mVideoUri = intent.getData();

        if (mVideoUri != null) {
            mVideoView.setVideoURI(mVideoUri);
            mCurrentPhotoPath = null;
            mImageView.setVisibility(View.INVISIBLE);
            mVideoView.setVisibility(View.VISIBLE);
            mVideoUri = null;

            // do not forget to start playing the video
            mVideoView.start();
        }
    }

    // to handle some display issue
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null));
        outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? ImageView.VISIBLE : ImageView.INVISIBLE);
        mVideoView.setVideoURI(mVideoUri);
        mVideoView.setVisibility(savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ? ImageView.VISIBLE : ImageView.INVISIBLE);
    }
}