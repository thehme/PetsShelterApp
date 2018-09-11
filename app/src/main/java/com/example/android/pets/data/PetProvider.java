package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {
    private static final String TAG = PetProvider.class.getSimpleName();
    private PetsDbHelper mDbHelper;

    // This cursor will hold the result of the query
    private Cursor cursor;

    private static final int PETS = 100;
    private static final int PET_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new PetsDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                // TODO: Perform database query on pets table
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // so listener, CatalogActivity, is notified when uri changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                Uri uriResult = insertPet(uri, contentValues);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return uriResult;
            default:
                // can't insert on row where pet already exists
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private void validateValues(ContentValues values) {
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (name == null || TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        if (gender == null && !PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires a gender");
        }
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires a non negative weight");
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        validateValues(values);

        // TODO: Insert a new pet into the pets database table with the given ContentValues
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(
                PetEntry.TABLE_NAME,
                null,
                values
        );
        if (id == -1) {
            Log.e(TAG, "Error inserting new pet into db for " + uri);
            return null;
        }

        // notify all listeners that a change has occurred to uri
        // second parameter is optional observer, but passing null makes it so that
        // by default cursor adapter object is notified
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }


    private void validateValuesForUpdate(ContentValues values) {
        if (values.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
            if (name == null || TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender == null && !PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires a gender");
            }
        }
        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires a non negative weight");
            }
        }
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.size() == 0) {
            return 0;
        }

        validateValuesForUpdate(contentValues);
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw  new IllegalArgumentException("update is not supported for " + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int updatedNum = database.update(
                PetEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        if (updatedNum > 0) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return updatedNum;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return deletePet(uri, selection, selectionArgs);
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return deletePet(uri,selection, selectionArgs);
            default:
                throw new IllegalArgumentException("delete no supported for " + uri);
        }
    }

    private int deletePet(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int deletedNum = database.delete(
                PetEntry.TABLE_NAME,
                selection,
                selectionArgs
        );
        if (deletedNum > 0) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return deletedNum;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetContract.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}