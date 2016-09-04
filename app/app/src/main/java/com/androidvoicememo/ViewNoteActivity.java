package com.androidvoicememo;

import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.androidvoicememo.db.SQLiteDBHelper;
import com.androidvoicememo.model.Note;
import com.my.target.ads.MyTargetView;

/**
 * Created by Dartl on 04.03.2016.
 */
public class ViewNoteActivity extends ParentActivity {
    private TextView viewNote_textVDate;
    private TextView viewNote_textVText;
    private Button btn_copyText;
    private Note note;
    private MyTargetView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarTop_main);
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

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Создаем экземпляр MyTargetView
        adView = (MyTargetView) findViewById(R.id.adMyView);

        // Инициализируем экземпляр
        adView.init(SLOT_ID);

        // Устанавливаем слушатель событий
        adView.setListener(new MyTargetView.MyTargetViewListener()
        {
            @Override
            public void onLoad(MyTargetView myTargetView)
            {
                // Данные успешно загружены, запускаем показ объявлений
                myTargetView.start();
            }

            @Override
            public void onNoAd(String reason, MyTargetView myTargetView)
            {
            }

            @Override
            public void onClick(MyTargetView myTargetView)
            {
            }
        });

        adView.load();
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
        saveNote.setVisible(true);

        //exportImportItem.setVisible(false);
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
            case R.id.action_saveNote:
                saveNote(note, db);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean saveNote(Note note, SQLiteDatabase db)  {
        ContentValues newValues = new ContentValues();
        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE, viewNote_textVText.getText().toString());
        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE, note.getDate());
        db.execSQL("UPDATE " +SQLiteDBHelper.NOTES_TABLE_NAME+ " SET "+ SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE
                +" = '" +viewNote_textVText.getText().toString()+ "' WHERE "+SQLiteDBHelper.NOTES_TABLE_COLUMN_ID
                +" = "+note.getId()+";");
        return true;
    }

    @Override
    public void onDestroy(){
        if (adView != null) adView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (adView != null) adView.resume();
        super.onResume();
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) adView.pause();
        super.onPause();
    }
}