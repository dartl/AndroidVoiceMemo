package com.androidvoicememo;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidvoicememo.model.Note;

/**
 * Created by Dartl on 04.03.2016.
 */
public class ViewNoteActivity extends ParentActivity {
    private TextView viewNote_textVDate;
    private TextView viewNote_textVText;
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
        btn_copyText = (Button) findViewById(R.id.btn_copyText);

        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("note");
        viewNote_textVDate.setText(note.getDate());
        viewNote_textVText.setText(note.getText_note());

        btn_copyText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_copyText:
                ClipboardManager _clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                _clipboard.setText(viewNote_textVText.getText());
                Toast toast = Toast.makeText(getApplicationContext(),
                        getResources().getText(R.string.textSaveSuccess), Toast.LENGTH_SHORT);
                toast.show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        deleteNoteItem.setVisible(true);
        cancelItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //события меню
        switch (id) {
            case R.id.action_deleteNote:
                Intent intent = new Intent();
                intent.putExtra("delete_note", note);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}