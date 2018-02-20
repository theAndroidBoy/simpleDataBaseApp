package com.easyapps.simpleDataBase;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.easyapps.simpleDataBase.data.PetContract.PetEntry;
import com.easyapps.simpleDataBase.data.PetDbHelper;


import static com.easyapps.simpleDataBase.ExperimentHelper.TAG;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ONE_ID = 10;
    RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    //---------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Log.i(TAG, "onCreate: MainActivity ");

        Log.i(TAG, "onCreate: FloatingActionButton initialized");
        
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        //recyclerView stuff
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        Log.i(TAG, "onCreate: recyclerView initialized");
    }

    //----------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();

        initializeAdapter();
    }

    //--------------------------------------------
    private void initializeAdapter() {
        Log.i(TAG, "initializeAdapter: with no data source ");

        adapter = new RecyclerViewAdapter(new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });
        recyclerView.setAdapter(adapter);

        Log.i(TAG, "initializeAdapter: LoaderManager started with LoaderID");
        getLoaderManager().initLoader(LOADER_ONE_ID, null, this);
    }

    //----------------------------------------------
    private void insertPet() {
        Log.i(TAG, "insertPet: ");

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
        Log.i(TAG, "insertPet: Uri of inserted row "+newUri);
    }

    //----------------------------
    private void deleteAllPets() {
        int noOffRows = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
        Log.i(TAG, "deleteAllPets: no of rows deleted " + noOffRows);
    }

    //-----------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    //------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_insert_dummy_data:
                insertPet();
                break;

            case R.id.action_insert_5_dummy_pets:
                insertFiveDummyPets();
                break;

            case R.id.action_delete_all_entries:
                deleteAllPets();
                break;                 

            case R.id.action_delete_update_particular_pet:
                Intent intent = new Intent(MainActivity.this,
                        DeleteOrUpdateParticularPet.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //-----------------------------------
    private void insertFiveDummyPets() {
        Log.i(TAG, "insertFiveDummyPets: ");

        String[] petNames = getResources().getStringArray(R.array.petNames);
        String[] petBreeds = getResources().getStringArray(R.array.petBreeds);
        int[] petGenders = getResources().getIntArray(R.array.petGenders);
        int[] petWeights = getResources().getIntArray(R.array.petWeights);
        ContentValues values;

        for (int i = 0; i < 5; i++) {
            values = new ContentValues();
            values.put(PetEntry.COLUMN_PET_NAME, petNames[i]);
            values.put(PetEntry.COLUMN_PET_BREED, petBreeds[i]);
            values.put(PetEntry.COLUMN_PET_GENDER, petGenders[i]);
            values.put(PetEntry.COLUMN_PET_WEIGHT, petWeights[i]);

            Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
            Log.i(TAG, "insertFiveDummyPets: Uri of inserted row : "+newUri);
        }
    } //insertFiveDummyPets closing

    //-----------------
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Log.i(TAG, "onCreateLoader: ");

        switch (loaderId) {
            case LOADER_ONE_ID:
                String[] projection = {
                        PetEntry._ID,
                        PetEntry.COLUMN_PET_NAME,
                        PetEntry.COLUMN_PET_BREED,
                        PetEntry.COLUMN_PET_GENDER,
                        PetEntry.COLUMN_PET_WEIGHT};
                Log.i(TAG, "onCreateLoader: start a CursorLoader ,with Content URI and projection");

                return new CursorLoader(this,
                        PetEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG, "onLoadFinished: ");
        Log.i(TAG, "onLoadFinished: call swapCursor() method on adapter");
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(TAG, "onLoaderReset: ");
        adapter.swapCursor(null);
    }
//----------------------------------------
} //MainActivity close
