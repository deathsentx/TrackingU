package com.example.trackingu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static com.example.trackingu.LoginActivity.getmEmail;

public class Index extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //ถ้า email ไม่เท่ากับค่าว่าง จะไปหน้า mainactiviity
        if(getmEmail(this)!=""){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
    //ถ้า email เท่ากับค่าว่าง จะไปหน้า loginactiviity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
