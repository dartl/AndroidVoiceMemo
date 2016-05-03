package com.androidvoicememo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;


import com.androidvoicememo.adapters.CursorNoteAdapter;
import com.androidvoicememo.db.SQLiteDBHelper;
import com.androidvoicememo.model.Note;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends ParentActivity {

    // указатели на элементы интерфейса
    private ListView listViewNodes;
    // массив имен атрибутов, из которых будут читаться данные
    private String[] from = {SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE,
            SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE};
    // массив ID View-компонентов, в которые будут вставлять данные
    private int[] to = {R.id.textVNode_text, R.id.textVDate };

    /* реклама */
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarTop);
        setSupportActionBar(myToolbar);

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



        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        mAdView = (AdView) findViewById(R.id.ad_view);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }


    @Override
    public void onDestroy(){
        // Очистите все ресурсы. Это касается завершения работы
        // потоков, закрытия соединений с базой данных и т. д.
        if (db.isOpen()) {
            db.close();
        }
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cursor_Notes = getAllNotes();
        sAdapterNotes.changeCursor(cursor_Notes);
        if (mAdView != null) {
            mAdView.resume();
        }

    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        searchItem.setVisible(true);
        searchItemAdd.setVisible(true);
        return true;
    }

}
