package android.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.spec.ECField;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignUpPage extends AppCompatActivity {

    //FireStore
    FirebaseFirestore db;

    private FirebaseAuth mAuth;

    private EditText name;
    private EditText email;
    private EditText password;

    private String nameStr;
    private String emailStr;
    private String passwordStr;

    private TextView invalidSignUp;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        name = findViewById(R.id.nameEnterSignUp);
        email = findViewById(R.id.emailEnterSignUp);
        password = findViewById(R.id.passwordEnterSignUp);

        invalidSignUp = findViewById(R.id.invalidSignUp);

        signUp = findViewById(R.id.signUpButtonSignUp);

        mAuth = FirebaseAuth.getInstance();

        //FireStore
        db = FirebaseFirestore.getInstance();
    }

    public void signUpClick(View v)
    {
        nameStr = name.getText().toString();
        emailStr = email.getText().toString();
        passwordStr = password.getText().toString();
        if(!validateForm())
        {
            invalidSignUp.setText("Failed to send verification Email");
            invalidSignUp.setVisibility(View.VISIBLE);
            return;
        }
        createAccount(emailStr, passwordStr);
        mAuth.signOut();
    }

    public void backToLoginClick(View v)
    {
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    public void createAccount(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendEmailVerification();
                        } else {
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthUserCollisionException fauce)
                            {
                                invalidSignUp.setText("There already exists an account with this email");
                            }
                            catch (FirebaseAuthWeakPasswordException faice)
                            {
                                invalidSignUp.setText("Password should be more than six characters and use a mix of alphabetic and numeric characters");
                            }
                            catch (Exception e)
                            {
                                invalidSignUp.setText("Failed to send verification Email");
                            }
                            invalidSignUp.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            invalidSignUp.setText("Email Sent. Press back to login when account is verified");
                            invalidSignUp.setTextColor(Color.GREEN);
                            invalidSignUp.setVisibility(View.VISIBLE);
                            signUp.setEnabled(false);
                            //FireStore
                            addUserToDataBase();
                            mAuth.signOut();
                        } else {
                            mAuth.getCurrentUser().delete();
                            invalidSignUp.setText("Failed to send verification Email");
                            invalidSignUp.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(nameStr)) {
            valid = false;
        }

        if (TextUtils.isEmpty(emailStr)) {
            valid = false;
        }
        if (TextUtils.isEmpty(passwordStr)) {
            valid = false;
        }
        return valid;
    }

    //FireStore
    public void addUserToDataBase()
    {
        Map<String, Object> userData = new HashMap<>();
        userData.put("Name", nameStr);
        userData.put("Email", emailStr);
        userData.put("Food Allergies", Arrays.asList("Food Allergies"));
        db.collection("users").document(mAuth.getCurrentUser().getUid()).set(userData);
    }

}