package ng.com.obkm.exquisitor.tflite;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ng.com.obkm.exquisitor.PhotoItem;

public class CreateVectors {

    final static String TAG = "createVectors";
    //private static Context sContext;
    private static List<PhotoItem> items;



    public static void classifyImages(final Context context) {
        Thread thread = new Thread(){
            public void run(){

        final Classify mClassify;
        items = new ArrayList<>();

        // FROM: https://stackoverflow.com/questions/8737054/how-to-get-path-by-mediastore-images-media
                final String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Log.i(TAG, "Projection length: " + projection.length);
        Log.i(TAG, "Projection content: " + projection[0]);
        Log.i(TAG, "Projection content: " + projection[1]);
        final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media._ID);
        // number of pictures on the phone
        final int count = cursor.getCount();
        final int image_path_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        mClassify = new Classify(context);


            for (int i = 0; i < count ; i++) {
                final int lars = i;
                    applyTFOnOnePic(lars, mClassify, cursor, image_path_index);
                    Log.i(TAG, "works");
                }

        cursor.close();
            }

        };
        thread.start();
    }

    private static void applyTFOnOnePic(int i, Classify mClassify, Cursor cursor, int image_path_index ){
        mClassify.setUpTFLite();
        cursor.moveToPosition(i);
        // get the path of the random image
        final String path = cursor.getString(image_path_index);
        Bitmap imageBitmap = getBitmap(path);
        mClassify.runTFLite(imageBitmap, path);
    }

    private static Bitmap getBitmap(String path) {
        File imgFile = new File(path);

        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return myBitmap;
        }
        return null;
    }
}
