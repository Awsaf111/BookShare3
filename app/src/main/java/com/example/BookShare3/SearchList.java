package com.example.BookShare3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.firestore.core.View;

import java.util.ArrayList;
import java.util.List;


public class SearchList extends AppCompatActivity {

    private List<String> bookOwners = new ArrayList<>();

    private List<String> phone = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        recyclerView = findViewById(R.id.recyclerView);

        String bookName = getIntent().getStringExtra("Book");
        Log.d("Firestore", "Book Name: " + bookName);
        FirebaseFirestore fstore;
        fstore = FirebaseFirestore.getInstance();
        // Query Firestore to fetch documents where the book field contains the specified book name
        CollectionReference collectionRef = fstore.collection("/user");
        Query query = collectionRef.whereArrayContains("book", bookName);

       // CollectionReference subcollectionRef = fstore.collection("/user").document("userId").collection("book");
        //DocumentReference docRef = fstore.collection("/user").document(userId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //itemList = (List<String>) documentSnapshot.get("book");
                    bookOwners = new ArrayList<>();
                    phone = new ArrayList<>();

                    QuerySnapshot docu;
                    docu = task.getResult();
                    Log.d("Firestore", "Document Count: " + docu.size());

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Get the owner name from each document and add it to the list
                        String ownerName = document.getString("fname");
                        String number = document.getString("phone");
                        bookOwners.add(ownerName);
                        phone.add(number);

                        // Log the ownerName for debugging
                        Log.d("Firestore", "Owner Name: " + ownerName);
                    }

                    // Add debug statement to verify the itemList
                    Log.d("Firestore", "Retrieved itemList: " + bookOwners.toString());

                    adapter = new RecyclerViewAdapter(bookOwners,phone);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchList.this));
                }
            }
        });

    }

    private static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private List<String> bookOwners;
        private List<String> phone;

        public RecyclerViewAdapter(List<String> bookOwners, List<String> phone){

            this.bookOwners = bookOwners;
            this.phone = phone;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            android.view.View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.owners, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position){
            String item = bookOwners.get(position);
            String phoneNumber = phone.get(position);
            holder.ownerTextView.setText(item);
            holder.phoneTextView.setText(phoneNumber);
            // Add log statement to check the position
            Log.d("RecyclerViewAdapter", "onBindViewHolder() - Position: " + position);
        }

        public List<String> getItemList() {
            return bookOwners;
        }

        @Override
        public int getItemCount() {
            int size = bookOwners.size();
            Log.d("RecyclerViewAdapter", "getItemCount() - Size: " + size);
            return size;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView ownerTextView;
            TextView phoneTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                ownerTextView = itemView.findViewById(R.id.ownerTextView);
                phoneTextView = itemView.findViewById(R.id.phoneTextView);
            }
        }
    }
}