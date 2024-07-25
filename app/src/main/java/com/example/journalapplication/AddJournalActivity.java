package com.example.journalapplication;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class AddJournalActivity extends AppCompatActivity {

    //Widgets
    private EditText edTitle,edThoughts;
    private TextView titlePostUsernameTV,postDateImageTV;
    private ImageView imagePreview,addPhotoBtn;
    private Button btnSave;
    private ProgressBar progressBar;

    //Firebase Storage
    private StorageReference storageReference;

    //FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Journals");

    //FirebaseAuth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private String currentUserID;
    private String currentUserName;

    //Using ActivityResultLauncher
    private ActivityResultLauncher<String> mTakePhoto;
    private Uri imgURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        edTitle =  findViewById(R.id.post_title_et);
        edThoughts = findViewById(R.id.post_description_et);
        titlePostUsernameTV = findViewById(R.id.post_username_textView);
        postDateImageTV = findViewById(R.id.post_date_textView);
        imagePreview = findViewById(R.id.post_imageView);
        addPhotoBtn  = findViewById(R.id.postCameraButton);
        btnSave = findViewById(R.id.post_save_journal_button);
        progressBar = findViewById(R.id.post_progressBar);

        progressBar.setVisibility(View.INVISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        if(user!=null){
            currentUserID = user.getUid();
            currentUserName = user.getDisplayName();
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveJournal();
            }
        });
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTakePhoto.launch("image/*");
            }
        });
        mTakePhoto = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {
                imagePreview.setImageURI(o);
                imgURI = o;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
    }

    private void SaveJournal() {
        String title = edTitle.getText().toString().trim();
        String thoughts = edThoughts.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(title)&&!TextUtils.isEmpty(thoughts)&& imgURI!=null){
            final StorageReference filePath = storageReference
                    .child("journal_images")
                    .child("my_image_"+ Timestamp.now().getSeconds());
            filePath.putFile(imgURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imgUrl = uri.toString();
                            Journal journal = new Journal();
                            journal.setImgUrl(imgUrl);
                            journal.setTitle(title);
                            journal.setThoughts(thoughts);
                            journal.setTimestamp(new Timestamp(new Date()));
                            journal.setUserName(currentUserName);
                            journal.setUserId(currentUserID);

                            collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(AddJournalActivity.this, JournalListActivity.class));
                                    finish();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddJournalActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddJournalActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });
        }else{
            Toast.makeText(this, "Nothing", Toast.LENGTH_SHORT).show();
        }
    }
}