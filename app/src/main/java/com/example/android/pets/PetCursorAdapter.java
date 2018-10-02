package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;

/**
 * Created by VIP on 21/07/2018.
 */

public class PetCursorAdapter extends CursorAdapter {
    public PetCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

        String name = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_NAME));
        String summary = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_BREED));

        if(TextUtils.isEmpty(summary))
            summary = context.getString(R.string.unknown_breed);


        nameTextView.setText(name);
        summaryTextView.setText(summary);

    }
}
