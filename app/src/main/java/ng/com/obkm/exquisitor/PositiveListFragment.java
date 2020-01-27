package ng.com.obkm.exquisitor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class PositiveListFragment extends Fragment {

    private static String TAG = "PositiveListFragment";

    private RecyclerView mImageRecyclerView;
    private ImageAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "OnCreateView was called");
        View view = inflater.inflate(R.layout.fragment_positive_list, container, false);
        mImageRecyclerView = (RecyclerView) view
                .findViewById(R.id.positive_recycler_view);
        mImageRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        ItemSpacesDecoration itemDecoration = new ItemSpacesDecoration(getActivity(), R.dimen.item_space);
        mImageRecyclerView.addItemDecoration(itemDecoration);

        updateUI();

        return view;
    }

    private void updateUI() {
        PositiveImageLab positiveLab = PositiveImageLab.get(getActivity());
//        List<PhotoItem> images = positiveLab.getPositiveImages();
        List<String> imagePaths = positiveLab.getPositiveImagePaths();
        Log.i(TAG, "PositiveImagesSize in PositiveListFragment: " + imagePaths.size());
        mAdapter = new ImageAdapter(imagePaths);
        mImageRecyclerView.setAdapter(mAdapter);
    }

    public class ImageHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private PhotoItem mPhotoItem;

        public void bind(String path) {
            mPhotoItem  = new PhotoItem(path);

//            Bitmap imageBitmap = mPhotoItem.getBitmap();
            Bitmap imageBitmap = PictureUtils.getThunbnail(path);
            mImageView.setImageBitmap(imageBitmap);
        }

        private ImageHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.grid_item_image, parent, false));

            mImageView = (ImageView) itemView.findViewById(R.id.grid_image);
        }
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageHolder> {

//        private List<PhotoItem> mPhotoItems;
        private List<String> mImagePaths;

        public ImageAdapter(List<String> paths) {
            mImagePaths = paths;
        }

        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new ImageHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ImageHolder holder, int position) {
            String path = mImagePaths.get(position);
            holder.bind(path);
        }

        @Override
        public int getItemCount() {
            return mImagePaths.size();
        }
    }

    public class ItemSpacesDecoration extends RecyclerView.ItemDecoration {

        private int mSpace;

        public ItemSpacesDecoration(int space) {
            this.mSpace = space;
        }

        public ItemSpacesDecoration(@NonNull Context context, @DimenRes int itemSpace) {
            this(context.getResources().getDimensionPixelSize(itemSpace));
        }

        // credit: https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mSpace/2, mSpace, mSpace/2, mSpace);

            // Add top margin only for the first item to avoid double mSpace between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = mSpace;
            } else {
                outRect.top = 0;
            }
        }
    }
}
