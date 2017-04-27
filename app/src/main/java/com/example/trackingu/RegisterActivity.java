package com.example.trackingu;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class RegisterActivity extends AppCompatActivity {

    public String getmFirstName() {
        return mFirstName.getText().toString();
    }

    public String getmLastName() {
        return mLastName.getText().toString();
    }

    public String getmPhonenumber() {
        return mPhonenumber.getText().toString();
    }

    public String getmEmail() {
        return mEmail.getText().toString();
    }

    public String getmPassword() {
        return tmpPassword;
    }



    public String getmImage() {

        Log.d("Dedug","getmImage");
        mImage.buildDrawingCache();
        Bitmap bitmap= mImage.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }



    String tmpPassword= "";
    Button bRegister;
    EditText mFirstName, mLastName, mPhonenumber, mEmail, mPassword, mConfirmPass;
    ImageView mImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        bRegister = (Button) findViewById(R.id.button_regis);
        mFirstName = (EditText) findViewById(R.id.FirstName);
        mLastName = (EditText) findViewById(R.id.LastName);
        mPhonenumber = (EditText) findViewById(R.id.Phonenumber);
        mEmail = (EditText) findViewById(R.id.Email);
        mPassword = (EditText) findViewById(R.id.Password);
        mConfirmPass = (EditText) findViewById(R.id.ConfirmPass);
        mImage = (ImageView) findViewById(R.id.Image);

        mImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               //crop รูปภาพ โดยใช้ library crop
                Crop.pickImage(RegisterActivity.this);

            }

        });


        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFirstName.getText().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Please enter your firstname", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mLastName.getText().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Please enter your lastname", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPhonenumber.getText().length() < 10) {
                    Toast.makeText(getApplicationContext(), "Please enter your phonenumber", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mEmail.getText().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Common common = new Common();
                    if(common.isValidEmail(mEmail.getText().toString())== false){
                        Toast.makeText(getApplicationContext(), "Please check your email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (mPassword.getText().length() != 0) {
                    if (mPassword.getText().length() < 6) {
                        Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (mConfirmPass.getText().length() < 1) {
                            Toast.makeText(getApplicationContext(), "Please enter your Confirm password", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!mPassword.getText().toString().equals(mConfirmPass.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "Please Check password", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }



                    //encyption password
                    Common common = new Common();
                    tmpPassword = common.EncryptionPassword(mPassword.getText().toString());

                }

                else {
                    Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                connectserver("register.php");
            }
        });

    }



    private void connectserver(String url) {
        ConnectServer handler = new ConnectServer(RegisterActivity.this);

        try {


            String result = handler.execute(url).get();
              Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show();
            if (result.equals("ใส่ Username ใหม่")) {

            } else {

               finish();
            }

        } catch (Exception e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mImage.setImageDrawable(null);
            mImage.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}
