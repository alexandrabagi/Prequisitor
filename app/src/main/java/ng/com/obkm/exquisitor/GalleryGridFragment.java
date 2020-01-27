//package ng.com.obkm.exquisitor;
//
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.provider.MediaStore;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.GridView;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class GalleryGridFragment extends Fragment {
//
//    private View v;
//    private GridView gridView;
//    private PhotoItem[] mPhotoItems;
//    private PhotoExtractor mPhotoExtractor = new PhotoExtractor();
//
//    public GalleryGridFragment() {
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (mPhotoExtractor.checkPermissionREAD_EXTERNAL_STORAGE(getActivity())) {
//
//            // FROM: https://stackoverflow.com/questions/8737054/how-to-get-path-by-mediastore-images-media
//            String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
//            Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media._ID);
//
//            final int count = cursor.getCount();
//            int image_column_index = cursor.getColumnIndex(MediaStore.Images.Media._ID);
//            int image_path_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//
//            final String[] paths = new String[count]; // contains all Uri
//
//            Bitmap[] bm = new Bitmap[count];
//            mPhotoItems = new PhotoItem[count];
//
//            for (int i = 0; i < count; i++) {
//                cursor.moveToPosition(i);
//                int id = cursor.getInt(image_column_index);
//                paths[i] = cursor.getString(image_path_index);
//                Log.i("README", "this is the IMAGEPATHINDEX: " + paths[i]);
//
//                mPhotoItems[i] = new PhotoItem(paths[i]);
//
//                bm[i] = MediaStore.Images.Thumbnails.getThumbnail(getActivity().getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
//            }
//            cursor.close();
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                         Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_dashboard_grid, container, false);
//
//        gridView = (GridView) getActivity().findViewById(R.id.galleryGridView);
//        GalleryGridViewAdapter photoAdapter = new GalleryGridViewAdapter(getActivity(), mPhotoItems);
//        gridView.setAdapter(photoAdapter);
//
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView parent, View view, int position, long id) {
//                PhotoItem photo = mPhotoItems[position];
//
//                String nameToPrint = photo.getPath();
//
//
//                Intent i = new Intent(getActivity(), PhotoItemActivity.class);
//                String strName = nameToPrint;
//                i.putExtra("name", strName);
//
//                startActivity(i);
//
//            }
//        });
//        return view;
//    }
//
//
//    public ArrayList<Drawable> getImages(){
//        ArrayList<Drawable> images = null;
//        return images;
//    }
//}
