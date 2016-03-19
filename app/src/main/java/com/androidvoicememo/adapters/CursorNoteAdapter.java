package com.androidvoicememo.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Dartl on 03.03.2016.
 */
public class CursorNoteAdapter extends SimpleCursorAdapter {

    public CursorNoteAdapter(Context context,
                             int resource,Cursor data,
                             String[] from, int[] to) {
        super(context, resource, data, from, to);
    }

    @Override
    public void setViewText(TextView v, String text) {
        // метод супер-класса, который вставляет текст
        if (text.length() >= 100) {
            text = text.substring(0, 100);
            text += "...";
        }


        super.setViewText(v, text);
        // если нужный нам TextView, то разрисовываем
    }

/*
    @Override
    public void setViewImage(ImageView v, int value) {
        // метод супер-класса
        super.setViewImage(v, value);
    }
*/
}
