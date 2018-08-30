package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.pets.data.PetContract.PetEntry;

// subclassing SQLiteOPenHelper
public class PetsDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "pets.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PetEntry.TABLE_NAME + " (" +
                    PetEntry.COLUMN_PET_ID + " INTEGER PRIMARY KEY, " +
                    PetEntry.COLUMN_PET_NAME + " NOT NULL, " +
                    PetEntry.COLUMN_PET_BREED +
                    PetEntry.COLUMN_PET_GENDER + " NOT NULL DEFAULT 0, " +
                    PetEntry.COLUMN_PET_WEIGHT + " NOT NULL DEFAULT 0 );";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME;

    public PetsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // because subclassing SQLiteOpenHelper, need to implement onCreate and onUpgrade methods
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
