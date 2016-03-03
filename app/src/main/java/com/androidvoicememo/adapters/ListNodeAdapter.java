package com.androidvoicememo.adapters;

import android.content.Context;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dartl on 03.03.2016.
 */
public class ListNodeAdapter extends SimpleAdapter {

    public ListNodeAdapter(Context context,
                           ArrayList data, int resource,
                           String[] from, int[] to) {
        super(context, data, resource, from, to);
    }
/*
    @Override
    public void setViewText(TextView v, String text) {
        // метод супер-класса, который вставляет текст
        super.setViewText(v, text);
        // если нужный нам TextView, то разрисовываем
    }

    @Override
    public void setViewImage(ImageView v, int value) {
        // метод супер-класса
        super.setViewImage(v, value);
    }
*/
}
