package com.androidvoicememo;

import android.annotation.TargetApi;
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
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.androidvoicememo.adapters.DatePickerFragment;
import com.androidvoicememo.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dartl on 03.03.2016.
 */
public class AddNoteActivity extends ParentActivity {

    private Button btn_addNote_save;
    private EditText recognizeText;
    private RadioGroup radioGroupRemember;
    private long offsetTime = -1;
    private long currentTimeOffset = -1;
    private RadioButton radioBtnRemember1;
    private RadioButton radioBtnRemember2;
    private RadioButton radioBtnRemember3;
    private RadioButton radioBtnRemember4;
    private RadioButton radioBtnRemember5;
    private ToggleButton toggleButton_Vibration;
    private ToggleButton toggleButton_Voice;

    /* Пермененые, относящиеся к записи звука */
    private String spokenText;
    AlertDialog aDialog;
    DialogFragment newFragment;

    private boolean vibration;
    private boolean voice;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);

        spokenText = (String) getResources().getText(R.string.textNotRecognize);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarTop_main);
        setSupportActionBar(myToolbar);

        /* Начало записи звука */

        /* Инициализируем окно для ошибки, чтобы не было NullPointerExeption */
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        aDialog = dialog.create();
        textRecognizer();
        /* радио групп */
        radioGroupRemember = (RadioGroup) findViewById(R.id.radioGroupRemember);
        radioBtnRemember2 = (RadioButton) findViewById(R.id.radioBtnRemember2);
        radioBtnRemember3 = (RadioButton) findViewById(R.id.radioBtnRemember3);
        radioBtnRemember4 = (RadioButton) findViewById(R.id.radioBtnRemember4);
        radioBtnRemember5 = (RadioButton) findViewById(R.id.radioBtnRemember5);

        radioGroupRemember.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtnRemember2:
                        currentTimeOffset = 1;
                        offsetTime = 60 * 60 * 1000;
                        break;
                    case R.id.radioBtnRemember3:
                        currentTimeOffset = 1;
                        offsetTime = 12 * 60 * 60 * 1000;
                        break;
                    case R.id.radioBtnRemember4:
                        currentTimeOffset = 1;
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
        recognizeText = (EditText) findViewById(R.id.recognizeText);
        btn_addNote_save.setOnClickListener(this);
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

        // кнопки toggle
        toggleButton_Vibration = (ToggleButton) findViewById(R.id.toggleButton_Vibration);
        toggleButton_Voice = (ToggleButton) findViewById(R.id.toggleButton_Voice);

        toggleButton_Vibration.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vibration = isChecked;
            }
        });

        toggleButton_Voice.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                voice = isChecked;
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
                //date -= 864000000;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm");

                String dateString = dateFormat.format(date);
                Note note = new Note(-1, "", spokenText, dateString);
                Intent intent = new Intent();
                intent.putExtra("new_note", note);
                if (currentTimeOffset > 0) {
                    Date now = new Date();
                    offsetTime = currentTimeOffset - now.getTime();
                }
                if (offsetTime > 0) {
                    Log.d("IMPORTANTT","Class AddNoteActivity, line number - 196: " + String.valueOf(offsetTime));
                    intent.putExtra("offsetTime", offsetTime);
                    intent.putExtra("voice",voice);
                    intent.putExtra("vibration",vibration);
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * Checks availability of speech recognizing Activity
     *
     * @param callerActivity – Activity that called the checking
     * @return true – if Activity there available, false – if Activity is absent
     */
    private boolean isSpeechRecognitionActivityPresented(Activity callerActivity) {
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

    private void installGoogleVoiceSearch(final Activity ownerActivity) {

        // создаем диалог, который спросит у пользователя хочет ли он
        // установить Голосовой Поиск
        Dialog dialog = new AlertDialog.Builder(ownerActivity)
                .setMessage(getResources().getText(R.string.messageNeedInstall))	// сообщение
                .setTitle(getResources().getText(R.string.attention))	// заголовок диалога
                .setPositiveButton(getResources().getText(R.string.install), new DialogInterface.OnClickListener() {    // положительная кнопка

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

                .setNegativeButton(getResources().getText(R.string.cancel), null)	// негативная кнопка
                .create();

        dialog.show();	// показываем диалог
    }

    public boolean hasConnection(final Context context)
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void textRecognizer() {
            if (isSpeechRecognitionActivityPresented(this) && hasConnection(this)) {
                // создаем Intent с действием RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                // добавляем дополнительные параметры:
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getText(R.string.addNote_sayFullText));	// текстовая подсказка пользователю
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);	// модель распознавания оптимальная для распознавания коротких фраз-поисковых запросов
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);	// количество результатов, которое мы хотим получить, в данном случае хотим только первый - самый релевантный

                // стартуем Activity и ждем от нее результата
                this.startActivityForResult(intent, 101);
            } else {
                if (hasConnection(this)) {
                    installGoogleVoiceSearch(AddNoteActivity.this);
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.
                            setMessage(getResources().getText(R.string.errorConnectionInternet)).
                            setTitle(getResources().getText(R.string.attention));
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setPositiveButton(getResources().getText(R.string.create_Note_NoInternet), new DialogInterface.OnClickListener() {    // положительная кнопка

                        // обработчик нажатия на кнопку Установить
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
    }

    /* Функция для получения данных о дате заметки */
    public void setDateAndTime(long value) {
        Date now = new Date();
        Date date = new Date(value);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy k:mm");
        String dateString = dateFormat.format(date);
        currentTimeOffset = value;
        //offsetTime = value - now.getTime();
        radioBtnRemember5.setText(dateString);
    }

    public void cancelDateAndTime() {
        radioBtnRemember5.setText(R.string.addNote_remember_custom);
        radioBtnRemember4.setChecked(true);
        currentTimeOffset = -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        cancelItem.setVisible(true);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // если это результаты распознавания речи
        // и процесс распознавания прошел успешно
        if (requestCode == 101 && resultCode == RESULT_OK) {

            // получаем список текстовых строк - результат распознавания
            // строк может быть несколько, так как не всегда удается точно распознать речь
            // более релевантные результаты идут в начале списка
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            recognizeText.setText(matches.get(0).toString());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
