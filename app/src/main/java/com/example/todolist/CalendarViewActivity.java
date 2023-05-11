package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.example.todolist.Model.ToDoModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarViewActivity extends AppCompatActivity {
    //private List<Task> tasks;
    MaterialCalendarView calendarView;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        calendarView = findViewById(R.id.calendarView);
        firestore = FirebaseFirestore.getInstance();


        firestore.collection("user_tasks").document(SpinnerSelection.selectedValue).collection("task").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<Date> taskDates = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String dueDate = document.getString("due");
                        if (dueDate != null) {
                            try {
                                Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dueDate);
                                taskDates.add(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    showTaskDates(taskDates);
                } else {
                    Toast.makeText(CalendarViewActivity.this, "Error getting tasks: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                List<ToDoModel> tasksForDate = getTasksForDate(date.getDate(), new OnTasksLoadedListenner() {
                    @Override
                    public void onTasksLoaded(List<ToDoModel> tasksForDate) {
                        if (tasksForDate.isEmpty()){
                            Toast.makeText(CalendarViewActivity.this, "List is empty : " + tasksForDate.size(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(CalendarViewActivity.this, "Number in List : " + tasksForDate.size(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CalendarViewActivity.this, TaskDetailActivity.class);
                        intent.putExtra("date clicked", date.getDate().toString());
                        CalendarListContainer.taskList = tasksForDate;
                        startActivity(intent);
                        //intent.putExtra("task", tasksForDate.get(0).getTask());
                        //intent.putExtra("tag", tasksForDate.get(0).getTag());
                        //ArrayList<ToDoModel> tasksForDateArrayList = new ArrayList<>(tasksForDate);
                        //intent.putParcelableArrayListExtra("task", tasksForDateArrayList);
                        //CalendarListContainer calendarListContainer = (CalendarListContainer) getApplication();
                        //calendarListContainer.setTasks(tasksForDate);

                    }
                });
            }
        });
    }

    private List<ToDoModel> getTasksForDate(Date date, OnTasksLoadedListenner listener){
        List<ToDoModel> allTasks = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        firestore.collection("user_tasks").document(SpinnerSelection.selectedValue).collection("task").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String dueDate = document.getString("due");
                        if (dueDate != null ){
                            try {
                                Date taskDate = new SimpleDateFormat("dd/MM/yyyy").parse(dueDate);
                                Calendar taskCalendar = Calendar.getInstance();
                                taskCalendar.setTime(taskDate);

                                if (calendar.get(Calendar.YEAR) == taskCalendar.get(Calendar.YEAR) &&
                                        calendar.get(Calendar.MONTH) == taskCalendar.get(Calendar.MONTH) &&
                                        calendar.get(Calendar.DAY_OF_MONTH) == taskCalendar.get(Calendar.DAY_OF_MONTH)) {
                                    ToDoModel taskToList = document.toObject(ToDoModel.class);
                                    allTasks.add(taskToList);
                                    Toast.makeText(CalendarViewActivity.this, "Date corresponding : " + allTasks.get(0).getTask(), Toast.LENGTH_LONG).show();
                                } else {
                                    //Toast.makeText(CalendarViewActivity.this, "Date not equal : " +"Date in calendar " + calendar.getTime() + "Date in database " + taskCalendar.getTime(), Toast.LENGTH_LONG).show();
                                }
                            } catch (ParseException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    listener.onTasksLoaded(allTasks); // Appel de la méthode onTasksLoaded() avec la liste des tâches
                } else {
                    Toast.makeText(CalendarViewActivity.this, "Error getting tasks : " + task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        return allTasks;
    }



    private void showTaskDates(List<Date> taskDates){
        Log.d("CalendarViewActivity", "showTaskDates: " + taskDates.toString());

        //tasks = new ArrayList<>();

        // Créez une map pour stocker les décorateurs pour chaque date
        Map<CalendarDay, DayViewDecorator> decoratorMap = new HashMap<>();

        // Parcourez les dates de tâches et créez un décorateur pour chaque date
        for (Date taskDate : taskDates){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(taskDate);
            CalendarDay day = CalendarDay.from(calendar);

            // Vérifiez si un décorateur a déjà été créé pour cette date
            DayViewDecorator decorator = decoratorMap.get(day);
            if (decorator == null) {
                // Créez un nouveau décorateur si aucun n'a été créé pour cette date
                decorator = new DayViewDecorator() {
                    @Override
                    public boolean shouldDecorate(CalendarDay day) {
                        return day.equals(CalendarDay.from(calendar));
                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                        view.addSpan(new DotSpan(20, Color.RED));
                    }
                };
                // Ajoutez le décorateur à la map
                decoratorMap.put(day, decorator);
            }
        }

        // Ajoutez tous les décorateurs à la vue de calendrier
        for (DayViewDecorator decorator : decoratorMap.values()) {
            calendarView.addDecorator(decorator);
        }
    }
}

