package com.example.todolist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.todolist.Adapter.SharesAdapter;
import com.example.todolist.Model.SharesModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class YourSharesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SharesAdapter adapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_shares);

        recyclerView = findViewById(R.id.recyclerView);

        // Créez votre requête Firestore pour récupérer les données de la collection
        Query query = FirebaseFirestore.getInstance()
                .collection("authorization")
                .document(currentUser.getEmail())
                .collection("yourShares")
                .whereNotEqualTo("sharedWith", currentUser.getEmail());
        //Where notEqualTo = doit contenir le field mais ne doit pas contenir la valeur

        // Créez une instance de FirestoreRecyclerOptions à partir de votre requête
        FirestoreRecyclerOptions<SharesModel> options = new FirestoreRecyclerOptions.Builder<SharesModel>()
                .setQuery(query, SharesModel.class)
                .build();

        // Initialisez votre adapter avec les options
        adapter = new SharesAdapter(options);

        // Définissez votre LinearLayoutManager et attachez l'adapter au RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Démarrez l'écoute des modifications de la base de données Firestore
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Arrêtez l'écoute des modifications de la base de données Firestore
        adapter.stopListening();
    }
}