package com.example.BookShare3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText nFullname, nEmail, nPassword, nPhone;
    Button nRegisterBtn;
    TextView nloginBtn;
    ProgressBar progressBar;

    FirebaseFirestore fstore;
    String userid;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        nFullname = findViewById(R.id.fullname);
        nEmail = findViewById(R.id.email);
        nPassword = findViewById(R.id.password);
        nPhone = findViewById(R.id.phone);
        nRegisterBtn = findViewById(R.id.registerBtn);
        nloginBtn = findViewById(R.id.createtext);
        progressBar = findViewById(R.id.progressBar);


        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        List<String> values = new ArrayList<>();

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }


        nloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

        nRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = nEmail.getText().toString().trim();
                String password = nPassword.getText().toString().trim();
                final String fullname = nFullname.getText().toString();
                final String phone = nPhone.getText().toString();

                if(TextUtils.isEmpty(email)){
                    nEmail.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    nPassword.setError("Password is Required");
                    return;
                }

                if(password.length() < 6){
                    nPassword.setError("Password must be >= 6 character");
                    return;
                }

                progressBar.setVisibility(view.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(getApplicationContext(), "Register Successful", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Onfailure: Email Not Sent" + e.getMessage());
                                }
                            });

                            Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_SHORT).show();
                            userid = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("/user").document(userid);
                            // UserID or userid?

                            Map<String,Object> user = new HashMap<>();
                            user.put("fname",fullname);
                            user.put("email", email);
                            user.put("phone",phone);
                            user.put("userId",userid);
                            user.put("book", values);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onsuccess: user profile is created for " + userid);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                            Intent intent = new Intent(getApplicationContext(), Account.class);
                            intent.putExtra("userId", userid);
                            startActivity(intent);

                        }
                        else{
                            Toast.makeText(Register.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(view.GONE);
                        }
                    }
                });
            }
        });
    }
}