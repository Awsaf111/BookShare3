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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.core.View;

import java.util.ArrayList;
import java.util.List;


public class ItemList extends AppCompatActivity {

    private List<String> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        recyclerView = findViewById(R.id.recyclerView);

        String userId = getIntent().getStringExtra("userId");
        FirebaseFirestore fstore;
        fstore = FirebaseFirestore.getInstance();
        CollectionReference subcollectionRef = fstore.collection("/user").document("userId").collection("book");
        DocumentReference docRef = fstore.collection("/user").document(userId);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    itemList = (List<String>) documentSnapshot.get("book");

                    // Add debug statement to verify the itemList
                    Log.d("Firestore", "Retrieved itemList: " + itemList.toString());

                    adapter = new RecyclerViewAdapter(itemList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ItemList.this));
                }
            }
        });

    }

    private static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private List<String> itemList;

        public RecyclerViewAdapter(List<String> itemList){

            this.itemList = itemList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            android.view.View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position){
            String item = itemList.get(position);
            holder.itemTextView.setText(item);

            // Add log statement to check the position
            Log.d("RecyclerViewAdapter", "onBindViewHolder() - Position: " + position);
        }

        public List<String> getItemList() {
            return itemList;
        }

        @Override
        public int getItemCount() {
            int size = itemList.size();
            Log.d("RecyclerViewAdapter", "getItemCount() - Size: " + size);
            return size;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView itemTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                itemTextView = itemView.findViewById(R.id.itemTextView);
            }
        }
    }
}