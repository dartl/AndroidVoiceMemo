package com.androidvoicememo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.androidvoicememo.model.Note;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Dartl on 03.03.2016.
 */
public class AddNoteActivity extends MainActivity {

    public static final String DIRECTORY = "/VoiceNotes/";
    private Chronometer timer;
    private Button btn_addNote_save;
    private Button btn_addNote_cancel;

    /* Пермененые, относящиеся к записи звука */
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String fileName;
    private String spokenText = "Текст не удалось распознать";

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

        /* Начало записи звука */

        fileName = Environment.getExternalStorageDirectory() + DIRECTORY;
        new File(fileName ).mkdirs();
        fileName += "/record.3gpp";
        try {
            releaseRecorder();

            File outFile = new File(fileName);
            if (outFile.exists()) {
                outFile.delete();
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setOutputFile(fileName);

            mediaRecorder.prepare();
            mediaRecorder.start();
            displaySpeechRecognizer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        releaseRecorder();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /* Клик по кнопке "Сохранить" */
            case R.id.btn_addNote_save:
                long date = System.currentTimeMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy k:mm");
                String dateString = dateFormat.format(date);
                Note note = new Note(-1, fileName, spokenText, dateString);
                Intent intent = new Intent();
                intent.putExtra("new_note", note);

                setResult(RESULT_OK, intent);
                finish();
                break;
            /* Клик по кнопке "Отмена" */
            case R.id.btn_addNote_cancel:
                if (mediaRecorder != null) {
                    mediaRecorder.stop();
                }
                finish();
                break;
            default:
                break;
        }
    }

    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {

        PackageManager pm = this.getPackageManager();
        List activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (activities.size() != 0) {	// если список не пустой
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Скажите заметку");
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    getClass().getPackage().getName());
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
// Start the activity, the intent will be populated with the speech text
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } else {
            installGoogleVoiceSearch(this);
        }
    }

    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        timer.stop();
        if (mediaRecorder != null) {
            mediaRecorder.stop();
        }
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            spokenText = results.get(0);
            // Do something with spokenText
            TextView recognizeText = (TextView) findViewById(R.id.recognizeText);
            recognizeText.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static void installGoogleVoiceSearch(final Activity ownerActivity) {

        // создаем диалог, который спросит у пользователя хочет ли он
        // установить Голосовой Поиск
        Dialog dialog = new AlertDialog.Builder(ownerActivity)
                .setMessage("Для распознавания речи необходимо установить \"Голосовой поиск Google\"")	// сообщение
                .setTitle("Внимание")	// заголовок диалога
                .setPositiveButton("Установить", new DialogInterface.OnClickListener() {    // положительная кнопка

                    // обработчик нажатия на кнопку Установить
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            // создаем Intent для открытия в маркете странички с приложением
                            // Голосовой Поиск имя пакета: com.google.android.voicesearch
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.voicesearch"));
                            // настраиваем флаги, чтобы маркет не попал к в историю нашего приложения (стек Activity)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            // отправляем Intent
                            ownerActivity.startActivity(intent);
                        } catch (Exception ex) {
                            // не удалось открыть маркет
                            // например из-за того что он не установлен
                            // ничего не поделаешь
                        }
                    }
                })

                .setNegativeButton("Отмена", null)	// негативная кнопка
                .create();

        dialog.show();	// показываем диалог
    }
}
