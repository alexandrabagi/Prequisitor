package ng.com.obkm.exquisitor.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VectorBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "vectorBase.db";

    public VectorBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + VectorDBSchema.VectorTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                VectorDBSchema.VectorTable.Cols.PATH + ", " +
                        VectorDBSchema.VectorTable.Cols.LABEL1 + ", " +
                        VectorDBSchema.VectorTable.Cols.PROB1 + ", " +
                        VectorDBSchema.VectorTable.Cols.LABEL2 + ", " +
                        VectorDBSchema.VectorTable.Cols.PROB2 + ", " +
                        VectorDBSchema.VectorTable.Cols.LABEL3 + ", " +
                        VectorDBSchema.VectorTable.Cols.PROB3 +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
