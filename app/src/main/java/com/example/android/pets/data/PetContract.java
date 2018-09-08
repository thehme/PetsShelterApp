package com.example.android.pets.data;

import android.provider.BaseColumns;

public final class PetContract {
    private static final String TAG = PetContract.class.getSimpleName();
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";

    private PetContract() {

    }

    public final static class PetEntry implements BaseColumns {
        public final static String TABLE_NAME = "pets";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PET_NAME = "name";
        public final static String COLUMN_PET_BREED = "breed";
        public final static String COLUMN_PET_GENDER = "gender";
        public final static String COLUMN_PET_WEIGHT = "weight";

        public final static int GENDER_UNKNOWN = 0;
        public final static int GENDER_FEMALE = 1;
        public final static int GENDER_MALE = 2;
    }
}
