package com.example.todolist.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Model.SharesModel;
import com.example.todolist.R;
import com.example.todolist.ShareActivity;
import com.example.todolist.YourSharesActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class SharesAdapter extends FirestoreRecyclerAdapter<SharesModel, SharesAdapter.SharesViewHolder> {
    private FirebaseFirestore firestore;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser currentUser = mAuth.getCurrentUser();

    public SharesAdapter(@NonNull FirestoreRecyclerOptions<SharesModel> options) {
        super(options);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull SharesViewHolder holder, int position, @NonNull SharesModel model) {
        holder.email.setText(model.getSharedWith());
    }

    @NonNull
    @Override
    public SharesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_shares, parent, false);
        return new SharesViewHolder(view);
    }

    public void deleteItem(int position) {
        // Supprimez l'élément à la position donnée dans la base de données Firestore
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public class SharesViewHolder extends RecyclerView.ViewHolder {

        TextView email;
        Button deleteBtn;

        public SharesViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.emailShared);
            deleteBtn = itemView.findViewById(R.id.delete_button);

            // Attachez un écouteur de clic pour supprimer l'élément du RecyclerView
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    showConfirmationDialog(position);
                }
            });
        }

        private void showConfirmationDialog(int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(deleteBtn.getContext());
            builder.setTitle("Confirmation de suppression");
            builder.setMessage("Voulez-vous vraiment supprimer cet élément ?");
            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteItem(position);
                    deleteSharedPermission(email.getText().toString());
                }
            });
            builder.setNegativeButton("Non", null);
            builder.show();
        }

        private void deleteSharedPermission(String email) {
            // Référence au document utilisateur
            DocumentReference userDocRef = firestore.collection("authorization").document(email);
            // Référence à la sous-collection sharedPermission
            CollectionReference sharedPermissionColRef = userDocRef.collection("sharedPermission");

            // Créer la requête pour récupérer le document qui correspond à l'e-mail de l'utilisateur actuel
            Query query = sharedPermissionColRef.whereEqualTo("sharedBy", currentUser.getEmail());

            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Supprimer le document de la sous-collection
                        sharedPermissionColRef.document(document.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(deleteBtn.getContext(), "Partage supprimé", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(deleteBtn.getContext(), "erreur lors de la suppression du partage" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }
}

