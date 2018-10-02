/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * EditText field to enter the pet's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the pet's breed
     */
    private EditText mBreedEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mWeightEditText;

    /**
     * EditText field to enter the pet's gender
     */
    private Spinner mGenderSpinner;


    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    private Uri mUri;

    private boolean mPetHasChanged = false;

   private String mName;

    private Toast mToast;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener()
    {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged=true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        mUri = getIntent().getData();
        if (mUri == null)
            setTitle(getString(R.string.add));
        else {
            setTitle(getString(R.string.edit));
            getLoaderManager().initLoader(0, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);

        Log.v(EditorActivity.class.getSimpleName(), "OnCreate");
        setupSpinner();


    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetContract.PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetContract.PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetContract.PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    private void savePet() {
        // SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //trim() eliminate leading and trialing white space

        String name = mNameEditText.getText().toString().trim();
        String breed = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(breed) && TextUtils.isEmpty(weightString) && mGender == PetContract.PetEntry.GENDER_UNKNOWN)
            return;

        int weight = 0;
        if (!TextUtils.isEmpty(weightString))
            weight = Integer.parseInt(weightString);


        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_NAME, name);
        values.put(PetContract.PetEntry.COLUMN_BREED, breed);
        values.put(PetContract.PetEntry.COLUMN_WEIGHT, weight);
        values.put(PetContract.PetEntry.COLUMN_GENDER, mGender);

        // if(TextUtils.isEmpty(name)&&TextUtils.isEmpty(breed)&&TextUtils.isEmpty(weightString)&&mGender==PetContract.PetEntry.GENDER_UNKNOWN)
        //   return;

        if (mUri == null) {

           if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Name is required !!!", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);
            //  long id =   db.insert(PetContract.PetEntry.TABLE_NAME,null,values);


            if (uri == null)
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.right), Toast.LENGTH_SHORT).show();
        } else {
           if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Name is required !!!", Toast.LENGTH_SHORT).show();
                return;
            }
            int rowsAffected = getContentResolver().update(mUri, values, null, null);
            if (rowsAffected == 0)
                Toast.makeText(this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show();
        }


    }
    private void makeToast(String msg)
    {
        if(mToast!=null)
            mToast.cancel();
        else
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener)
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder = new AlertDialog.Builder(EditorActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        else
            builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg)
                .setPositiveButton(R.string.discard,discardButtonClickListener)
                .setNegativeButton(R.string.keep_editing,new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(!mPetHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedChangesDialog(onClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:

                savePet();
                //Exit Activity
                finish();
                return true;
                // Respond to a click on the "Delete" menu option
         //    case R.id.action_delete:
                // Do nothing for now
            //    return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if(!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                }
            };
            showUnsavedChangesDialog(onClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

   /* @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
         super.onPrepareOptionsMenu(menu);
        if(mUri==null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;

    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String projection[] = new String[]{
                PetContract.PetEntry.COULMN_ID,
                PetContract.PetEntry.COLUMN_NAME,
                PetContract.PetEntry.COLUMN_BREED,
                PetContract.PetEntry.COLUMN_GENDER,
                PetContract.PetEntry.COLUMN_WEIGHT};

        return new CursorLoader(this, mUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_NAME));
            String breed = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_BREED));
            int gender = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_GENDER));
            int weight = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_WEIGHT));

            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(String.valueOf(weight));
            mGenderSpinner.setSelection(gender);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText(String.valueOf(""));
        mGenderSpinner.setSelection(0);
    }
}