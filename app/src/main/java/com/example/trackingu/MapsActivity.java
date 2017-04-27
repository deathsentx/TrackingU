package com.example.trackingu;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Location;
//import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trackingu.Modules.DirectionFinder;
import com.example.trackingu.Modules.DirectionFinderListener;
import com.example.trackingu.Modules.Route;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.BubbleIconFactory;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.trackingu.LoginActivity.getmEmail;

public class MapsActivity extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, DirectionFinderListener,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private GoogleMap mMap;
    private String mLat = null;
    private String mLong;
    private String email;
    private List<Polyline> polylinePaths = new ArrayList<>();
    //private Context context = getActivity();
    private Boolean mPause;


    //public มันสาธารณะ เราสามารถเรียกใช้งานได้จากทุก class
    public String getEmail() {
        return email;
    }

    public String getmLat() {
        return mLat;
    }

    public String getmLong() {
        return mLong;
    }


    //Google ApiClient
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationRequest mLocationRequest;
    private LatLng mFriendlocation;
    private LatLng mylatLng;
    private String mEmailfriend;

    public MapsActivity(LatLng friendlocation, String emailfriend) {
        this.mFriendlocation = friendlocation;
        this.mEmailfriend = emailfriend;
    }

    public MapsActivity() {

    }


    @Nullable
    @Override   //เริ่มต้นทำงานเองเมื่อเรียกใช้งาน class
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_maps, container, false);


        // return map ให้ Fragment
        return rootView;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = getmEmail(getActivity());
        Log.d("Debug", "StartMap");


        //รองรับ fragment ของ map โดยเฉพาะ กำหนดเป็น

        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //เรียกใช้งาน Map จาก SDK Google มา set ค่าใน loyout map
        mapFragment.getMapAsync(this);

        final Handler refreshHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //update ตำแหน่งเพื่อนทุกๆ 10 วินาที
                Log.e("read-time start", "");
                marker(mMap);

                refreshHandler.postDelayed(this, 10 * 1000);
            }
        };
        refreshHandler.postDelayed(runnable, 10 * 1000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("Debug", "onMapReady");

        mMap = googleMap;
        marker(mMap);


        // เก็บค่า Map  ไว้ในตัวแปรเพื่อเอาไปใช้งานที่อื่นได้


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("Debug", "checkif");
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {

            buildGoogleApiClient();
            //สร้างปุ่มปัจจุบันของเราา
            mMap.setMyLocationEnabled(true);
        }
        Log.d("Debug", "ok");


    }

    private void buildGoogleApiClient() {
        Log.d("Debug", "buildGoogleApiClient");
        // ตำแหน่ง ณ ปัจจุบันของเรา
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (mPause == true) {
            // ตำแหน่ง ณ ปัจจุบัน update ทุกๆ 10 วินาที
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(10000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            mGoogleApiClient.connect();
        }
        Log.d("Debug", "end buildGoogleApiClient");
    }


    private JSONArray connectserver(String url, String s) {
        ConnectServer handler = new ConnectServer(MapsActivity.this);  // อันนี้จะเรียกใช้ constructor ของ class connectserver
        JSONArray mJSONArray = null;
        try {

            if (s.equals("insert")) {
                String result = handler.execute(url, s).get(); //params[url] ,params[s]

            }
            if (s.equals("select")) {

                String result = handler.execute(url, s).get();
                JSONObject JsonObject = new JSONObject(result);
                mJSONArray = JsonObject.getJSONArray("Datafrommap");
                return mJSONArray;
            }


        } catch (Exception e) {

        }
        return null;

    }


    // ตำแหน่งของเพื่อน
    private void marker(GoogleMap mMap) {
        Log.d("Debug", "marker");

        //โหลดข้อมูลจาก Server
        try {
            JSONArray Array = connectserver("selectmap.php", "select");


            for (int i = 0; i < Array.length(); i++) {
                //Loop ปักหมุดลงตำแหน่งของเพื่อนที่โหลดมาจาก server

                mMap.addMarker(new MarkerOptions()
                        //ปัก Marker ที่พิกัด LatLng โดยใช้ภาพจาก picture_user ที่ดึงมาจากฐานข้อมูล จากนั้นก็กำหนดข้อความให้กับ Marker โดยใช้ email ที่ดึงจากฐานข้มูลเช่นกัน
                        .position(new LatLng(Double.parseDouble(Array.getJSONObject(i).getString("latitude")), Double.parseDouble(Array.getJSONObject(i).getString("longtitude"))))
                        .icon(BitmapDescriptorFactory.fromBitmap(getIconMap(Array.getJSONObject(i).getString("picture_user"))))
                        .title(Array.getJSONObject(i).getString("email"))
                        //จุดพิกัดของรูป marker
                        .anchor(0.5f, 1));

            }

            for (int i = 0; i < Array.length(); i++) {
                //Loop ข้อมูลตำแหน่งของเพื่อนที่โหลดมาจาก server

                if (mEmailfriend.equals(Array.getJSONObject(i).getString("email"))) {
                    mFriendlocation = new LatLng(Double.parseDouble(Array.getJSONObject(i).getString("latitude")),
                            Double.parseDouble(Array.getJSONObject(i).getString("longtitude")));

                    sendRequest(mylatLng, mFriendlocation);

                }
            }

        } catch (Exception e) {

        }


    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Debug", "onConnected");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            Log.d("Debug", "lat " + String.valueOf(mLastLocation.getLatitude()) + " long" + String.valueOf(mLastLocation.getLongitude()));
            Log.d("Debug", "mlast location not null");

            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Debug", "Connection Suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Dedug", "ConnectionFailed = " + connectionResult.getErrorCode());

    }


    private void startLocationUpdates() {
        Log.d("Debug", "startLocationUpdates");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }



    @Override
    public void onLocationChanged(Location location) {
        Log.d("Debug", "start onLocationchang");


        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker

        mLat = String.valueOf(location.getLatitude()); //แปลงค่า Latitude จาก String ให้ เป็น Double
        mLong = String.valueOf(location.getLongitude()); //แปลงค่า Longitude จาก String ให้ เป็น Double


        mylatLng = new LatLng(location.getLatitude(), location.getLongitude());
        //Log.d("Debug", "lat " + String.valueOf(location.getLatitude()) + " long" + String.valueOf(location.getLongitude()));


        //ปัก Marker ที่พิกัด Latlng โดยให้ Marker เปลี่ยนเป็นสี HUE_GREEN
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mylatLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));


        connectserver("insertmap.php", "insert");


        // ดึง current location friend
        marker(mMap);


    }


    private void sendRequest(LatLng mLatLng, LatLng fLatLng) {
        try {
            //แปลงค่า Latitude และ Longitude จาก String ให้เป็น Double
            String myLatLngs = String.format("%s, %s", mLatLng.latitude, mLatLng.longitude);
            String frLatLngs = String.format("%s, %s", fLatLng.latitude, fLatLng.longitude);
            //แสดงเส้นทางจากต้นทางไปยังปลายทาง
            new DirectionFinder(this, myLatLngs, frLatLngs).execute();


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes)

    {
        polylinePaths = new ArrayList<>();


        for (Route route : routes) {
            //ขนาดของเส้นทาง
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(7);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));


        }
    }

    @Override
    public void onDirectionFinderStart() {

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }

    }

    //การถอดรหัสรูปภาพด้วย Base64
    private Bitmap getIconMap(String input) {

        byte[] decodedBytes = Base64.decode(input, 0);

        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        //ย่อรูปภาพให้มีขนาด 60*60
        Bitmap bmp = getResizedBitmap(bitmap, 60, 60);

        return bmp;
    }

    //การย่อรูปภาพให้เป็นสี่เหลี่ยมจตุรัส
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPause = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPause = true;

    }


}

