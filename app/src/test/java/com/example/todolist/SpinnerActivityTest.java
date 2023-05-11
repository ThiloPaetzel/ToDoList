package com.example.todolist;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.AdapterView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SpinnerActivityTest {

    @Test
    public void testOnItemSelected(){
        SpinnerActivity spinnerActivity = new SpinnerActivity();

        AdapterView adapterView = mock(AdapterView.class);
        View view = mock(View.class);
        int position = 1;
        long id = 2L;
        String selectedValue = "Selected Value";

        when(adapterView.getItemAtPosition(position)).thenReturn(selectedValue);

        spinnerActivity.onItemSelected(adapterView, view, position, id);

        assertEquals(selectedValue, SpinnerSelection.selectedValue);
    }
}
