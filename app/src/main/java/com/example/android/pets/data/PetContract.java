package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by VIP on 09/07/2018.
 */

public final class PetContract {

    private PetContract(){}

    public static final String CONTENT_AUTHORITY ="com.example.android.pets";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "Pets";

    public static final class PetEntry implements BaseColumns
    {

// used from Activities not from ContentProvider i.e it will be passed in the CRUD methods when they are called from Activities
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PETS);
        public static final String TABLE_NAME = "Pets";
        public static final String COULMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BREED = "breed";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_WEIGHT = "weight";

        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_UNKNOWN = 0;

        public static boolean isValidGender(int gender)
        {
            if(gender==GENDER_MALE||gender==GENDER_FEMALE||gender==GENDER_UNKNOWN)
                return true;
            return false;
        }
    }
}
