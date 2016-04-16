package com.androidvoicememo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidvoicememo.adapters.DatePickerFragment;
import com.androidvoicememo.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dartl on 03.03.2016.
 */
public class AddNoteActivity extends MainActivity implements
        RecognitionListener {

    private Button btn_addNote_save;
    private Button btn_addNote_cancel;
    private EditText recognizeText;
    private RadioGroup radioGroupRemember;
    private long offsetTime = -1;
    private RadioButton radioBtnRemember1;
    private RadioButton radioBtnRemember2;
    private RadioButton radioBtnRemember3;
    private RadioButton radioBtnRemember4;
    private RadioButton radioBtnRemember5;

    /* Пермененые, относящиеся к записи звука */
    private String spokenText = "Текст не удалось распознать";
    AlertDialog aDialog;
    DialogFragment newFragment;

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);

        /* Начало записи звука */

        /* Инициализируем окно для ошибки, чтобы не было NullPointerExeption */
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        aDialog = dialog.create();

        textRecognizer();

        /* радио групп */
        radioGroupRemember = (RadioGroup) findViewById(R.id.radioGroupRemember);
        radioBtnRemember1 = (RadioButton) findViewById(R.id.radioBtnRemember1);
        radioBtnRemember2= (RadioButton) findViewById(R.id.radioBtnRemember2);
        radioBtnRemember3= (RadioButton) findViewById(R.id.radioBtnRemember3);
        radioBtnRemember4= (RadioButton) findViewById(R.id.radioBtnRemember4);
        radioBtnRemember5= (RadioButton) findViewById(R.id.radioBtnRemember5);

        radioBtnRemember1.setEnabled(false);
        radioBtnRemember2.setEnabled(false);
        radioBtnRemember3.setEnabled(false);
        radioBtnRemember4.setEnabled(false);
        radioBtnRemember5.setEnabled(false);

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
                        offsetTime = -1;
                        break;
                    case R.id.radioBtnRemember5:
                        if (newFragment != null) {
                            newFragment.onStart();
                        } else {
                            newFragment = new DatePickerFragment();
                            newFragment.show(getFragmentManager(), "datePicker");
                        }
                        break;
                }
            }
        });
        /* Добавляем обработчики кликов */
        btn_addNote_save = (Button) findViewById(R.id.btn_addNote_save);
        btn_addNote_cancel = (Button) findViewById(R.id.btn_addNote_cancel);
        recognizeText = (EditText) findViewById(R.id.recognizeText);
        btn_addNote_save.setOnClickListener(this);
        btn_addNote_cancel.setOnClickListener(this);
        recognizeText.addTextChangedListener(new TextWatcher() {
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
                spokenText = str;
            }
        });
        
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
                Note note = new Note(-1, "", spokenText, dateString);
                Intent intent = new Intent();
                intent.putExtra("new_note", note);
                if (offsetTime > 0) {
                    intent.putExtra("offsetTime", offsetTime);
                    Log.d("IMPORANT", "Class AddNoteActivity, line number - 159" +
                            String.valueOf(offsetTime));
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
        recognizeText.setText(getResources().getText(R.string.addNote_recognitionText));
    }

    @Override
    public void onBeginningOfSpeech() {

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
        if (spokenText == null) {
            spokenText = "Текст не удалось распознать";
            recognizeText.setText(spokenText);
        } else {
            radioBtnRemember1.setEnabled(true);
            radioBtnRemember2.setEnabled(true);
            radioBtnRemember3.setEnabled(true);
            radioBtnRemember4.setEnabled(true);
            radioBtnRemember5.setEnabled(true);
        }
    }

    @Override
    public void onError(int error) {
        String message;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Ошибка записи звука с микрофона";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Ошибка подключения к интернету. Проверьте подключение к интернету и попробуйте снова.";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "Текст не удалось распознать";
                spokenText = "Текст не удалось распознать";
                recognizeText.setText(spokenText);
                dialog.setPositiveButton("Попробовать снова", new DialogInterface.OnClickListener() {    // положительная кнопка

                    // обработчик нажатия на кнопку Установить
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textRecognizer();
                    }
                });
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "Сервер распознования речи на данный момент занят, попробуйте пойзже.";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "Ошибка на стороне сервера. Проверьте подключение к интернету и попробуйте снова.";
                break;
            default:
                message = "Не удалось распознать текст";
                break;
        }

        dialog.setMessage(message)	// сообщение
                .setTitle("Внимание");
        if(!aDialog.isShowing()) {
            aDialog = dialog.show();
        }

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
        /* включаем неактивные кнопку Сохранить и распознанный текст */
        recognizeText.setEnabled(true);
        btn_addNote_save.setEnabled(true);
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

    private static void installGoogleVoiceSearch(final Activity ownerActivity) {

        // создаем диалог, который спросит у пользователя хочет ли он
        // установить Голосовой Поиск
        Dialog dialog = new AlertDialog.Builder(ownerActivity)
                .setMessage("Для распознавания речи необходимо установить приложение \"Google\" с функцией голосового поиска.")	// сообщение
                .setTitle("Внимание")	// заголовок диалога
                .setPositiveButton("Установить", new DialogInterface.OnClickListener() {    // положительная кнопка

                    // обработчик нажатия на кнопку Установить
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            // создаем Intent для открытия в маркете странички с приложением
                            // Голосовой Поиск имя пакета: com.google.android.voicesearch
                            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.googlequicksearchbox"));
                            // настраиваем флаги, чтобы маркет не попал к в историю нашего приложения (стек Activity)
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            // отправляем Intent
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=com.google.android.googlequicksearchbox"));
                            ownerActivity.startActivity(intent);
                        } catch (Exception ex) {

                        }
                    }
                })

                .setNegativeButton("Отмена", null)	// негативная кнопка
                .create();

        dialog.show();	// показываем диалог
    }

    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    private void textRecognizer() {
        if (isSpeechRecognitionActivityPresented(this)) {
            if (speech == null) {
                speech = SpeechRecognizer.createSpeechRecognizer(this);
                speech.setRecognitionListener(this);
                recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                speech.startListening(recognizerIntent);
            } else {
                speech.startListening(recognizerIntent);
            }
        } else {
            if (hasConnection(this)) {
                installGoogleVoiceSearch(AddNoteActivity.this);
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.
                        setMessage("У вас отсутствует подключение к интернету, включите его и попробуйте снова.").
                        setTitle("Внимание");
            }
        }
    }

    /* Функция для получения данных о дате заметки */
    public void setDateAndTime(long value) {
        Date now = new Date();
        Date date = new Date(value);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy k:mm");
        String dateString = dateFormat.format(date);
        offsetTime = value - now.getTime();
        radioBtnRemember5.setText(String.valueOf(offsetTime));
    }

    public void cancelDateAndTime() {
        radioBtnRemember5.setText(R.string.addNote_remember_custom);
        radioBtnRemember1.setChecked(true);
        offsetTime = -1;
    }

}
