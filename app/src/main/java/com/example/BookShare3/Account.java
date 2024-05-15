package com.example.BookShare3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Account extends AppCompatActivity {

    EditText nBook;

    Button nShareBtn,nShelfBtn,nRemoveBtn,nSearchBtn;

    List<String> itemList = new ArrayList<>();


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        nBook = findViewById(R.id.BookValueTextView);
        nShareBtn = findViewById(R.id.ShareBtn);
        nShelfBtn = findViewById(R.id.ShelfBtn);
        nRemoveBtn = findViewById(R.id.RemoveBtn);
        nSearchBtn = findViewById(R.id.SearchBtn);


        String userId = getIntent().getStringExtra("userId");
        FirebaseFirestore fstore;
        fstore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fstore.collection("/user").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("fname");

                    TextView fullNameTextView = findViewById(R.id.fullnameValueTextView);
                    fullNameTextView.setText(fullName);

                    itemList = (List<String>) documentSnapshot.get("book");


                    nShareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String book = nBook.getText().toString();
                            docRef.update("book", FieldValue.arrayUnion(book));

                            Intent intent = new Intent(getApplicationContext(), ItemList.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                        }
                    });

                    nShelfBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getApplicationContext(), ItemList.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                        }
                    });

                    nRemoveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String book = nBook.getText().toString();

                            if (itemList != null && itemList.contains(book)) {
                                itemList.remove(book);
                            }

                            // Update the document with the modified list
                            docRef.update("book", itemList);
                            Intent intent = new Intent(getApplicationContext(), ItemList.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                        }
                    });

                    nSearchBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String book = nBook.getText().toString();

                            Intent intent = new Intent(getApplicationContext(), SearchList.class);
                            intent.putExtra("Book", book);
                            startActivity(intent);
                        }
                    });

                }
            }
        });

    }
}