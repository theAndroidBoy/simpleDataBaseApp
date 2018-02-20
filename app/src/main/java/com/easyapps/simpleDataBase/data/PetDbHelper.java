package com.easyapps.simpleDataBase.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.easyapps.simpleDataBase.ExperimentHelper;
import com.easyapps.simpleDataBase.data.PetContract.PetEntry;
import static com.easyapps.simpleDataBase.ExperimentHelper.smallBreak;
import static com.easyapps.simpleDataBase.ExperimentHelper.TAG;

public class PetDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shelter.db";
    private static final int DATABASE_VERSION = 1;

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null,
                DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: ");

        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + PetEntry.TABLE_NAME + " ("
                + PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                + PetEntry.COLUMN_PET_BREED + " TEXT, "
                + PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                + PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: ");
        // The database is still at version 1, so there's nothing to do be done here.
    }
 //------------------------------------------------------------------------------------


}