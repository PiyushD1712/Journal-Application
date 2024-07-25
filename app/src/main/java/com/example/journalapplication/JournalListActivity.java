package com.example.journalapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class JournalListActivity extends AppCompatActivity {
    //FirebaseAuth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    //FirebaseFirestore
    private FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firestore.collection("Journals");
    //Storage
    private StorageReference storageReference;
    private RecyclerView recyclerView;
    private MyJournalAdapter myJournalAdapter;
    private List<Journal> list;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);
        firebaseAuth= FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        fab = findViewById(R.id.idAddJournalButton);

        recyclerView = findViewById(R.id.idRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JournalListActivity.this,AddJournalActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idItem = item.getItemId();
        if(idItem == R.id.idAddJournal){
            if(user!=null && firebaseAuth!=null){
                startActivity(new Intent(JournalListActivity.this,AddJournalActivity.class));
            }
        } else if (idItem == R.id.idSignOut) {
            if(user!=null && firebaseAuth!=null){
                firebaseAuth.signOut();
                startActivity(new Intent(JournalListActivity.this,MainActivity.class));
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                list.clear();
                for(QueryDocumentSnapshot journals: queryDocumentSnapshots){
                    Journal journal = journals.toObject(Journal.class);
                    list.add(journal);
                }
                myJournalAdapter =  new MyJournalAdapter(JournalListActivity.this,list);
                recyclerView.setAdapter(myJournalAdapter);
                myJournalAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JournalListActivity.this, "Oops! Something went Wrong..", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
}