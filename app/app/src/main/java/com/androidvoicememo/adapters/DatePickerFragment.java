package com.androidvoicememo.adapters;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidvoicememo.AddNoteActivity;
import com.androidvoicememo.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dartl on 15.04.2016.
 */
public class DatePickerFragment extends DialogFragment{
    private TimePicker tp;
    private DatePicker dp;
    private Button button_OK;
    private Button button_CANCEL;
    private Dialog dialog;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();

        dialog = new Dialog(getActivity());

        dialog.setTitle("Установите время напоминания");
        dialog.setContentView(R.layout.date_and_time_dialog);
        tp = (TimePicker) dialog.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        dp = (DatePicker) dialog.findViewById(R.id.datePicker);
        tp.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));

        button_OK = (Button) dialog.findViewById(R.id.button_OK);
        button_CANCEL = (Button) dialog.findViewById(R.id.button_CANCEL);
        button_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNoteActivity callingActivity = (AddNoteActivity) getActivity();
                callingActivity.cancelDateAndTime();
                Log.d("IMPORTANT", "Нажали отмена");
                dialog.hide();
            }
        });
        button_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNoteActivity callingActivity = (AddNoteActivity) getActivity();
                Date date = new Date(dp.getYear() - 1900, dp.getMonth(), dp.getDayOfMonth(),
                        tp.getCurrentHour(), tp.getCurrentMinute());
                Date now = new Date();
                long value = date.getTime() - now.getTime();
                if (value > 0) {
                    callingActivity.setDateAndTime(date.getTime());
                    dialog.hide();
                } else {
                    Toast toast = Toast.makeText(callingActivity,
                            getResources().getText(R.string.head_select_date), Toast.LENGTH_SHORT);
                    toast.show();
                }

                Log.d("IMPORTANT", "Нажали ОК");

                //dialog.cancel();
            }
        });

        // Create a new instance of DatePickerDialog and return it
        return dialog;
    }
}