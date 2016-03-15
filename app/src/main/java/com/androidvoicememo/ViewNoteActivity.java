package com.androidvoicememo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.androidvoicememo.model.Note;

/**
 * Created by Dartl on 04.03.2016.
 */
public class ViewNoteActivity extends MainActivity {
    private TextView viewNote_textVDate;
    private TextView viewNote_textVText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note_activity);

        /* Выводим данные полученные из основного списка */
        viewNote_textVDate = (TextView) findViewById(R.id.viewNote_textVDate);
        viewNote_textVText = (TextView) findViewById(R.id.viewNote_textVText);
        Intent intent = getIntent();
        Note note = (Note) intent.getSerializableExtra("note");
        viewNote_textVDate.setText(note.getDate());
        viewNote_textVText.setText(note.getText_note());

    }
}