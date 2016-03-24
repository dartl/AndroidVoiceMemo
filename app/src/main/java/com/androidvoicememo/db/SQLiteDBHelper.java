package com.androidvoicememo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.JsonReader;

import com.androidvoicememo.model.Note;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by Dartl on 03.03.2016.
 */
public class SQLiteDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "VOICE_NOTES.DB";
    public static final String NOTES_TABLE_NAME = "NOTES";
    public static final String NOTES_TABLE_COLUMN_ID = "_id";
    public static final String NOTES_TABLE_COLUMN_TEXT_NOTE = "TEXT_NOTE";
    public static final String NOTES_TABLE_COLUMN_DATE = "DATE";


    public SQLiteDBHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(
                "create table " + NOTES_TABLE_NAME +
                        "(" + NOTES_TABLE_COLUMN_ID + " integer primary key, " +
                        NOTES_TABLE_COLUMN_TEXT_NOTE + " text, " + NOTES_TABLE_COLUMN_DATE + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
        onCreate(db);
    }

    public boolean importDB(File file, SQLiteDatabase db) {
        String json = "";
        if (file.canRead()) {
            try {
                FileInputStream fin = new FileInputStream(file);
                json = convertStreamToString(fin);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONObject jreader = new JSONObject(json);
                if (jreader.toString().length() >= 1) {
                    JSONObject objNote;
                    String textNote;
                    String date;
                    JSONArray arrayNames = jreader.names();
                    JSONArray array = jreader.toJSONArray(arrayNames);
                    for (int  i = 0; i < array.length(); i++) {
                        objNote=(JSONObject)array.get(i);
                        textNote = objNote.getString(NOTES_TABLE_COLUMN_TEXT_NOTE);
                        date = objNote.getString(NOTES_TABLE_COLUMN_DATE);
                        ContentValues newValues = new ContentValues();
                        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE, textNote);
                        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE, date);
                        db.insert(SQLiteDBHelper.NOTES_TABLE_NAME, null, newValues);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public boolean exportDB(File file, SQLiteDatabase db) {
        JSONObject obj;
        JSONObject resultJson = new JSONObject();
        Note note;
        Cursor cursorObj = db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTES_TABLE_NAME + " ORDER BY " + SQLiteDBHelper.NOTES_TABLE_COLUMN_ID
                + " DESC", null);
        while (cursorObj.moveToNext()) {
            note = new Note(cursorObj);
            obj = new JSONObject();
            try {
                obj.put(NOTES_TABLE_COLUMN_TEXT_NOTE, note.getText_note());
                obj.put(NOTES_TABLE_COLUMN_DATE, note.getDate());
                resultJson.put(String.valueOf(note.getId()),obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (file.canWrite()) {
            try {
                PrintWriter printWriter = new PrintWriter(file);
                printWriter.println(resultJson.toString());
                printWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

}
