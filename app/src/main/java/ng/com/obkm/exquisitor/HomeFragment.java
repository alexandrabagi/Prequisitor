package ng.com.obkm.exquisitor;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Bitmap;
import android.widget.ImageView;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import ng.com.obkm.exquisitor.tflite.Classify;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private String[] mItemPaths = new String[6];
    private Set<String> mItemPathSet = new HashSet<String>();
    private Classify mClassify;

    final String TAG = "home";

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView called");
        View v = inflater.inflate(R.layout.fragment_home_grid, container, false);

            // get the 6 random item
            //mItemPaths = getRandomImages();

            mItemPathSet = getRandomImageSet();
            int size = mItemPathSet.size();
            Log.d(TAG, "Size: " + size);

            int i = 0;
            for (Iterator<String> it = mItemPathSet.iterator(); it.hasNext();) {
                final String path = it.next();
                Log.i("Image Path", path);

            // for (int i = 0; i < 6; i++) {
                // get the Bitmap of them
                //final String path = mItemPaths[i];

                //final Bitmap imageBitmap = getBitmap(path);
                //get the imageView of them
                String number = String.valueOf(i + 1);
                String name = "galleryImage".concat(number);
                int resID = getResources().getIdentifier(name, "id", getActivity().getPackageName());
                final ImageView myImage = (ImageView) v.findViewById(resID);
                // set bitmap to imageView
                //myImage.setImageBitmap(imageBitmap);
                updateImageView(path, myImage);
                // set up onClickListener for the view
                //if (imageBitmap != null) {
                    //Log.i(TAG, "Check imageBitmap " + (imageBitmap==null));
                    myImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), PhotoItemActivity.class);
                            Log.i(TAG, "Intent sends path: " + path);
                            // mClassify.runTFLite(intent, imageBitmap);
                            intent.putExtra("path", path);

                            startActivity(intent);
                        }
                    });
                //}
                i++;
            }
            return v;
    }

    private void updateImageView(String path, ImageView myImage) {
        File imgFile = new File(path);
        if (imgFile == null || !imgFile.exists()) {
            myImage.setImageDrawable(null);
        } else {
//            Bitmap myBitmap = PictureUtils.getScaledBitmap(
//                    imgFile.getPath(), getActivity());
            Bitmap myBitmap = PictureUtils.getThunbnail(imgFile.getPath());
            myImage.setImageBitmap(myBitmap);
        }
    }

//    private String[] getRandomImages() {
//        // FROM: https://stackoverflow.com/questions/8737054/how-to-get-path-by-mediastore-images-media
//        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
//        Log.i(TAG, "Projection length: " + projection.length);
//        Log.i(TAG, "Projection content: " + projection[0]);
//        Log.i(TAG, "Projection content: " + projection[1]);
//
//        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media._ID);
//
//        // number of pictures on the phone
//        final int count = cursor.getCount();
//        int image_path_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//        Log.i(TAG, "Image_path_index: " + image_path_index);
//
//        mClassify = new Classify(getActivity());
//        mClassify.setUpTFLite();
//
//        final Random random = new Random();
//        String[] paths = new String[6];
//
//        for (int i = 0; i < 6; i++) {
//            int rnd = random.nextInt(count);
//            cursor.moveToPosition(rnd);
//            // get the path of the random image
//            final String path = cursor.getString(image_path_index);
//            paths[i] = path;
//        }
//        cursor.close();
//        return paths;
//    }


    private HashSet<String> getRandomImageSet() {
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media._ID);

        // number of pictures on the phone
        final int count = cursor.getCount();
        int image_path_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

        mClassify = new Classify(getActivity());
        mClassify.setUpTFLite();

        final Random random = new Random();
        HashSet<String> paths = new HashSet<>();

        while (paths.size() < 6) {
            int rnd = random.nextInt(count);
            cursor.moveToPosition(rnd);
            // get the path of the random image
            final String path = cursor.getString(image_path_index);
            paths.add(path);
        }
        cursor.close();
        return paths;
    }
}
