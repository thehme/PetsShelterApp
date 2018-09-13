package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = CatalogActivity.class.getSimpleName();
    private static final int URL_LOADER = 0;
    private PetCursorAdaptor petCursorAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // find reference to list view
        ListView listView = (ListView) findViewById(R.id.pets_list);

        // find empty view
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        petCursorAdaptor = new PetCursorAdaptor(this, null);
        listView.setAdapter(petCursorAdaptor);

        // add click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // id to identify which item was clicked on
                Intent editActivity = new Intent(CatalogActivity.this, EditorActivity.class);
                // create uri using ContentUris.withAppendedId
                // content://com.example.android.pets/id
                Uri uri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);
                editActivity.setData(uri);
                startActivity(editActivity);
            }
        });

        // initialize loader
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet() {
        try {
            ContentValues values = new ContentValues();
            values.put(PetEntry.COLUMN_PET_NAME, "Toto");
            values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
            values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
            values.put(PetEntry.COLUMN_PET_WEIGHT, 7);
            Uri uri = getContentResolver().insert(
                    PetEntry.CONTENT_URI,
                    values
            );
        } catch (Exception e) {
            Log.e(TAG, "Error inserting dummy data", e);
        }
    }

    private void deletePets() {
        try {
            int numDeleted = getContentResolver().delete(
                    PetEntry.CONTENT_URI, null, null
            );
        } catch (Exception e) {
            Log.e(TAG, "Error deleting data", e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // Do nothing for now
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };

        switch (loaderId) {
            case URL_LOADER:
                return new CursorLoader(
                    this,
                    PetEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        petCursorAdaptor.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        petCursorAdaptor.swapCursor(null);
    }


}