package com.example.todolist;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;

public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener{
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedValue = parent.getItemAtPosition(position).toString();
        SpinnerSelection.selectedValue = selectedValue;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Nothing for now
    }
}
