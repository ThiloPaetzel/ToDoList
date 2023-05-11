package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.Adapter.ToDoAdapter;
import com.example.todolist.Model.ToDoModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnDialogCloseListener {

    private RecyclerView recyclerView;
    private FloatingActionButton mFab;
    private FirebaseFirestore firestore;
    private ToDoAdapter adapter;
    private List<ToDoModel> mList;
    private Query query;
    private ListenerRegistration listenerRegistration;

    private Chip tagMaison, tagTravail, tagLoisir;
    private ChipGroup chipGroup;
    private List<String> selectedTags = new ArrayList<>();
    private TextView listOfText;

    Spinner spinner;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        mFab = findViewById(R.id.floatingActionButton);
        firestore = FirebaseFirestore.getInstance();
        tagMaison = findViewById(R.id.tag_maison);
        tagTravail = findViewById(R.id.tag_travail);
        tagLoisir = findViewById(R.id.tag_loisir);
        chipGroup = findViewById(R.id.filter_chip_group);
        listOfText = findViewById(R.id.listOfText);
        spinner = findViewById(R.id.spinner);
        addAuthToSpinner();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mList.clear();
                String selectedValue = parent.getItemAtPosition(position).toString();
                SpinnerSelection.selectedValue = selectedValue;
                showData();
                listOfText.setText("List of : " + SpinnerSelection.selectedValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));



        //Permet d'afficher AddNewTask mais en restant dans la même page
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        mList = new ArrayList<>();
        adapter = new ToDoAdapter(MainActivity.this, mList);


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);


        recyclerView.setAdapter(adapter);

        //Toast.makeText(this,"value " + SpinnerSelection.selectedValue, Toast.LENGTH_LONG).show();

        //showData();

        //Home tag clicked
        tagMaison.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mList.clear();
                    showData();
                }else{
                    mList.clear();
                    showData();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        //Work tag clicked
        tagTravail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mList.clear();
                    showData();
                }else{
                    mList.clear();
                    showData();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        //Spare time tag clicked
        tagLoisir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mList.clear();
                    showData();
                }else{
                    mList.clear();
                    showData();
                    adapter.notifyDataSetChanged();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.disconnect_menu:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to disconnect ?")
                        .setTitle("Disconnect")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            case R.id.profile_menu:
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                return true;
            case R.id.share_menu:
                Intent shareIntent = new Intent(getApplicationContext(), ShareActivity.class);
                startActivity(shareIntent);
                return true;
            case R.id.calendar_menu:
                Intent calendarIntent = new Intent(getApplicationContext(), CalendarViewActivity.class);
                startActivity(calendarIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    private void showData() {
        // Vider la liste des tâches avant de l'ajouter à nouveau
        mList.clear();
        // Créer une requête Firestore qui ordonne les tâches par date décroissante
        Query query = firestore.collection("user_tasks").document(SpinnerSelection.selectedValue).collection("task").orderBy("time", Query.Direction.DESCENDING);

        // Créer une liste des tags sélectionnés
        List<String> selectedTags = new ArrayList<>();
        if (tagMaison.isChecked()) {
            selectedTags.add("Home");
        }
        if (tagTravail.isChecked()) {
            selectedTags.add("Work");
        }
        if (tagLoisir.isChecked()) {
            selectedTags.add("Spare Time");
        }

        // Ajouter un filtre pour inclure seulement les tâches qui ont au moins un des tags sélectionnés
        if (!selectedTags.isEmpty()) {
            query = query.whereIn("tag", selectedTags);
        }

        // Créer un écouteur de snapshot pour la requête Firestore
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                // Parcourir les changements de documents et ajouter les tâches à la liste
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        String id = documentChange.getDocument().getId();
                        ToDoModel toDoModel = documentChange.getDocument().toObject(ToDoModel.class).withId(id);

                        mList.add(toDoModel);
                        // Avertir l'adaptateur que la liste des tâches a été mise à jour
                        adapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }

    private void addAuthToSpinner(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DocumentReference docRef = db.collection("authorization").document(currentUserEmail);

        //Test
        List<Object> sharedPermissions = new ArrayList<>();
        db.collection("authorization").document(currentUserEmail).collection("sharedPermission").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                Map<String, Object> data = document.getData();
                for (Map.Entry<String, Object> entry : data.entrySet()){
                    sharedPermissions.add(entry.getValue());
                }
            }

            //Remplir le spinner avec les valeurs de sharedPermission
            Spinner filterSpinner = findViewById(R.id.spinner);
            ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(MainActivity.this, android.R.layout.simple_spinner_item, sharedPermissions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            filterSpinner.setAdapter(adapter);

            //Sélectionner la valeur du spinner qui correspond à l'utilisateur connecté
            for (int i = 0; i < sharedPermissions.size(); i++){
                if (sharedPermissions.get(i).toString().equals(currentUserEmail)){
                    filterSpinner.setSelection(i);
                    SpinnerSelection.selectedValue = sharedPermissions.get(i).toString();
                }
            }

            //SpinnerSelection.selectedValue = sharedPermissions.get(0).toString();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erreur lors de la récupération des autorisations", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList.clear();
        showData();
        adapter.notifyDataSetChanged();
    }


}