package com.androidvoicememo.adapters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.androidvoicememo.R;
import com.androidvoicememo.ViewNoteActivity;
import com.androidvoicememo.model.Note;

/**
 * Created by Dartl on 19.03.2016.
 */
public class TimeNotification extends BroadcastReceiver {
    private static final int NOTIFY_CODE = 101;
    private static final String NOTIFY_TAG = "VOICE_NOTE";

    NotificationManager nm;
    @Override
    public void onReceive(Context context, Intent intent) {
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Интент для активити, которую мы хотим запускать при нажатии на уведомление
        Intent intentTL = new Intent(context, ViewNoteActivity.class);
        Note note = (Note) intent.getSerializableExtra("note");
        intentTL.putExtra("note", note);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon175x175)
                .setAutoCancel(true) //уведомление закроется по клику на него
                .setTicker("Уведомление о заметке") //текст, который отобразится вверху статус-бара при создании уведомления
                .setContentText(note.getText_note()) // Основной текст уведомления
                .setContentIntent(PendingIntent.getActivity(context, 0, intentTL, PendingIntent.FLAG_CANCEL_CURRENT))
                .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                .setContentTitle("Уведомление о заметке") //заголовок уведомления
                .setDefaults(Notification.DEFAULT_VIBRATE); // звук, вибро и диодный индикатор выставляются по умолчанию
        nm.notify(NOTIFY_TAG, note.getId(), nb.build());
    }
}
