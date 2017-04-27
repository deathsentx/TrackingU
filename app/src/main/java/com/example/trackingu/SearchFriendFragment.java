package com.example.trackingu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.trackingu.data.DataFriend;

import org.json.JSONArray;
import org.json.JSONObject;


public class SearchFriendFragment extends Fragment {

    private RadioButton mRadioButton;
    private View rootView;
    private EditText ListFriend;
    private Button AddFriend;
//    private ImageView ImageFriend;
    private RadioGroup radioGroup;
    public SearchFriendFragment() {
        //กำหนด constructor นี้ให้เป็นค่าว่าง
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_search_friend, container, false);
        ListFriend = (EditText) rootView.findViewById(R.id.friend);
//        ImageFriend = (ImageView) rootView.findViewById(R.id.friend_picture);
        AddFriend = (Button) rootView.findViewById(R.id.but_search);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        radioGroup.clearCheck();


        AddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                mRadioButton = (RadioButton) rootView.findViewById(selectedId);
                if (mRadioButton == null) {
                    Toast.makeText(getActivity(), "กรุณาเลือก", Toast.LENGTH_SHORT).show();
                } else if (mRadioButton.getText().toString().trim().equals("Pin")) {

                    connectserver("searchfriend.php", "pin");
                } else if (mRadioButton.getText().toString().trim().equals("Phone number")) {

                    connectserver("searchfriend.php", "phone");
                }
            }
        });


        return rootView;
    }

    private void connectserver(String url, String status) {
        ConnectServer handler = new ConnectServer(SearchFriendFragment.this);
        try {

            String result = handler.execute(url, status).get();

            JSONObject JsonObject = new JSONObject(result);
            JSONArray mJSONArray = JsonObject.getJSONArray("Datafromfriend");
            if (mJSONArray.toString().trim().equals("[]")) {
                Toast.makeText(getActivity(), "ไม่มีผู้ใช้", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                intent.putExtra("firstname", mJSONArray.getJSONObject(0).getString("firstname").toString());
                intent.putExtra("lastname", mJSONArray.getJSONObject(0).getString("lastname").toString());
                intent.putExtra("email", mJSONArray.getJSONObject(0).getString("email").toString());
                intent.putExtra("picture_user", mJSONArray.getJSONObject(0).getString("picture_user"));
                startActivity(intent);
            }
        } catch (Exception e) {

        }

    }

    public String getListFriend() {
        return ListFriend.getText().toString();
    }


}
