package ng.com.obkm.exquisitor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class PhotoItemActivity extends AppCompatActivity {

    private static final String TAG = "singlePhoto";

    private String path = "";
    private PhotoItem mItem;

    // presets for rgb conversion
    private static final int RESULTS_TO_SHOW = 3;

    // activity elements
    private ImageView selectedImage;
    private TextView label1;
    private TextView label2;
    private TextView label3;
    private TextView confidence1;
    private TextView confidence2;
    private TextView confidence3;

    private List<String> labelList;

    private DecimalFormat df = new DecimalFormat();

    // priority queue that will hold the top results from the CNN
    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_photo);

        try {
            labelList = loadLabelList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // labels that hold top three results of CNN
        label1 = (TextView) findViewById(R.id.label1);
        label2 = (TextView) findViewById(R.id.label2);
        label3 = (TextView) findViewById(R.id.label3);
        // displays the probabilities of top labels
        confidence1 = (TextView) findViewById(R.id.confidence1);
        confidence2 = (TextView) findViewById(R.id.confidence2);
        confidence3 = (TextView) findViewById(R.id.confidence3);
        // initialize imageView that displays selected image to the user
        selectedImage = (ImageView) findViewById(R.id.selectedImage);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        Log.i(TAG, "Path received from intent: " + path);

        mItem = new PhotoItem(path);
        Log.i(TAG, "Path: " + path);

        final ImageView myImage = (ImageView) findViewById(R.id.selectedImage);
        updateImageView(path, myImage);

        printTopKLabels();

        myImage.setOnTouchListener(new OnSwipeTouchListener(PhotoItemActivity.this) {
                public void onSwipeTop() {
                    Toast.makeText(PhotoItemActivity.this, "You rated the image as neutral", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PhotoItemActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                public void onSwipeRight() {
                    Toast.makeText(PhotoItemActivity.this, "You rated the image as positive", Toast.LENGTH_SHORT).show();
                    PositiveImageLab.get(getApplicationContext()).addPositiveImage(path);
//                    Log.i(TAG,"PositiveImageLabSize: " + PositiveImageLab.get(getApplicationContext()).getPositiveImageLabSize());
                    Intent intent = new Intent(PhotoItemActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                public void onSwipeLeft() {
                    Toast.makeText(PhotoItemActivity.this, "You rated the image as negative", Toast.LENGTH_SHORT).show();
                    NegativeImageLab.get(getApplicationContext()).addNegativeImage(path);
//                    Log.i(TAG,"NegativeImageLabSize: " + NegativeImageLab.get(getApplicationContext()).getNegativeImageLabSize());
                    Intent intent = new Intent(PhotoItemActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                public void onSwipeBottom() {
                    Toast.makeText(PhotoItemActivity.this, "Back", Toast.LENGTH_SHORT).show();
                    finish();
                }

            });
    }

    // print the top labels and respective confidences
    private void printTopKLabels() {

        int[] labels = VectorLab.get(getApplicationContext()).queryLabels(mItem.getImagePath());
        float[] probs = VectorLab.get(getApplicationContext()).queryProbs(mItem.getImagePath());

        int noOfLabels = labels.length-1;
        df.setMaximumFractionDigits(1);

        label1.setText("1. " + labelList.get((labels[noOfLabels])));
        label2.setText("2. " + labelList.get((labels[noOfLabels-1])));
        label3.setText("3. " + labelList.get((labels[noOfLabels-2])));

        confidence1.setText(df.format(probs[noOfLabels]*100) + "%");
        confidence2.setText(df.format(probs[noOfLabels-1]*100) + "%");
        confidence3.setText(df.format(probs[noOfLabels-2]*100) + "%");
    }

    // appears also in HomeFragment.java
    private void updateImageView(String path, ImageView myImage) {
        File imgFile = new File(path);
        if (imgFile == null || !imgFile.exists()) {
            myImage.setImageDrawable(null);
        } else {
            Bitmap myBitmap = PictureUtils.getScaledBitmap(
                    imgFile.getPath(), this
            );
            myImage.setImageBitmap(myBitmap);
        }
    }

    private List<String> loadLabelList() throws IOException {
        labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(this.getAssets().open("labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }
}
