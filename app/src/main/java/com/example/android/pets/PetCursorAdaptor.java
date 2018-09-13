package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;

public class PetCursorAdaptor extends CursorAdapter {

    public PetCursorAdaptor(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // TODO: Fill out this method and return the list item view (instead of null)
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO: Fill out this method
        // Find list item text views were data will be populated
        TextView petName = (TextView) view.findViewById(R.id.pet_name);
        TextView petBreed = (TextView) view.findViewById(R.id.pet_breed);

        // get data from current cursor item
        String name = cursor.getString(cursor.getColumnIndexOrThrow(PetEntry.COLUMN_PET_NAME));
        String breed = cursor.getString(cursor.getColumnIndexOrThrow(PetEntry.COLUMN_PET_BREED));

        // populate text views with cursor data
        petName.setText(name);
        if (TextUtils.isEmpty(breed)) {
            petBreed.setText("Unknown breed");
        } else {
            petBreed.setText(breed);
        }
    }
}
