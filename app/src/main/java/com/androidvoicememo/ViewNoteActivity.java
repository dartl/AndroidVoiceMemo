package com.androidvoicememo;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidvoicememo.adapters.TimeNotification;
import com.androidvoicememo.model.Note;

/**
 * Created by Dartl on 04.03.2016.
 */
public class ViewNoteActivity extends ParentActivity {
    private TextView viewNote_textVDate;
    private TextView viewNote_textVText;
    private Button btn_deleteNote;
    private Button btn_copyText;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarTop);
        setSupportActionBar(myToolbar);

        /* Выводим данные полученные из основного списка */
        viewNote_textVDate = (TextView) findViewById(R.id.viewNote_textVDate);
        viewNote_textVText = (TextView) findViewById(R.id.viewNote_textVText);
        btn_deleteNote = (Button) findViewById(R.id.btn_deleteNote);
        btn_copyText = (Button) findViewById(R.id.btn_copyText);

        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("note");
        viewNote_textVDate.setText(note.getDate());
        viewNote_textVText.setText(note.getText_note());

        btn_copyText.setOnClickListener(this);
        btn_deleteNote.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_deleteNote:
                Intent intent = new Intent();
                intent.putExtra("delete_note", note);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btn_copyText:
                ClipboardManager _clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                _clipboard.setText(viewNote_textVText.getText());
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Текст успешно скоппирован", Toast.LENGTH_LONG);
                toast.show();
                break;
            default:
                break;
        }
    }
}