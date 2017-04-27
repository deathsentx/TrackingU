package com.example.trackingu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trackingu.data.DataFriend;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.R.attr.value;


public class FriendFragment extends Fragment {
    private View rootView;
    private ListView listView;
    private ArrayList<DataFriend> mDataFriends;


    public FriendFragment() {
        //กำหนดให้ constructor นี้เป็นค่าว่าง
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_friend, container, false);
        mDataFriends = new ArrayList<DataFriend>();

        connectserver("selectfriend.php");

        return rootView;
    }

    private void connectserver(String url) {
        Log.d("Dedug","Start connectserver");
        ConnectServer handler = new ConnectServer(FriendFragment.this);  // อันนี้จะเรียกใช้ constructor ของ class connectserver
        JSONArray mJSONArray = null;
        try {

            String result = handler.execute(url,"listfriend").get();
            JSONObject JsonObject = new JSONObject(result);
            mJSONArray = JsonObject.getJSONArray("Datafromfriend");

            for (int i = 0; i < mJSONArray.length(); i++)
            {
                mDataFriends.add(new DataFriend(
                        mJSONArray.getJSONObject(i).getString("firstname").toString()
                        ,mJSONArray.getJSONObject(i).getString("lastname").toString()
                        ,mJSONArray.getJSONObject(i).getString("picture_user").toString()
                        ,mJSONArray.getJSONObject(i).getString("latitude").toString()
                        ,mJSONArray.getJSONObject(i).getString("longtitude").toString()
                        ,mJSONArray.getJSONObject(i).getString("email").toString()));

            }


            final AdapterFriend adapter = new AdapterFriend(getActivity(),mDataFriends);
            listView = (ListView) rootView.findViewById(R.id.FriendlistView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, final int pos, long id) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("ต้องการเส้นทางหรือไม่?")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    LatLng  friendlocation = new LatLng(mDataFriends.get(pos).getmLat(),mDataFriends.get(pos).getmLong());
                                    MapsActivity fragment = new MapsActivity(friendlocation, mDataFriends.get(pos).getmEmailfriend());

                                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                                    fragmentTransaction.commit();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();

                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("ต้องการลบเพื่อนหรือไม่?")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                   deletefriend("deletefriend.php",mDataFriends.get(position).getmEmailfriend());
                                    adapter.remove(adapter.getItem(position));
                                    adapter.notifyDataSetChanged();
                                    listView.setAdapter(adapter);


                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                    return true;

                }
            });

        } catch (Exception e) {
            Log.e("Error",e.toString());

        }
    }

    private void deletefriend(String url,String emailfriend) {
        ConnectServer handler = new ConnectServer(FriendFragment.this);

        try {

            String result = handler.execute(url,"deletefriend",emailfriend).get();
            Toast.makeText(getActivity(), result.toString(), Toast.LENGTH_SHORT).show();


        } catch (Exception e) {

        }
    }


    public class AdapterFriend  extends ArrayAdapter<DataFriend> {
        private Context mContext;
        private ArrayList<DataFriend> mDataFriends;

        public AdapterFriend(Context context,  ArrayList<DataFriend> DataFriends){
            super(context, R.layout.fragment_friend , DataFriends);
            this.mContext = context;
            mDataFriends = DataFriends;

        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.activity_adapter_friend, parent, false);

                ImageView picture = (ImageView)view.findViewById(R.id.friend_picture);
                TextView FirstName = (TextView) view.findViewById(R.id.friend_firstname);
                TextView LastName = (TextView) view.findViewById(R.id.friend_lastname);
                FirstName.setText(mDataFriends.get(position).getmFirstname());
                LastName.setText(mDataFriends.get(position).getmLastname());
                picture.setImageBitmap(decodeBase64(mDataFriends.get(position).getmImage()));

            }
            return view;

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

}
