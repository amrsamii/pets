package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by VIP on 09/07/2018.
 */

public class PetDbHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME= "shelter.db";
    public static final int DATABASE_VERSION = 1;

    public PetDbHelper(Context context) {
       super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    // create the database
    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PETS_TABLE = "CREATE TABLE "+ PetContract.PetEntry.TABLE_NAME+" ("
                +PetContract.PetEntry.COULMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +PetContract.PetEntry.COLUMN_NAME+" TEXT NOT NULL, "
                +PetContract.PetEntry.COLUMN_BREED+" TEXT, "
                +PetContract.PetEntry.COLUMN_GENDER+" INTEGER NOT NULL, "
                +PetContract.PetEntry.COLUMN_WEIGHT+" INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }
    // update the darabase

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
