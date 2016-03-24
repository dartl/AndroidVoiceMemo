package com.androidvoicememo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import com.androidvoicememo.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dartl on 03.03.2016.
 */
public class AddNoteActivity extends MainActivity implements
        RecognitionListener {

    public static final String DIRECTORY = "/VoiceNotes/";
    private Chronometer timer;
    private Button btn_addNote_save;
    private Button btn_addNote_cancel;
    private TextView recognizeText;
    private RadioGroup radioGroupRemember;
    private Integer offsetTime;
    private RadioButton radioBtnRemember1;
    private RadioButton radioBtnRemember2;
    private RadioButton radioBtnRemember3;
    private RadioButton radioBtnRemember4;

    /* Пермененые, относящиеся к записи звука */
    private String fileName;
    private String spokenText = "Текст не удалось распознать";

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);

        Toast toast;
        /* Начало записи звука */
        fileName = "unknown";
        if (isSpeechRecognitionActivityPresented(this)) {
            speech = SpeechRecognizer.createSpeechRecognizer(this);
            speech.setRecognitionListener(this);
            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            speech.startListening(recognizerIntent);
        } else {
            toast = Toast.makeText(getApplicationContext(),
                    "У вас не установлен голосовой поиск от Google. Для корректной " +
                            "работы установите его.", Toast.LENGTH_LONG);
            toast.show();
        }

        /* Запускаем таймер */
        timer = (Chronometer) findViewById(R.id.addNote_timer);
        timer.start();

        /* радио групп */
        radioGroupRemember = (RadioGroup) findViewById(R.id.radioGroupRemember);
        radioBtnRemember1 = (RadioButton) findViewById(R.id.radioBtnRemember1);
        radioBtnRemember2= (RadioButton) findViewById(R.id.radioBtnRemember2);
        radioBtnRemember3= (RadioButton) findViewById(R.id.radioBtnRemember3);
        radioBtnRemember4= (RadioButton) findViewById(R.id.radioBtnRemember4);

        radioBtnRemember1.setEnabled(false);
        radioBtnRemember2.setEnabled(false);
        radioBtnRemember3.setEnabled(false);
        radioBtnRemember4.setEnabled(false);

        radioGroupRemember.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtnRemember1:
                        offsetTime = 30 * 60 * 1000;
                        break;
                    case R.id.radioBtnRemember2:
                        offsetTime = 60 * 60 * 1000;
                        break;
                    case R.id.radioBtnRemember3:
                        offsetTime = 12 * 60 * 60 * 1000;
                        break;
                    case R.id.radioBtnRemember4:
                        offsetTime = 24 * 60 * 60 * 1000;
                        break;
                }
            }
        });
        /* Добавляем обработчики кликов */
        btn_addNote_save = (Button) findViewById(R.id.btn_addNote_save);
        btn_addNote_cancel = (Button) findViewById(R.id.btn_addNote_cancel);
        recognizeText = (TextView) findViewById(R.id.recognizeText);
        btn_addNote_save.setOnClickListener(this);
        btn_addNote_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /* Клик по кнопке "Сохранить" */
            case R.id.btn_addNote_save:
                //speech.stopListening();
                long date = System.currentTimeMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy k:mm");
                String dateString = dateFormat.format(date);
                Note note = new Note(-1, fileName, spokenText, dateString);
                Intent intent = new Intent();
                intent.putExtra("new_note", note);
                if (offsetTime != null) {
                    intent.putExtra("offsetTime", offsetTime);
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
            /* Клик по кнопке "Отмена" */
            case R.id.btn_addNote_cancel:
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Произнесите текст заметки", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d("Speech", "onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        timer.stop();
        if (spokenText == null) {
            spokenText = "Текст не удалось распознать";
            recognizeText.setText(spokenText);
        } else {
            radioBtnRemember1.setEnabled(true);
            radioBtnRemember2.setEnabled(true);
            radioBtnRemember3.setEnabled(true);
            radioBtnRemember4.setEnabled(true);
        }
    }

    @Override
    public void onError(int error) {
        String message;
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "Текст не удалось распознать";
                spokenText = "Текст не удалось распознать";
                recognizeText.setText(spokenText);
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }

        Toast toast = Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        spokenText = matches.get(0);
        if (spokenText == null) {
            spokenText = "Текст не удалось распознать";
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Мы распознали текст, сохраните или отмените сохранение заметки", Toast.LENGTH_SHORT);
            toast.show();
        }
        spokenText = spokenText.toLowerCase();
        recognizeText.setText(spokenText);
        speech.cancel();
        speech.destroy();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }


    /**
     * Checks availability of speech recognizing Activity
     *
     * @param callerActivity – Activity that called the checking
     * @return true – if Activity there available, false – if Activity is absent
     */
    private static boolean isSpeechRecognitionActivityPresented(Activity callerActivity) {
        try {
            // getting an instance of package manager
            PackageManager pm = callerActivity.getPackageManager();
            // a list of activities, which can process speech recognition Intent
            List activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

            if (activities.size() != 0) {    // if list not empty
                return true;                // then we can recognize the speech
            }
        } catch (Exception e) {

        }

        return false; // we have no activities to recognize the speech
    }
}
