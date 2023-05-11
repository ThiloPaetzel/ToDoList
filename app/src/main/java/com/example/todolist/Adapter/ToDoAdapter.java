package com.example.todolist.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.AddNewTask;
import com.example.todolist.MainActivity;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.R;
import com.example.todolist.SpinnerSelection;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDoModel> todoList;
    private MainActivity activity;
    private FirebaseFirestore firestore;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser currentUser = mAuth.getCurrentUser();


    public ToDoAdapter(MainActivity mainActivity, List<ToDoModel> todoList){
        this.todoList = todoList;
        activity = mainActivity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.each_task,parent,false);
        firestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    public void deleteTask(int position){
        ToDoModel toDoModel = todoList.get(position);
        firestore.collection("user_tasks").document(SpinnerSelection.selectedValue).collection("task").document(toDoModel.TaskId).delete();
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public Context getContext(){
        return activity;
    }

    public void editTask(int position){
        ToDoModel toDoModel = todoList.get(position);

        CollectionReference taskRef = firestore.collection("user_tasks").document(SpinnerSelection.selectedValue).collection("task");

        taskRef.whereEqualTo("task", toDoModel.getTask())
                .whereEqualTo("due", toDoModel.getDue())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            String taskId = queryDocumentSnapshots.getDocuments().get(0).getId();

                            Bundle bundle = new Bundle();
                            bundle.putString("task", toDoModel.getTask());
                            bundle.putString("due", toDoModel.getDue());
                            bundle.putString("id", taskId);
                            bundle.putString("tag", toDoModel.getTag());

                            AddNewTask addNewTask = new AddNewTask();
                            addNewTask.setArguments(bundle);
                            addNewTask.show(activity.getSupportFragmentManager(), addNewTask.getTag());

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Error getting task id", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        ToDoModel toDoModel = todoList.get(position);
        holder.mCheckBox.setText(toDoModel.getTask());
        holder.mDueDateTv.setText("Due on " + toDoModel.getDue());

        holder.mCheckBox.setChecked(toBoolean(toDoModel.getStatus()));

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = holder.getBindingAdapterPosition(); //get position of the task
                if (position != RecyclerView.NO_POSITION) {
                    ToDoModel toDoModel = todoList.get(position);
                    //String taskId = toDoModel.TaskId;
                    if (isChecked) {
                        firestore.collection("user_tasks").document(SpinnerSelection.selectedValue).collection("task").document(toDoModel.TaskId).update("status", 1);
                    } else {
                        firestore.collection("user_tasks").document(SpinnerSelection.selectedValue).collection("task").document(toDoModel.TaskId).update("status", 0);
                    }
                }
            }
        });
    }

    private boolean toBoolean(int status){
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    //Renseigner les éléments de la page à afficher, ici each_task.xml ce fichier xml représente chaque tache provenant de la database
    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mDueDateTv;
        CheckBox mCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mDueDateTv = itemView.findViewById(R.id.due_date_tv);
            mCheckBox = itemView.findViewById(R.id.mCheckbox);
        }
    }
}
