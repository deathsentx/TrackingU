package com.example.trackingu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class ForgotPassword extends AppCompatActivity {
    public  String getmPhonenumber(){
        return  Phonenumber;
    }
    public String getmPassword() {
        return tmpPassword;
    }
    private  String Phonenumber;


    String tmpPassword= "";
    Button bSumit;
    EditText mPhoneNumber;
    SharedPreferences sharedpreferencesforgotpassword;
    SharedPreferences.Editor editorforgotpassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);


        bSumit = (Button) findViewById(R.id.submit);
        mPhoneNumber = (EditText) findViewById(R.id.phonenumber);

        sharedpreferencesforgotpassword = getSharedPreferences("TRACKINGU", Context.MODE_PRIVATE);

        bSumit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Phonenumber = mPhoneNumber.getText().toString();

                if (Phonenumber.length() != 0) { //check phonenumber ว่ามีการกรอกหรือไม่?

                    String randomPassword = randomString(6);

                    Log.d("Debug",randomString(6));


                    //encyption password
                    Common common = new Common();
                    tmpPassword = common.EncryptionPassword(randomPassword);
                    //คำสั่งที่ save ค่า password
                    editorforgotpassword = sharedpreferencesforgotpassword.edit();
                    editorforgotpassword.putString("Password", randomPassword);
                    editorforgotpassword.commit();



                    connectserver("forgotpassword.php");


                } else {
                    Toast.makeText(getApplicationContext(), "Please enter phonenumber ", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    // random password
    String randomString(final int length) {
        char[] chars = "1234567890abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    // check การส่ง sms ว่าส่งสำเร็จหรือไม่
    private void sendSMS(String Phonenumber, String message) {
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(Phonenumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Message has sent", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Message has Fail", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectserver(String url){
        ConnectServer handler = new  ConnectServer(ForgotPassword.this);

        try {


            String result = handler.execute(url).get();

            if (result.equals("false")) {
                Toast.makeText(getApplicationContext(), "Incorrectphone number ", Toast.LENGTH_SHORT).show();

            } else {


                String password = GetPassword();
                // สง sms พร้อมรหัสที่ random
                sendSMS(Phonenumber, password);


                Toast.makeText(getApplicationContext(), Phonenumber + result, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                startActivity(intent);
            }

        } catch (Exception e) {

        }
    }
    //การเก็บข้อมูล password
    public String GetPassword() {
        SharedPreferences sharedpreferencesforgotpassword = getSharedPreferences("TRACKINGU", Context.MODE_PRIVATE);
        return sharedpreferencesforgotpassword.getString("Password", "");
    }
}
