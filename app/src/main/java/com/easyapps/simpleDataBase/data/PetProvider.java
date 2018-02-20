package com.easyapps.simpleDataBase.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.easyapps.simpleDataBase.ExperimentHelper.smallBreak;
import static com.easyapps.simpleDataBase.ExperimentHelper.TAG;


public class PetProvider extends ContentProvider {

    public static final String TAG = "flow";
    private static final int PETS = 100;
    private static final int PET_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //------------------------------------------------------------------------------
    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    private PetDbHelper petDbHelper;

    //---------------------------------------------------------------------------
    @Override
    public boolean onCreate() {
        Log.i(TAG, "onCreate: PetProvider ,Made a PetDbHelper object");
        petDbHelper = new PetDbHelper(getContext());
        return true;
    }

    //---------------------------------------------------------------------
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Log.i(TAG, "query: pet provider");
        SQLiteDatabase database = petDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        Log.i(TAG, "query: use Uri Matcher to find what kind of URI");
        switch (match) {
            case PETS:
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, null, null,
                        null, null, null);
                break;
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.i(TAG, "query: leaving Pet Provider with a cursor");

        return cursor;
    }

    //----------------------------------------------------------
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.i(TAG, "insert: pet provider");
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    //--------------------------------------------------------------------
    private Uri insertPet(Uri uri, ContentValues values) {
        Log.i(TAG, "insertPet: pet provider");
        // Check that the name is not null
        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        // Check that the gender is valid
        Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        SQLiteDatabase database = petDbHelper.getWritableDatabase();
        long id = database.insert(PetContract.PetEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    //-------------------------------------------------------------
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        Log.i(TAG, "update: pet provider");
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                Log.i(TAG, "update: case pets without id");
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                Log.i(TAG, "update: case pets with id");
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    //--------------------------------------------------
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.i(TAG, "updatePet: pet provider");
        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = petDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(PetContract.PetEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }

    //------------------------------------------------------------------------
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i(TAG, "delete: pet provider");
        SQLiteDatabase database = petDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                Log.i(TAG, "delete: case delete without id ");
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PET_ID:
                Log.i(TAG, "delete: case delete with id");
                rowsDeleted = database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;

    }

    //---------------------------------------------------------------
    @Override
    public String getType(Uri uri) {
        return null;
    }
}