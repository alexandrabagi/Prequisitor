
//delete? wont compile because Doggie was deleted

/*package ng.com.obkm.exquisitor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

public class GalleryGridViewAdapter extends BaseAdapter {

    private final Context mContext;

    public int[] imageArray = new int[100];

    public GalleryGridViewAdapter(Context context) {
        this.mContext = context;
        // this.mPhotoItems = mPhotoItems;

        for (int i = 0; i < 100; i++) {
            imageArray[i] = R.drawable.doggie;
        }
    }

    @Override
    public int getCount() {
        return imageArray.length;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return imageArray[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(imageArray[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(172, 172));

        return imageView;
    }

}
*/