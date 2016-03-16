package com.androidvoicememo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidvoicememo.model.Note;

/**
 * Created by Dartl on 04.03.2016.
 */
public class ViewNoteActivity extends MainActivity {
    private TextView viewNote_textVDate;
    private TextView viewNote_textVText;
    private Button startRecord;

    private String fileName;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note_activity);

        /* Выводим данные полученные из основного списка */
        viewNote_textVDate = (TextView) findViewById(R.id.viewNote_textVDate);
        viewNote_textVText = (TextView) findViewById(R.id.viewNote_textVText);
        startRecord = (Button) findViewById(R.id.startRecord);

        Intent intent = getIntent();
        Note note = (Note) intent.getSerializableExtra("note");
        viewNote_textVDate.setText(note.getDate());
        viewNote_textVText.setText(note.getText_note());
        fileName = note.getPath_file();

        startRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /* Клик по кнопке "Прослушать" */
            case R.id.startRecord:
                try {
                    if (fileName != null)
                    releasePlayer();
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(fileName);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}