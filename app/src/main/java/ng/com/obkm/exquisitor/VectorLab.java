package ng.com.obkm.exquisitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ng.com.obkm.exquisitor.database.VectorBaseHelper;
import ng.com.obkm.exquisitor.database.VectorCursorWrapper;
import ng.com.obkm.exquisitor.database.VectorDBSchema.VectorTable;

public class VectorLab {

    private static VectorLab sVectorLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static VectorLab get(Context context) {
        if (sVectorLab == null) {
            sVectorLab = new VectorLab(context);
        }
        return sVectorLab;
    }

    private VectorLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new VectorBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addVector(String imagePath, int label1, float prob1, int label2, float prob2, int label3, float prob3) {
        ContentValues values = getContentValues(imagePath, label1, prob1, label2, prob2, label3, prob3);
        mDatabase.insert(VectorTable.NAME, null, values);
    }

    public int[] getLabelsFromVector(String path) {

        return null;
    }

    private VectorCursorWrapper queryItems (String whereClause, String[]whereArgs){
        Cursor cursor = mDatabase.query(
                VectorTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );
        return new VectorCursorWrapper(cursor);
    }

    //returns top labels for given path
    public int[] queryLabels (String imagePath) {
        VectorCursorWrapper cursor = queryItems(
                VectorTable.Cols.PATH + " = ?",
                new String[]{imagePath}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getLabels();
        } finally {
            cursor.close();
        }
    }

    //returns top labels for given path
    public float[] queryProbs (String imagePath) {
        VectorCursorWrapper cursor = queryItems(
                VectorTable.Cols.PATH + " = ?",
                new String[]{imagePath}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getProbs();
        } finally {
            cursor.close();
        }
    }

//    public int getVectorLabSize() {
//        return getVectorImages().size();
//    }

//    public PhotoItem getPositiveImage(String imagePath) {
//        return null;
//    }

//    public void updateImage(PhotoItem item) {
//        // pg. 279
//        String path = item.getPath();
//        ContentValues values = getContentValues(item);
//
//        mDatabase.update(VectorTable.NAME, values, PositiveTable.Cols.PATH + " =?", new String[] {path});
//    }

    private VectorCursorWrapper queryImages(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                VectorTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new VectorCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(String imagePath, int label1, float prob1, int label2, float prob2, int label3, float prob3) {
        ContentValues values = new ContentValues();
        values.put(VectorTable.Cols.PATH, imagePath);
        values.put(VectorTable.Cols.LABEL1, label1);
        values.put(VectorTable.Cols.PROB1, prob1);
        values.put(VectorTable.Cols.LABEL2, label2);
        values.put(VectorTable.Cols.PROB2, prob2);
        values.put(VectorTable.Cols.LABEL3, label3);
        values.put(VectorTable.Cols.PROB3, prob3);

        return values;
    }
}
