package lk.jiat.eshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import lk.jiat.eshop.model.User;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getName();
    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, signupUsername, signupMobile;
    private Button signupButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        signupEmail = findViewById(R.id.emailtext);
        signupPassword = findViewById(R.id.passwordtext);
        signupUsername = findViewById(R.id.usernametext);
        signupMobile = findViewById(R.id.mobiletext);
        signupButton = findViewById(R.id.signupbutton);
        TextView view = findViewById(R.id.logintext);

        SpannableString underlinetext = new SpannableString("Login here");
        underlinetext.setSpan(new UnderlineSpan(), 0, underlinetext.length(),1);
        view.setText(underlinetext);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,LogInActivity.class);
                startActivity(intent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String username = signupUsername.getText().toString().trim();
                String mobile = signupMobile.getText().toString().trim();
                if (email.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                if (pass.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                } if (username.isEmpty()){
                    signupUsername.setError("Username cannot be empty");
                } if (mobile.isEmpty()){
                    signupMobile.setError("Mobile cannot be empty");
                } else{

                    firestore.collection("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.isEmpty()) {

                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        }

                                        Toast.makeText(SignUpActivity.this, "This Gmail Already Exists. Login with this Gmail", Toast.LENGTH_SHORT).show();

                                    } else {

                                        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {

                                                    String addressLine1 = "empty";
                                                    String addressLine2 = "empty";
                                                    String city = "empty";
                                                    String deliverMobile = "empty";

                                                    User user = new User(username, email, mobile, pass,addressLine1,addressLine2,city,deliverMobile);

                                                    db.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {

                                                            Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();

                                                            signupEmail.setText("");
                                                            signupMobile.setText("");
                                                            signupPassword.setText("");
                                                            signupUsername.setText("");

                                                            startActivity(new Intent(SignUpActivity.this, LogInActivity.class));

                                                        }}).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(SignUpActivity.this, "SignUp Fail. Try Again", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                } else {
                                                    Toast.makeText(SignUpActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error searching for user by email", e);
                                }
                            });


                }

            }
        });

    }
}