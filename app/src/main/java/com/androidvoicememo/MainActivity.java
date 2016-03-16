package com.androidvoicememo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.androidvoicememo.adapters.CursorNoteAdapter;
import com.androidvoicememo.db.SQLiteDBHelper;
import com.androidvoicememo.model.Note;

import java.util.List;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private Button btn_show_AddNote;
    private ListView listViewNodes;
    private CursorNoteAdapter sAdapterNotes;
    // массив имен атрибутов, из которых будут читаться данные
    private String[] from = {SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE, SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE};
    // массив ID View-компонентов, в которые будут вставлять данные
    private int[] to = {R.id.textVNode_text, R.id.textVDate  };
    private Cursor cursor_Notes;

    private static final int ADD_NEW_NOTE = 400;

    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Тестовое подключение к БД */
        SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);

        try {
            db = dbHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = dbHelper.getReadableDatabase();
        }

        cursor_Notes = db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTES_TABLE_NAME, null);
        /* Подключение к БД - КОНЕЦ */

        listViewNodes = (ListView) findViewById(R.id.listViewNodes);

        /* Обработка события клика на элемент списка */
        listViewNodes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ViewNoteActivity.class);
                Cursor cursor_Note = db.rawQuery("SELECT * FROM " +
                        SQLiteDBHelper.NOTES_TABLE_NAME + " WHERE "+
                        SQLiteDBHelper.NOTES_TABLE_COLUMN_ID +" ="+id, null);
                cursor_Note.moveToNext();
                Note note = new Note(cursor_Note);
                intent.putExtra("note", note);
                startActivity(intent);
            }
        });


        //Адаптер на курсор из БД
        sAdapterNotes = new CursorNoteAdapter(this,R.layout.list_notes_activity, cursor_Notes, from, to);
        listViewNodes.setAdapter(sAdapterNotes);


        /* Обработка событий клика */
        btn_show_AddNote = (Button) findViewById(R.id.btn_show_AddNote);
        btn_show_AddNote.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /* Клик по кнопке "Добавить заметку" */
            case R.id.btn_show_AddNote:
                Intent intent = new Intent(this, AddNoteActivity.class);
                startActivityForResult(intent, ADD_NEW_NOTE);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //события меню
        switch (id) {
            case R.id.app_name_addNote:
                Intent intent = new Intent(this, AddNoteActivity.class);
                startActivity(intent);
            break;
            case R.id.app_name_searchNote:
            break;
            case R.id.app_name_settings:
            break;
            case R.id.app_name_export_import:
            break;
            case R.id.app_exit:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        // Если мы вернулись с окна Создания Заметки, нажав "Сохранить", то сохраняем в БД
        if (requestCode == ADD_NEW_NOTE) {
            Note new_note = (Note) data.getSerializableExtra("new_note");
            data.removeExtra("new_note");
            if (new_note != null) {
                ContentValues newValues = new ContentValues();
                newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_FILE, new_note.getPath_file());
                newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE, new_note.getText_note());
                newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE, new_note.getDate());
                db.insert(SQLiteDBHelper.NOTES_TABLE_NAME, null, newValues);
                cursor_Notes = db.rawQuery("SELECT * FROM " +
                        SQLiteDBHelper.NOTES_TABLE_NAME, null);
                cursor_Notes.moveToLast();
                sAdapterNotes.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
