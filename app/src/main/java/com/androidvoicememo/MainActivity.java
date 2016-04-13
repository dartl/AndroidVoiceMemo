package com.androidvoicememo;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidvoicememo.adapters.CursorNoteAdapter;
import com.androidvoicememo.adapters.TimeNotification;
import com.androidvoicememo.db.SQLiteDBHelper;
import com.androidvoicememo.model.Note;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    // указатели на элементы интерфейса
    private Button btn_show_AddNote;
    private ListView listViewNodes;
    private Button btn_Search;
    private Button btn_Search_Clear;
    private ImageButton imageButtonCancelSearch;
    private EditText editText_SearchPhrase;
    // массив имен атрибутов, из которых будут читаться данные
    private String[] from = {SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE,
            SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE};
    // массив ID View-компонентов, в которые будут вставлять данные
    private int[] to = {R.id.textVNode_text, R.id.textVDate };
    // Указатель на выборку заметок из БД
    private Cursor cursor_Notes;
    private Cursor cursor_Notes_Search;
    // Адаптер для добавления заметок в ListView
    private CursorNoteAdapter sAdapterNotes;
    // Константные коды завершения для добавления/удаления заметки
    private static final int ADD_NEW_NOTE = 400;
    private static final int VIEW_NOTE = 401;
    private static final int EXPORT_NOTE = 402;
    // Указательна БД
    protected SQLiteDatabase db;
    private float MOVE_LENGTH = 50;


    @Override
    protected void onResume() {
        super.onResume();
        editText_SearchPhrase.setText(getResources().getText(R.string.main_searchPhrase));
        cursor_Notes = getAllNotes();
        sAdapterNotes.changeCursor(cursor_Notes);
    }


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

        cursor_Notes = getAllNotes();
        /* Подключение к БД - КОНЕЦ */

        //
        listViewNodes = (ListView) findViewById(R.id.listViewNodes);

        /* Обработка события клика на элемент списка */
        listViewNodes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ViewNoteActivity.class);
                Cursor cursor_Note = db.rawQuery("SELECT * FROM " +
                        SQLiteDBHelper.NOTES_TABLE_NAME + " WHERE " +
                        SQLiteDBHelper.NOTES_TABLE_COLUMN_ID + " =" + id, null);
                cursor_Note.moveToNext();
                Note note = new Note(cursor_Note);
                intent.putExtra("note", note);
                startActivityForResult(intent, VIEW_NOTE);
            }
        });

        //deleteNote = (Button) findViewById(R.id.deleteNote);
        //Адаптер на курсор из БД
        sAdapterNotes = new CursorNoteAdapter(this,R.layout.list_notes_activity, cursor_Notes, from, to);
        listViewNodes.setAdapter(sAdapterNotes);

        /* Обработка событий клика */
        btn_show_AddNote = (Button) findViewById(R.id.btn_show_AddNote);
        btn_show_AddNote.setOnClickListener(this);
        imageButtonCancelSearch = (ImageButton) findViewById(R.id.imageButtonCancelSearch);;
        imageButtonCancelSearch.setOnClickListener(this);

        editText_SearchPhrase = (EditText) findViewById(R.id.editText_SearchPhrase);
        editText_SearchPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) v).setText("");
            }
        });

        editText_SearchPhrase.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    EditText eText = (EditText) v;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    if (eText.getText().toString().length() == 0) {
                        eText.setText(getResources().getText(R.string.main_searchPhrase));
                        cursor_Notes = getAllNotes();
                        sAdapterNotes.changeCursor(cursor_Notes);
                    }
                    return true;
                }
                return false;
            }
        });
        editText_SearchPhrase.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String str = s.toString();
                str.toLowerCase();

                cursor_Notes_Search = db.rawQuery("SELECT * FROM " +
                        SQLiteDBHelper.NOTES_TABLE_NAME + " WHERE (lower(" +
                        SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE + ") like '%" + s + "%') ORDER BY " +
                        SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE + " DESC", null);

                sAdapterNotes.changeCursor(cursor_Notes_Search);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            /* Клик по кнопке "Добавить заметку" */
            case R.id.btn_show_AddNote:
                Intent intent = new Intent(this, AddNoteActivity.class);
                startActivityForResult(intent, ADD_NEW_NOTE);
                break;
            case R.id.imageButtonCancelSearch:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                editText_SearchPhrase.setText(getResources().getText(R.string.main_searchPhrase));
                cursor_Notes = getAllNotes();
                sAdapterNotes.changeCursor(cursor_Notes);
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
        Intent intent;
        //события меню
        switch (id) {
            case R.id.app_name_addNote:
                intent = new Intent(this, AddNoteActivity.class);
                startActivityForResult(intent, ADD_NEW_NOTE);
            break;
            case R.id.app_name_reference:
                intent = new Intent(this, ReferenceActivity.class);
                startActivity(intent);
                break;
            /*case R.id.app_name_searchNote:
            break;
            case R.id.app_name_settings:
            break;*/
            case R.id.app_name_export_import:
                intent = new Intent(this, ImportExportActivity.class);
                startActivityForResult(intent, EXPORT_NOTE);
            break;
            /*case R.id.app_exit:
            break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        // Если мы вернулись с окна Создания Заметки, нажав "Сохранить", то сохраняем в БД
        if (requestCode == ADD_NEW_NOTE && resultCode == RESULT_OK) {
            Note new_note = (Note) data.getSerializableExtra("new_note");
            data.removeExtra("new_note");
            if (new_note != null) {
                ContentValues newValues = new ContentValues();
                newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE, new_note.getText_note());
                newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE, new_note.getDate());
                db.insert(SQLiteDBHelper.NOTES_TABLE_NAME, null, newValues);
                cursor_Notes = getAllNotes();
                sAdapterNotes.changeCursor(cursor_Notes);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Заметка сохранена", Toast.LENGTH_SHORT);
                toast.show();
            }
            if (data.getIntExtra("offsetTime",-1) > 0) {
                int offset = data.getIntExtra("offsetTime",-1);
                data.removeExtra("offsetTime");
                cursor_Notes.moveToLast();
                Note note_s = new Note(cursor_Notes);
                restartNotify(note_s,offset);
            }
        } else if (requestCode == VIEW_NOTE && resultCode == RESULT_OK) {
            Note delete_note = (Note) data.getSerializableExtra("delete_note");
            data.removeExtra("delete_note");
            int delCount = db.delete(SQLiteDBHelper.NOTES_TABLE_NAME, "_id = " + delete_note.getId(), null);
            NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(TimeNotification.NOTIFY_TAG,delete_note.getId());
            cursor_Notes = getAllNotes();
            sAdapterNotes.changeCursor(cursor_Notes);
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Запись удалена", Toast.LENGTH_SHORT);
            toast.show();
        } else if (requestCode == EXPORT_NOTE) {
            cursor_Notes = getAllNotes();
            sAdapterNotes.changeCursor(cursor_Notes);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void restartNotify(Note note, int offsetTime) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TimeNotification.class);
        intent.putExtra("note",note);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );
        // На случай, если мы ранее запускали активити, а потом поменяли время,
        // откажемся от уведомления
        am.cancel(pendingIntent);
        // Устанавливаем разовое напоминание
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + offsetTime, pendingIntent);
    }

    private Cursor getAllNotes() {
        return db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTES_TABLE_NAME + " ORDER BY " + SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE
                + " DESC", null);
    }

    @Override
    public void onDestroy(){
        // Очистите все ресурсы. Это касается завершения работы
        // потоков, закрытия соединений с базой данных и т. д.
        if (db.isOpen()) {
            db.close();
        }
        super.onDestroy();
    }


}
