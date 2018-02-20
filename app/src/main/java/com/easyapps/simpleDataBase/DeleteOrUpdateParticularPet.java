package com.easyapps.simpleDataBase;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.easyapps.simpleDataBase.data.PetContract;
import com.easyapps.simpleDataBase.data.PetContract.PetEntry;
import com.easyapps.simpleDataBase.data.PetDbHelper;

import static com.easyapps.simpleDataBase.ExperimentHelper.TAG;

public class DeleteOrUpdateParticularPet extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;
    private int mGender = PetEntry.GENDER_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_update_particular_pet);

        Toast.makeText(this, "delete and update only by name",
                Toast.LENGTH_LONG).show();

        mNameEditText = findViewById(R.id.delete_particular_pet_name);
        mBreedEditText = findViewById(R.id.delete_particular_pet_breed);
        mWeightEditText = findViewById(R.id.delete_particular_pet_weight);
        mGenderSpinner = findViewById(R.id.delete_particular_pet_spinner_gender);

        setupSpinner();
    }

    //------------------------------------
    private void setupSpinner() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mGenderSpinner.setAdapter(genderSpinnerAdapter);
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE;
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN;
            }
        });
    }

    //-------------------------------------------------------------------------
    private void deletePet() {
        Log.i(TAG, "deletePet: ");

        String nameString = mNameEditText.getText().toString().trim();
        int result = getContentResolver().delete(PetEntry.CONTENT_URI,
                PetEntry.COLUMN_PET_NAME + "=?",
                new String[]{nameString});

        Log.i(TAG, "deletePet: result = "+ result);

        if(result==-1)
            Toast.makeText(this,"row not deleted ",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,"row with Name "+nameString+ " was deleted ",
                    Toast.LENGTH_LONG).show();

    }

    //---------------------------
    private void updatePet() {
        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();
        int weight=0;
        try {
             weight = Integer.parseInt(weightString);
        } catch (Exception e) {

        }
        ContentValues values = new ContentValues();
        if (!nameString.isEmpty())
        values.put(PetEntry.COLUMN_PET_NAME, nameString);
        if(!breedString.isEmpty())
        values.put(PetEntry.COLUMN_PET_BREED, breedString);
        values.put(PetEntry.COLUMN_PET_GENDER, mGender);
        if(!weightString.isEmpty())
        values.put(PetEntry.COLUMN_PET_WEIGHT, weight);

       int noOffRowsUpdated=getContentResolver().update(PetEntry.CONTENT_URI,values,
                PetEntry.COLUMN_PET_NAME+"=?",
                new String[]{nameString} );

        Log.i(TAG, "updatePet: no of rows updated is "+ noOffRowsUpdated);

    }

    //-----------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete_update_particular, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                Log.i(TAG, "onOptionsItemSelected: ");
                deletePet();
                finish();
                break;

            case R.id.update:
                Log.i(TAG, "onOptionsItemSelected: ");
                updatePet();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}