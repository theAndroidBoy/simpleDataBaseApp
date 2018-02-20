package com.easyapps.simpleDataBase;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.easyapps.simpleDataBase.ExperimentHelper.TAG;

import com.easyapps.simpleDataBase.data.PetContract.PetEntry;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.PersonViewHolder> {

    private CustomItemClickListener listener;
    private Cursor mCursor;

    //----------------------------------------------------------------------------
    RecyclerViewAdapter(CustomItemClickListener listener) {
        this.listener = listener;
    }

    //---------------------------------------------------------
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.i(TAG, "onAttachedToRecyclerView: ");
    }

    //-----------------------------------------------
    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.i(TAG, "onCreateViewHolder: ");

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_pet,
                viewGroup, false);
        final PersonViewHolder personViewHolder = new PersonViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, personViewHolder.getAdapterPosition());
            }
        });
        return personViewHolder;
    }

    //------------------------------------------
    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int position) {
        Log.i(TAG, "onBindViewHolder: ");
        Log.i(TAG, "onBindViewHolder: position : " + position);

        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        try {
            int idColumnIndex = mCursor.getColumnIndex(PetEntry._ID);
            int nameColumnIndex = mCursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = mCursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = mCursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = mCursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            int currentID = mCursor.getInt(idColumnIndex);
            String currentName = mCursor.getString(nameColumnIndex);
            String currentBreed = mCursor.getString(breedColumnIndex);
            int currentGender = mCursor.getInt(genderColumnIndex);
            int currentWeight = mCursor.getInt(weightColumnIndex);

            personViewHolder.petId.setText("" + currentID);
            personViewHolder.petName.setText(currentName);
            personViewHolder.petBreed.setText(currentBreed);
            personViewHolder.petGender.setText("" + currentGender);
            personViewHolder.petWeight.setText("" + currentWeight);

        } finally {
            Log.i(TAG, "onBindViewHolder: finally block");
        }

    } // onBind closed

    //----------------------------------------------------------------------------------
    @Override
    public int getItemCount() {

        if (mCursor == null)
            return 0;

        return mCursor.getCount();
    }

    //-------------------------------------------------------------------------
    void swapCursor(Cursor newCursor) {
        Log.i(TAG, "swapCursor: ");
        // Always close the previous mCursor first
        if (mCursor != null)
        {
            mCursor.close();
            Log.i(TAG, "swapCursor: close the existing Cursor");
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
            Log.i(TAG, "swapCursor: notifyDataSetChanged");
        }
    } //swap cursor closed

    //--------------------------------------------------------------------------------
    class PersonViewHolder extends RecyclerView.ViewHolder {

        TextView petId;
        TextView petName;
        TextView petBreed;
        TextView petGender;
        TextView petWeight;

        PersonViewHolder(View itemView) {
            super(itemView);

            petId = itemView.findViewById(R.id.idValue);
            petName = itemView.findViewById(R.id.nameValue);
            petBreed = itemView.findViewById(R.id.breedValue);
            petGender = itemView.findViewById(R.id.genderValue);
            petWeight = itemView.findViewById(R.id.measurementValue);

        }
    }
    //---------------------------------------------------------------------------
}
