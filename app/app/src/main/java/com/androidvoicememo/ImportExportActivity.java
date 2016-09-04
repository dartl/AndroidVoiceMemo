package com.androidvoicememo;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidvoicememo.adapters.OpenFileDialog;
import com.androidvoicememo.db.SQLiteDBHelper;
import com.my.target.ads.MyTargetView;

import java.io.File;
import java.io.IOException;

/**
 * Created by Dartl on 24.03.2016.
 */
public class ImportExportActivity extends ParentActivity {
    private Button btn_Export;
    private Button btn_SelectFile;
    private Button btn_Import;
    private TextView textView_SelectFile;
    private String fileExportName;
    private EditText editText_SelectFile;
    private SQLiteDBHelper dbHelper;

    private OpenFileDialog fileDialog;
    private String importFileName;
    private Boolean isImport = false;

    private MyTargetView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_export_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarTop_main);
        setSupportActionBar(myToolbar);

        /* Тестовое подключение к БД */
        dbHelper = new SQLiteDBHelper(this);

        try {
            db = dbHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = dbHelper.getReadableDatabase();
        }

        editText_SelectFile = (EditText) findViewById(R.id.editText_SelectFile);
        textView_SelectFile = (TextView) findViewById(R.id.textView_SelectFile);

        btn_Export = (Button) findViewById(R.id.btn_Export);
        btn_SelectFile = (Button) findViewById(R.id.btn_SelectFile);
        btn_Import = (Button) findViewById(R.id.btn_Import);
        btn_Import.setOnClickListener(this);
        btn_SelectFile.setOnClickListener(this);
        btn_Export.setOnClickListener(this);

        btn_Import.setEnabled(false);
        editText_SelectFile.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });

        // Создаем экземпляр MyTargetView
        adView = new MyTargetView(this);

        // Инициализируем экземпляр
        adView.init(SLOT_ID);
        // Добавляем экземпляр в лэйаут главной активности
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.exportImport_layouts);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /* Клик по кнопке "Экспортировать" */
            case R.id.btn_Export:
                fileExportName = editText_SelectFile.getText().toString();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (fileExportName.length() > 0) {
                    File file = new File(Environment.getExternalStorageDirectory()+"/",fileExportName+".json");
                    try {
                        if (file.createNewFile()) {
                            if (dbHelper.exportDB(file,db)) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        getResources().getText(R.string.exportSuccess) + ": " +
                                                file.getAbsolutePath(), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getResources().getText(R.string.exportNameFile), Toast.LENGTH_LONG);
                    toast.show();
                }

                break;
            case R.id.btn_SelectFile:
                fileDialog = new OpenFileDialog(this)
                        .setFilter(".*\\.json")
                        .setOpenDialogListener(new OpenFileDialog.OpenDialogListener() {
                            @Override
                            public void OnSelectedFile(String fileName) {
                                importFileName = fileName;
                                textView_SelectFile.setText(getResources().getText(R.string.selectFile) + " :" + importFileName);
                                btn_Import.setEnabled(true);
                            }
                        });
                fileDialog.show();
                break;
            case R.id.btn_Import:
                if (dbHelper.importDB(new File(importFileName),db)) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getResources().getText(R.string.importSuccess), Toast.LENGTH_SHORT);
                    toast.show();
                    isImport = true;
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getResources().getText(R.string.importNotSuccess), Toast.LENGTH_SHORT);
                    toast.show();
                    isImport = true;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        cancelItem.setVisible(true);
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

