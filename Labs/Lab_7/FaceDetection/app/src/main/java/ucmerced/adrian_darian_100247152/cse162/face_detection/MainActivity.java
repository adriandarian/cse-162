package ucmerced.adrian_darian_100247152.cse162.face_detection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView iw;
    Canvas canvas;
    Bitmap mutableBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure the detection options
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions
                        .Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        // Convert the image to the appropriate format using InputImage object
        Bitmap bm = getBitmapFromAssets("faces.png");
        InputImage image = InputImage.fromBitmap(bm, 0);

        // Get an instance of FaceDetector
        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);

        // Use Canvas to draw the detection box
        mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(mutableBitmap);

        // Display the image
        iw = (ImageView) findViewById(R.id.image_view);
        iw.setImageBitmap(mutableBitmap);

        // Process the image
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        // Task completed successfully
                                        for (Face face : faces) {
                                            // Get Bounding box
                                            Rect bounds = face.getBoundingBox();

                                            // Paint a red box around face
                                            Paint paint = new Paint();
                                            paint.setAntiAlias(true);
                                            paint.setColor(Color.RED);
                                            paint.setStyle(Paint.Style.STROKE);
                                            paint.setStrokeWidth(8);

                                            // Actually draw box
                                            canvas.drawRect(bounds, paint);

                                            // Reprint image
                                            iw = (ImageView) findViewById(R.id.image_view);
                                            iw.setImageBitmap(mutableBitmap);
                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });

    }

    private Bitmap getBitmapFromAssets(String fileName) {
        AssetManager am = getAssets();
        InputStream is = null;
        try {
            is = am.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }
}