package com.example.android.pets.data;

import android.provider.BaseColumns;

public final class PetContract {
    private static final String TAG = PetContract.class.getSimpleName();

    private PetContract() {

    }

    public static final class PetEntry implements BaseColumns {
        public static final String TABLE_NAME = "pets.db";

        public static final String COLUMN_PET_ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";

        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_FEMALE = 1;
        public static final int GENDER_MALE = 2;
    }
}
