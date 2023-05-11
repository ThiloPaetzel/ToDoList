package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.todolist.Adapter.ToDoAdapter;
import com.example.todolist.Model.ToDoModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TaskDetailActivity extends AppCompatActivity {

    private ListView tasksList;
    private TextView datePicked;
    private String dateClicked;
    private ToDoAdapter adapter;
    private List<ToDoModel> mList;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        tasksList = findViewById(R.id.taskList);
        datePicked = findViewById(R.id.datePicked);


        List<String> tagStrings = new ArrayList<>();
        List<String> taskStrings = new ArrayList<>();
        for (ToDoModel task : CalendarListContainer.taskList){
            taskStrings.add(task.getTask());
            tagStrings.add(task.getTag());
        }

        dateClicked = getIntent().getStringExtra("date clicked");
        //Conversion de la date en string en date en format dd/MM/yyyy
        DateFormat sourceFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        try {
            Date date = sourceFormat.parse(dateClicked);
            DateFormat targetFormat = new SimpleDateFormat("EEE/dd/MM/yyyy", Locale.ENGLISH);
            String formattedDate = targetFormat.format(date);
            datePicked.setText(formattedDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 0; i< taskStrings.size(); i++) {
            Map<String, String> map = new HashMap<>(2);
            map.put("task", taskStrings.get(i));
            map.put("tag", tagStrings.get(i));
            data.add(map);
        }

        SimpleAdapter listAdapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2, new String[]{"task", "tag"}, new int[]{android.R.id.text1, android.R.id.text2});

        tasksList.setAdapter(listAdapter);
    }
}