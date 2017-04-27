package com.example.trackingu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        Button AddFriend = (Button) findViewById(R.id.button_add);
        TextView mFirstname = (TextView) findViewById(R.id.friend_firstname);
        TextView mLastname = (TextView) findViewById(R.id.friend_lastname);
        ImageView mImage = (ImageView) findViewById(R.id.Image);

        mFirstname.setText(getIntent().getExtras().getString("firstname"));
        mLastname.setText(getIntent().getExtras().getString("lastname"));
        mImage.setImageBitmap(decodeBase64(getIntent().getExtras().getString("picture_user").toString()));

        AddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectserver("addfriend.php");
            }
        });
    }


    private void connectserver(String url) {
        ConnectServer handler = new ConnectServer(AddFriendActivity.this);
        try {

            String result = handler.execute(url).get();
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            if (result.trim().equals("insert my new friend")) {
                Intent intent = new Intent(AddFriendActivity.this, MainActivity.class);
                intent.putExtra("addfriend", "ture");
                startActivity(intent);
            }


        } catch (Exception e) {

        }

    }

    public String getEmailFriend() {
        return getIntent().getExtras().getString("email");
    }

    //การถอดรหัสรูปภาพด้วย Base64
    private Bitmap decodeBase64(String input) {
        if (input.length() > 5) {
            byte[] decodedBytes = Base64.decode(input, 0);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }
        return null;
    }
}
