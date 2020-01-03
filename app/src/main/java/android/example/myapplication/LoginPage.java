package android.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    //Firebase authentication
    private FirebaseAuth mAuth;

    EditText userName;
    EditText password;

    TextView incorrectCredentials;

    String userNameStr;
    String passwordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        userName = findViewById(R.id.userNameEnter);
        password = findViewById(R.id.passwordEnter);
        incorrectCredentials = findViewById(R.id.incorrectCredentials);
        mAuth = FirebaseAuth.getInstance();
    }

    public void logInClick(View v)
    {
        userNameStr = userName.getText().toString();
        passwordStr = password.getText().toString();

        loginUser(userNameStr, passwordStr);
    }

    public void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(mAuth.getCurrentUser().isEmailVerified()) {
                                updateUI(user);
                            }
                            else {
                                incorrectCredentials.setText("Email not verified");
                                updateUI(null);
                            }
                        } else {

                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void signUpHereClick(View v)
    {
        Intent intent = new Intent(this, SignUpPage.class);
        startActivity(intent);
    }

    /*@Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }*/

    public void updateUI(FirebaseUser currentUser) {
        if (currentUser != null)
        {
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        }
        else
        {
            incorrectCredentials.setVisibility(View.VISIBLE);
        }
    }
}