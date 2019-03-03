package com.is2all.challenges.Dialogs;

import android.app.TimePickerDialog;
import android.content.Context;

import com.is2all.challenges.R;

public class DialogTimePicker extends TimePickerDialog {
    public DialogTimePicker(Context context, TimePickerDialog.OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, listener, hourOfDay, minute, is24HourView);
    }

    @Override
    public void onStart() {
        super.onStart();
        getWindow().setWindowAnimations(R.style.MyAnimation_Window);

    }
}
