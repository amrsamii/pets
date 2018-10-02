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

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;
    private ListView listView;
    private View view;
    private CursorAdapter cursorAdapter;
    private Toast mToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        listView = (ListView) findViewById(R.id.list_view);
        view = findViewById(R.id.empty_view);
        listView.setEmptyView(view);
        cursorAdapter = new PetCursorAdapter(this, null);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri uri = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id);
                intent.setData(uri);
                startActivity(intent);
            }
        });
       listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position,final long id) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    builder = new AlertDialog.Builder(CatalogActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                else
                    builder = new AlertDialog.Builder(CatalogActivity.this);
                builder.setTitle("Delete")
                        .setCancelable(false)
                        .setMessage("Are you sure you want to delete this pet?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,  int i) {

                                Uri uri = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI,id);
                               int rowsDeleted = getContentResolver().delete(uri,null,null);
                                if(rowsDeleted==0)
                                    makeToast(getString(R.string.delete_pet_failed));
                                else
                                    makeToast(getString(R.string.delete_successful));

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });

       fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);


     /*   Uri uri = Uri.parse("https//:www.google.com").buildUpon().appendQueryParameter("Format","JeoJSON").build();
        try {
            URL url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/

    }

    private void insertPet() {
        //   SQLiteDatabase db= mPetDbHelper.getWritableDatabase();

        ContentValues content = new ContentValues();
        content.put(PetContract.PetEntry.COLUMN_NAME, "Toto");
        content.put(PetContract.PetEntry.COLUMN_BREED, "Terrier");
        content.put(PetContract.PetEntry.COLUMN_GENDER, PetContract.PetEntry.GENDER_MALE);
        content.put(PetContract.PetEntry.COLUMN_WEIGHT, 7);

        Uri uri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, content);
        //long id= db.insert(PetContract.PetEntry.TABLE_NAME,null,content);

        long id = ContentUris.parseId(uri);
        Log.v("Catalog Acvtivity", "new row ID: " + id);
    }

    /*@Override
    protected void onStart() {
        super.onStart();

        displayDatabaseInfo();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
               /* Loader<Cursor> loader=getLoaderManager().getLoader(LOADER_ID);
                if(loader==null)
                    getLoaderManager().initLoader(LOADER_ID,null,this);
                else
                    getLoaderManager().restartLoader(LOADER_ID,null,this);*/
                // displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:

                deleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteConfirmationDialog()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder = new AlertDialog.Builder(CatalogActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        else
            builder = new AlertDialog.Builder(CatalogActivity.this);
        builder.setMessage(R.string.delete_all_pets_msg)
                .setPositiveButton(R.string.delete_pets,new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getContentResolver().delete(PetContract.PetEntry.CONTENT_URI,null,null);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    private void makeToast(String msg)
    {
        if(mToast!=null)
            mToast.cancel();
        else
            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.


        // Create and/or open a database to read from it
        //  SQLiteDatabase db = mPetDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.

        String projection[] = new String[]{
                PetContract.PetEntry.COULMN_ID,
                PetContract.PetEntry.COLUMN_NAME,
                PetContract.PetEntry.COLUMN_BREED,
                PetContract.PetEntry.COLUMN_GENDER,
                PetContract.PetEntry.COLUMN_WEIGHT};


        // Cursor cursor = db.query(PetContract.PetEntry.TABLE_NAME,projection,null,null,null,null,null);

        Cursor cursor = getContentResolver().query(PetContract.PetEntry.CONTENT_URI, projection, null, null, null);
        // try {
        // Display the number of rows in the Cursor (which reflects the number of rows in the
        // pets table in the database).
          /*  TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("Number of rows in pets database table: " + cursor.getCount());
            displayView.append("\n"+PetContract.PetEntry.COULMN_ID+" - "+ PetContract.PetEntry.COLUMN_NAME+" - "+ PetContract.PetEntry.COLUMN_BREED+" - "+ PetContract.PetEntry.COLUMN_GENDER+" - "
            + PetContract.PetEntry.COLUMN_WEIGHT);

            while(cursor.moveToNext())
            {
                int id = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COULMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_NAME));
                String breed = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_BREED));
                int gender = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_GENDER));
                int weight = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_WEIGHT));
                displayView.append("\n"+id+" - " +name+" - "+breed + " - "+gender+" - "+weight);

            }*/
        // ListView listView = (ListView)findViewById(R.id.list_view);

        //  View view = findViewById(R.id.empty_view);
        // listView.setEmptyView(view);
        // CursorAdapter cursorAdapter = new PetCursorAdapter(this, cursor);

        // listView.setAdapter(cursorAdapter);
     /*  } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }*/
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        if (id == LOADER_ID) {
            String projection[] = new String[]{
                    PetContract.PetEntry.COULMN_ID,
                    PetContract.PetEntry.COLUMN_NAME,
                    PetContract.PetEntry.COLUMN_BREED};
            return new CursorLoader(this, PetContract.PetEntry.CONTENT_URI, projection, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        cursorAdapter.swapCursor(null);
    }
}
