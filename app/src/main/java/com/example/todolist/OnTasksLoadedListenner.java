package com.example.todolist;

import com.example.todolist.Model.ToDoModel;

import java.util.List;

public interface OnTasksLoadedListenner {
    void onTasksLoaded(List<ToDoModel> tasksForDate);
}
