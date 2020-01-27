package ng.com.obkm.exquisitor.database;

import android.database.Cursor;
import android.database.CursorWrapper;


import static ng.com.obkm.exquisitor.database.VectorDBSchema.*;

public class VectorCursorWrapper extends CursorWrapper {

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public VectorCursorWrapper(Cursor cursor) {
        super(cursor);
    }


    public int[] getLabels() {
        int[] labels = new int[3];

        labels[0] = getInt(getColumnIndex(VectorTable.Cols.LABEL1));
        labels[1] = getInt(getColumnIndex(VectorTable.Cols.LABEL2));
        labels[2] = getInt(getColumnIndex(VectorTable.Cols.LABEL3));

        return labels;
    }

    public float[] getProbs() {
        float[] probs = new float[3];

        probs[0] = getFloat(getColumnIndex(VectorTable.Cols.PROB1));
        probs[1] = getFloat(getColumnIndex(VectorTable.Cols.PROB2));
        probs[2] = getFloat(getColumnIndex(VectorTable.Cols.PROB3));

        return probs;
    }
}
