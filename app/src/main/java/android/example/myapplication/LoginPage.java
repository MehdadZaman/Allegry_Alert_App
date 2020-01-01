package android.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginPage extends AppCompatActivity {

    EditText userName;
    EditText password;

    TextView inccorrectCredentials;

    String userNameStr;
    String passwordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        userName = findViewById(R.id.userNameEnter);
        password = findViewById(R.id.passwordEnter);
        inccorrectCredentials = findViewById(R.id.incorrectCredentials);
    }


    public void logInClick(View v)
    {
        userNameStr = userName.getText().toString();
        passwordStr = password.getText().toString();

        if(userNameStr.equals("mehdadzaman") && passwordStr.equals("password1"))
        {
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        }
        else
        {
            inccorrectCredentials.setVisibility(View.VISIBLE);
        }
    }

    public void signUpHereClick(View v)
    {
        Intent intent = new Intent(this, SignUpPage.class);
        startActivity(intent);
    }
}
