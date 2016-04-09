package com.androidvoicememo;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidvoicememo.adapters.OpenFileDialog;
import com.androidvoicememo.db.SQLiteDBHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by Dartl on 24.03.2016.
 */
public class ImportExportActivity extends MainActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_export_activity);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /* Клик по кнопке "Экспортировать" */
            case R.id.btn_Export:
                fileExportName = editText_SelectFile.getText().toString();
                File file = new File(Environment.getExternalStorageDirectory()+"/",fileExportName+".json");
                try {
                    if (file.createNewFile()) {
                        if (dbHelper.exportDB(file,db)) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Экспорт успешен. Файл находится вот тут: " + file.getAbsolutePath(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_SelectFile:
                fileDialog = new OpenFileDialog(this)
                        .setFilter(".*\\.json")
                        .setOpenDialogListener(new OpenFileDialog.OpenDialogListener() {
                            @Override
                            public void OnSelectedFile(String fileName) {
                                importFileName = fileName;
                                textView_SelectFile.setText("Был выбран файл :" + importFileName);
                                btn_Import.setEnabled(true);
                                //Toast.makeText(getApplicationContext(), fileName, Toast.LENGTH_LONG).show();
                            }
                        });
                fileDialog.show();
                break;
            case R.id.btn_Import:
                if (dbHelper.importDB(new File(importFileName),db)) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Импорт произошел успешно", Toast.LENGTH_SHORT);
                    toast.show();
                    isImport = true;
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Импорт не удалось произвести, не корректная структура файла", Toast.LENGTH_SHORT);
                    toast.show();
                    isImport = true;
                }
                break;
            default:
                break;
        }
    }

}

