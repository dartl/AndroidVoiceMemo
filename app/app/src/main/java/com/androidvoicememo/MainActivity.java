package com.androidvoicememo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;


import com.androidvoicememo.adapters.CursorNoteAdapter;
import com.androidvoicememo.db.SQLiteDBHelper;
import com.androidvoicememo.model.Note;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.my.target.ads.MyTargetView;


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
    private MyTargetView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarTop_main);
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

        // Создаем экземпляр MyTargetView
        adView = new MyTargetView(this);

        // Инициализируем экземпляр
        adView.init(SLOT_ID);
        // Добавляем экземпляр в лэйаут главной активности
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relativeLayout);
        RelativeLayout.LayoutParams adViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        adViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        adViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout.addView(adView, adViewLayoutParams);

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
        /*
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
        mAdView.loadAd(adRequest);*/
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
        if (adView != null) adView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (adView != null) adView.resume();
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
        if (adView != null) adView.pause();
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
        addItem.setVisible(true);
        return true;
    }

}
