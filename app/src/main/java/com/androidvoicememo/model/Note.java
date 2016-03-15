package com.androidvoicememo.model;

import android.database.Cursor;

import com.androidvoicememo.db.SQLiteDBHelper;

import java.io.Serializable;

/**
 * Created by Dartl on 03.03.2016.
 */
public class Note implements Serializable {
    private int id;
    private String path_file;
    private String text_note;
    private String date;

    public Note() {
        this.id = -1;
        this.path_file = null;
        this.text_note = null;
        this.date = null;
    }

    public Note(int _id, String _path_file, String _text_note, String _date) {
        this.id = _id;
        this.path_file = _path_file;
        this.text_note = _text_note;
        this.date = _date;
    }

    public Note(Cursor elem) {
        this.id = elem.getInt(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_ID));
        this.path_file = elem.getString(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_FILE));
        this.text_note = elem.getString(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE));
        this.date = elem.getString(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE));
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath_file() {
        return path_file;
    }

    public void setPath_file(String path_file) {
        this.path_file = path_file;
    }

    public String getText_note() {
        return text_note;
    }

    public void setText_note(String text_note) {
        this.text_note = text_note;
    }
}
