package com.example.trackingu;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.trackingu.LoginActivity.getmEmail;


/**
 * Created by aonauma on 11/6/2016 AD.
 */

public class ConnectServer extends AsyncTask<String, Integer, String> {
    private RequestBody mFormBody;
    Response response = null;

    private  MainActivity mMainActivity;
    private  MapsActivity mMapsActivity;
    private  RegisterActivity mRegisterActivity;
    private  LoginActivity mLoginActivity;
    private  ForgotPassword mForgotPassword;
    private  ProfileActivity mProfileActivity;
    private  FriendFragment mFriendFragment;
    private SearchFriendFragment mSearchFriendFragment;
    private  AddFriendActivity mAddFriendActivity;

    //private  String hostURL = "http://trackingu.tk/trackinguAPI/";
   private  String hostURL = "http://172.20.10.9/trackinguapi/";

    public  ConnectServer(MapsActivity mapactivity){ //class นี้จะทำงานก็ต่อเมื่อเรียกใช้ constructor

        this.mMapsActivity = mapactivity; //constructor

    }

    public  ConnectServer(RegisterActivity registeractivity){

        this.mRegisterActivity = registeractivity;

    }
    public  ConnectServer(LoginActivity loginactivity){

        this.mLoginActivity = loginactivity;

    }
    public  ConnectServer(ForgotPassword forgotPassword){

        this.mForgotPassword = forgotPassword;

    }
    public  ConnectServer(ProfileActivity profileActivity){

        this.mProfileActivity = profileActivity;

    }
    public  ConnectServer(FriendFragment friendFragment){

        this.mFriendFragment = friendFragment;

    }
    public  ConnectServer(SearchFriendFragment searchFriendFragment){
        this.mSearchFriendFragment = searchFriendFragment;

    }
    public  ConnectServer(AddFriendActivity addFriendActivity){
        this.mAddFriendActivity = addFriendActivity;

    }
    public  ConnectServer(MainActivity mainActivity){
        this.mMainActivity = mainActivity;

    }


    @Override
    protected String doInBackground(String... params) {

        OkHttpClient client = new OkHttpClient();//Library okhttp
         //params[] , ค่าที่ส่งมาให้ตัว params มี params[0] เท่ากับ url และ params[1] เท่ากับ สถานะ
        String url = hostURL+ params[0];
        try { // try คือ การประกาศว่าคำสั่งที่อยู่ใน { จะมีการทำงานผิดปกติ อาจเกิด error ได้ ถ้า error จะเรียกใช้ catch
            if(mMapsActivity!=null) {
                if (params[1].equals("insert")) {


                    mFormBody = new FormBody.Builder() //Formbody เป็นรูปแบบที่เราจะส่งข้อมูลไปให้ server
                            .add("latitude", mMapsActivity.getmLat()) //เพิ่มข้อมูลที่จะส่งไป ตัวแรก latitude ตัวแปร php ที่ประกาศไว้รับข้อมูล และตัวที่ 2 คือค่าที่ส่งขึ้นไป
                            .add("longtitude", mMapsActivity.getmLong())
                            .add("email", mMapsActivity.getEmail())
                            .build();
                } else {
                    mFormBody = new FormBody.Builder()
                            .add("email", mMapsActivity.getEmail())
                            .build();
                }
            }

            if(mRegisterActivity!=null){
                mFormBody = new FormBody.Builder()
                        .add("email", mRegisterActivity.getmEmail())
                        .add("password", mRegisterActivity.getmPassword())
                        .add("firstname", mRegisterActivity.getmFirstName())
                        .add("lastname", mRegisterActivity.getmLastName())
                        .add("phonenumber", mRegisterActivity.getmPhonenumber())
                        .add("picture_user", mRegisterActivity.getmImage())
                        .build();

            }

            if (mLoginActivity != null) {

                mFormBody = new FormBody.Builder()
                        .add("email", mLoginActivity.getmEmail())
                        .add("password", mLoginActivity.getmPassword())
                        .build();

            }
            if (mForgotPassword != null) {

                mFormBody = new FormBody.Builder()
                        .add("phonenumber", mForgotPassword.getmPhonenumber())
                        .add("password", mForgotPassword.getmPassword())
                        .build();

            }
            if(mProfileActivity!=null) {
                mFormBody = new FormBody.Builder()
                        .add("email", getmEmail(mProfileActivity.getContext()))
                        .add("password", mProfileActivity.getmPassword())
                        .add("firstname", mProfileActivity.getmFirstName())
                        .add("lastname", mProfileActivity.getmLastName())
                        .add("phonenumber", mProfileActivity.getmPhonenumber())
                        .add("picture_user", mProfileActivity.getmProfile())
                        .add("pin", mProfileActivity.getmPin())

                        .build();

            }
            if(mFriendFragment!=null) {
                if(params[1]=="listfriend") {
                    mFormBody = new FormBody.Builder()
                            .add("email", getmEmail(mFriendFragment.getContext()))
                            .build();
                }else if(params[1]=="deletefriend"){
                    mFormBody = new FormBody.Builder()
                            .add("email", getmEmail(mFriendFragment.getContext()))
                            .add("emailfriend",params[2].trim())

                            .build();

                }


            }
            if(mSearchFriendFragment!=null) {
                if(params[1].trim()=="pin"){
                    mFormBody = new FormBody.Builder()
                            .add("pin", mSearchFriendFragment.getListFriend())
                            .build();
                }else if(params[1].trim()=="phone"){
                    mFormBody = new FormBody.Builder()
                            .add("phonenumber", mSearchFriendFragment.getListFriend())
                            .build();
                }


            }

        if(mAddFriendActivity!=null) {
            mFormBody = new FormBody.Builder()
                    .add("email",getmEmail(mAddFriendActivity))
                    .add("emailfriend",mAddFriendActivity.getEmailFriend())

                    .build();

        }
            if(mMainActivity!=null) {
                mFormBody = new FormBody.Builder()
                        .add("email", getmEmail(mMainActivity))
    //                    .add("picture_user", mMainActivity.getmProfile())
                        .build();

            }



                Request request = new Request.Builder().url(url).post(mFormBody).build(); //เรียกใช้งาน server และส่งค่าในรูปแบบ post ไปให้

            response = client.newCall(request).execute();//รับค่าที่ตอบกลับมาจาก server

            String mResult = response.body().string(); //เอาค่าที่ตอบกลับมาเก็บไว้ mResult
            return mResult; // return ค่า mResult ไปให้ class ที่เรียกใช้งาน


        } catch (IOException e) { //ถ้า erorr  จะเข้าฟังชันนี้
            e.printStackTrace();
        }

        return null;
    }


}
