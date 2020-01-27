package ng.com.obkm.exquisitor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoItemActivity2 extends AppCompatActivity {

    private static final String DEBUG_TAG = "singlePhoto";

    private String path = "";
    private PhotoItem mItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_photo);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        mItem = new PhotoItem(path);

        Bitmap imageBitmap = mItem.getBitmap();

        final ImageView myImage = (ImageView) findViewById(R.id.wholeScreen);

        myImage.setImageBitmap(imageBitmap);

        myImage.setOnTouchListener(new OnSwipeTouchListener(PhotoItemActivity2.this) {
                public void onSwipeTop() {
                    Toast.makeText(PhotoItemActivity2.this, "top", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PhotoItemActivity2.this, MainActivity.class);
                    startActivity(intent);
                }
                public void onSwipeRight() {
                    Toast.makeText(PhotoItemActivity2.this, "right", Toast.LENGTH_SHORT).show();
                    PositiveImageLab.get(getApplicationContext()).addPositiveImage(path);
//                    Log.i(DEBUG_TAG,"PositiveImageLabSize: " + PositiveImageLab.get(getApplicationContext()).getPositiveImageLabSize());
                    Intent intent = new Intent(PhotoItemActivity2.this, MainActivity.class);
                    startActivity(intent);
                }
                public void onSwipeLeft() {
                    Toast.makeText(PhotoItemActivity2.this, "left", Toast.LENGTH_SHORT).show();
                    NegativeImageLab.get(getApplicationContext()).addNegativeImage(path);
//                    Log.i(DEBUG_TAG,"NegativeImageLabSize: " + NegativeImageLab.get(getApplicationContext()).getNegativeImageLabSize());
                    Intent intent = new Intent(PhotoItemActivity2.this, MainActivity.class);
                    startActivity(intent);
                }
                public void onSwipeBottom() {
                    Toast.makeText(PhotoItemActivity2.this, "bottom", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
    }
}
