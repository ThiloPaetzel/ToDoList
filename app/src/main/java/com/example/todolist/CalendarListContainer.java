package com.example.todolist;

import android.app.Application;

import com.example.todolist.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class CalendarListContainer extends Application {
    public static List<ToDoModel> taskList; //Contient la list des taches du jour sélectionné
}
