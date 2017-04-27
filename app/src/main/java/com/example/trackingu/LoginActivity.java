package com.example.trackingu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public String getmEmail() {
        return mEmail.getText().toString();
    } // แปลงค่าให้เป็น String ถ้าไม่เเปลงค่าจะได้จะเป็น editext

    public String getmPassword() {
        return tmpPassword;
    }

    String tmpPassword = "";
    Button bLogin, bRegister;
    EditText mEmail, mPassword;
    TextView mForgotpassword;
    SharedPreferences sharedpreferencesemail;
    SharedPreferences.Editor editoremail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Log.d("Debug", "Start");
        bLogin = (Button) findViewById(R.id.login);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mForgotpassword = (TextView) findViewById(R.id.forgot);
        bRegister = (Button) findViewById(R.id.register);
        sharedpreferencesemail = getSharedPreferences("TRACKINGU", Context.MODE_PRIVATE);


        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmail.getText().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Please enter your e-mail complete",
                            Toast.LENGTH_SHORT).show();
                } else if (mPassword.getText().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
                } else {
                    //validate email
                    Common common = new Common();
                    if (common.isValidEmail(mEmail.getText().toString()) == false) {
                        Toast.makeText(getApplicationContext(), "Please check your email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // คำสั่งที่ save ค่า login
                    editoremail = sharedpreferencesemail.edit();
                    editoremail.putString("Email", mEmail.getText().toString());
                    editoremail.commit();


                    //encyption password
                    tmpPassword = common.EncryptionPassword(mPassword.getText().toString());
                    //            Toast.makeText(getApplicationContext(), tmpPassword, Toast.LENGTH_SHORT).show();


                    connectserver("login.php");

                }
            }
        });

        mForgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Forgot Password", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);

            }
        });
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Register", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void connectserver(String url) {
        ConnectServer handler = new ConnectServer(LoginActivity.this);

        try {

            String result = handler.execute(url).get();

            if (result.equals("Incorrect Username and Password")) {
                Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

        } catch (Exception e) {

        }


    }

    //การกดปุ่ม back ก่อนออกจากโปรแกรม
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("คุณต้องการออกจากระบบหรือไม่?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences preferences = getSharedPreferences("email", 0);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    //การเก็บข้อมูล email
    public static String getmEmail(Context context) {
        SharedPreferences sharedpreferencesemail = context.getSharedPreferences("TRACKINGU", Context.MODE_PRIVATE);
        return sharedpreferencesemail.getString("Email", "");
    }

}
