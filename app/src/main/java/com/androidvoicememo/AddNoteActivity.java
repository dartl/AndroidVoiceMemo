package com.androidvoicememo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;

/**
 * Created by Dartl on 03.03.2016.
 */
public class AddNoteActivity extends MainActivity {
    private Chronometer timer;
    private Button btn_addNote_save;
    private Button btn_addNote_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);

        /* Запускаем таймер */
        timer = (Chronometer) findViewById(R.id.addNote_timer);
        timer.start();

        /* Добавляем обработчики кликов */
        btn_addNote_save = (Button) findViewById(R.id.btn_addNote_save);
        btn_addNote_cancel = (Button) findViewById(R.id.btn_addNote_cancel);
        btn_addNote_save.setOnClickListener(this);
        btn_addNote_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /* Клик по кнопке "Сохранить" */
            case R.id.btn_addNote_save:
                break;
            /* Клик по кнопке "Отмена" */
            case R.id.btn_addNote_cancel:
                this.finish();
                break;
            default:
                break;
        }
    }
}
