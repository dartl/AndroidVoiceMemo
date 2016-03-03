package com.androidvoicememo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.androidvoicememo.adapters.ListNodeAdapter;
import com.androidvoicememo.db.SQLiteDBHelper;
import com.androidvoicememo.model.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private Button btn_show_AddNote;
    private ListView listViewNodes;
    //private ArrayList<Note> notes;

    final String ATTRIBUTE_NAME_PATH_FILE = "path_file";
    final String ATTRIBUTE_NAME_TEXT_NOTE = "text_note";
    final String ATTRIBUTE_NAME_DATE = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Тестовое подключение к БД */
        SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);
        SQLiteDatabase db;
        try {
            db = dbHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = dbHelper.getReadableDatabase();
        }
        ContentValues newValues = new ContentValues();

        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_FILE, "testFile");
        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE, "testTextNote");
        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE, "24.02.2016 15:43");

        db.insert(SQLiteDBHelper.NOTES_TABLE_NAME, null, newValues);

        Cursor testC = db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTES_TABLE_NAME, null);
        /* Подключение к БД - КОНЕЦ */

        /* Выводим элементы */
        ArrayList<Map<String, Object>> notes = new ArrayList<Map<String, Object>>();
        Map<String, Object> m;
        Note temp;

        while(testC.moveToNext()) {
            temp = new Note(testC);
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_DATE, temp.getDate());
            m.put(ATTRIBUTE_NAME_TEXT_NOTE, temp.getText_note());
            notes.add(m);
        }

        listViewNodes = (ListView) findViewById(R.id.listViewNodes);

        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME_DATE, ATTRIBUTE_NAME_TEXT_NOTE };
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.textVDate, R.id.textVNode_text };
        SimpleAdapter sAdapter = new SimpleAdapter(this, notes, R.layout.list_notes_activity, from, to);

        listViewNodes.setAdapter(sAdapter);

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
                startActivity(intent);
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
}
