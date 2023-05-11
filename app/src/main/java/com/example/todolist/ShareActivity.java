package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareActivity extends AppCompatActivity {
    private EditText shareName;
    private Button shareButton, yourSharesButton;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        shareButton = findViewById(R.id.shareButton);
        shareName = findViewById(R.id.usernameEditText);
        yourSharesButton = findViewById(R.id.yourShareButton);


        yourSharesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), YourSharesActivity.class);
                startActivity(intent);

                /* Test
                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                // Récupérer une référence à la sous-collection "yourShares" pour l'utilisateur actuel
                CollectionReference yourSharesRef = FirebaseFirestore.getInstance().collection("authorization")
                        .document(currentUserEmail).collection("yourShares");
                // Récupérer tous les documents de la sous-collection "yourShares" pour l'utilisateur actuel
                yourSharesRef.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                // Créer une chaîne vide pour stocker les noms d'utilisateur partagés
                                String sharedUsers = "";
                                // Parcourir tous les documents de la sous-collection "yourShares"
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    // Récupérer le nom d'utilisateur du document actuel
                                    String username = documentSnapshot.getString("username");
                                    // Ajouter le nom d'utilisateur à la chaîne
                                    sharedUsers += username + "\n";
                                }
                                // Afficher la chaîne de noms d'utilisateur partagés dans un Toast
                                Toast.makeText(getApplicationContext(), sharedUsers, Toast.LENGTH_SHORT).show();
                            }
                        });
                 */
            }

        });




        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = shareName.getText().toString().trim();
                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                //Test
                // Récupérer une référence à la sous-collection "sharedPermissions" pour l'utilisateur actuel
                CollectionReference sharedPermissionsRef = FirebaseFirestore.getInstance().collection("authorization")
                        .document(email).collection("sharedPermission");
                //Référence à la sous-collection "yourShares" pour l'utilisateur actuel
                CollectionReference yourSharesRef = FirebaseFirestore.getInstance().collection("authorization")
                        .document(currentUserEmail).collection("yourShares");
                // Créer un nouveau document dans la sous-collection "yourShares" avec un ID unique
                DocumentReference newShareRef = yourSharesRef.document();
                // Créer un nouveau document dans la sous-collection "sharedPermissions" avec un ID unique
                DocumentReference newPermissionRef = sharedPermissionsRef.document();


                //Test


                // Vérifier que l'UID correspond à un utilisateur existant dans l'outil d'authentification de Firebase
                FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                        .addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                            @Override
                            public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
                                List<String> signInMethods = signInMethodQueryResult.getSignInMethods();
                                if (signInMethods == null || signInMethods.isEmpty()) {
                                    // L'UID ne correspond à aucun utilisateur, afficher un message d'erreur
                                    Toast.makeText(getApplicationContext(), "Utilisateur introuvable", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (email.equals(currentUserEmail)) {
                                    // L'UID correspond à l'utilisateur actuel, afficher un message d'erreur
                                    Toast.makeText(getApplicationContext(), "Vous ne pouvez pas partager avec vous-même", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (email.equals("")) {
                                    // L'UID correspond à l'utilisateur actuel, afficher un message d'erreur
                                    Toast.makeText(getApplicationContext(), "Veuillez entrer un email d'un utilisateur", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // L'UID correspond à un utilisateur existant, ajouter l'autorisation de partage dans la base de données
                                // Créer un objet de données pour stocker les détails de l'autorisation de partage
                                Map<String, Object> permissionData = new HashMap<>();
                                permissionData.put("sharedBy", currentUserEmail);
                                newPermissionRef.set(permissionData, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Afficher un message de succès
                                                Toast.makeText(getApplicationContext(), "Partage autorisé avec succès", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Afficher un message d'erreur en cas d'échec
                                                Toast.makeText(getApplicationContext(), "Erreur lors de l'autorisation de partage : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                // Créer un objet de données pour stocker les détails du partage de l'utilisateur actuel
                                Map<String, Object> shareData = new HashMap<>();
                                shareData.put("sharedWith", email);
                                newShareRef.set(shareData, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Afficher un message de succès
                                                Toast.makeText(getApplicationContext(), "Données dans yourShares créé", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Afficher un message d'erreur en cas d'échec
                                                Toast.makeText(getApplicationContext(), "Erreur lors de la création dans yourShares : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Afficher un message d'erreur en cas d'échec
                                Toast.makeText(getApplicationContext(), "Erreur lors de la vérification de l'email : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            });
    }
}

