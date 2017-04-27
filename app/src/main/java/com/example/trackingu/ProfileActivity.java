package com.example.trackingu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ProfileActivity extends Fragment {


    public String getmFirstName() {
        return mFirstName.getText().toString();
    }

    public String getmLastName() {
        return mLastName.getText().toString();
    }

    public String getmPhonenumber() {
        return mPhonenumber.getText().toString();
    }


    public String getmPassword() {
        return tmpPassword;
    }
    public String getmPin() {
        return mPin.getText().toString();
    }
    public String getmProfile() {

        Log.d("Dedug","getmProfile");
        Bitmap bm=((BitmapDrawable)mProfile.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;


    }


    String tmpPassword= "";
    ImageView  mProfile;
    EditText mFirstName, mLastName, mPhonenumber, mEmail, mPassword,mConfirmPass, mPin ;
    Button bSummit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_profile , container , false);


         mProfile = (ImageView)rootView.findViewById(R.id.Image_profile);
         mFirstName = (EditText) rootView.findViewById(R.id.FirstName);
         mLastName = (EditText) rootView.findViewById(R.id.LastName);
         mPhonenumber = (EditText) rootView.findViewById(R.id.Phonenumber);
         mEmail = (EditText) rootView.findViewById(R.id.Email);
         mPassword = (EditText) rootView.findViewById(R.id.Password);
         mConfirmPass = (EditText) rootView.findViewById(R.id.ConfirmPass);
         mPin = (EditText) rootView.findViewById(R.id.Pin);
         bSummit = (Button) rootView.findViewById(R.id.button_edit);



        connectserver("selectprofile.php");

        Log.d("Debug","end onCreateView");


//        mEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getActivity() ,EditProfileActivity.class);
//                startActivity(intent);
//            }
//        });


        mProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Debug", "start onClickImage");
               // Crop.pickImage(getActivity());
                Crop.pickImage(getActivity(), ProfileActivity.this);

                Log.d("Debug", "end onClickImage");
            }

        });

        bSummit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity() ,ProfileActivity.class);
//                startActivity(intent);
                Log.d("Debug", "setOnClickListener");

                        if (mFirstName.getText().length() < 1) {
                            Toast.makeText(getActivity(), "Please enter your firstname", Toast.LENGTH_SHORT).show();

                            return;
                        }
                        if (mLastName.getText().length() < 1) {
                            Toast.makeText(getActivity(), "Please enter your lastname", Toast.LENGTH_SHORT).show();
                            return;
                        }


            if (mPassword.getText().length() != 0) {
                if (mPassword.getText().length() < 6){
                    Toast.makeText(getActivity(), "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    if (mConfirmPass.getText().length() < 1){
                        Toast.makeText(getActivity(), "Please enter your Confirm password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!mPassword.getText().toString().equals(mConfirmPass.getText().toString())){
                        Toast.makeText(getActivity(), "Please Check password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                //encyption password
                Common common = new Common();
                tmpPassword  = common.EncryptionPassword(mPassword.getText().toString());




            }
                updateprofile("updateprofile.php");

                    }
        });


        return  rootView;
    }

    private void connectserver(String url) {
        Log.d("Dedug","Start connectserver");
        ConnectServer handler = new ConnectServer(ProfileActivity.this);  // อันนี้จะเรียกใช้ constructor ของ class connectserver
        JSONArray mJSONArray = null;
        try {

                String result = handler.execute(url).get();
                JSONObject JsonObject = new JSONObject(result);
                mJSONArray = JsonObject.getJSONArray("Datafromprofile");

            //ดึงมาจาก selectprofile.php
            for (int i = 0; i < mJSONArray.length(); i++){
                    mFirstName.setText(mJSONArray.getJSONObject(i).getString("firstname").toString());
                    mLastName.setText(mJSONArray.getJSONObject(i).getString("lastname").toString());
                    mPhonenumber.setText(mJSONArray.getJSONObject(i).getString("phonenumber").toString());
                    mEmail.setText(mJSONArray.getJSONObject(i).getString("email").toString());
                    mPin.setText(mJSONArray.getJSONObject(i).getString("pin").toString());
                    mProfile.setImageBitmap(decodeBase64(mJSONArray.getJSONObject(i).getString("picture_user")));
                    tmpPassword = mJSONArray.getJSONObject(i).getString("password").toString();
                }
        } catch (Exception e) {
            Log.e("Error",e.toString());

        }
    }

    private void updateprofile(String url) {
        ConnectServer handler = new ConnectServer(ProfileActivity.this);

        try {


            String result = handler.execute(url).get();
            Toast.makeText(getActivity(), result.toString(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();

        } catch (Exception e) {

        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        Log.d("Debug","onActivityResult");
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void beginCrop(Uri source) {
        Log.d("Debug","beginCrop");
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getActivity(), ProfileActivity.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        Log.d("Debug","handleCrop");
        if (resultCode == RESULT_OK) {
            Log.d("Debug","if handleCrop");
            mProfile.setImageDrawable(null);
            mProfile.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Log.d("Debug","else if handleCrop");
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.d("Debug","end handleCrop");
    }

    //การถอดรหัสรูปภาพด้วย Base64
    private Bitmap decodeBase64(String input)
    {
        if (input.length()>5) {
            byte[] decodedBytes = Base64.decode(input, 0);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }
        return null;
    }


    }

