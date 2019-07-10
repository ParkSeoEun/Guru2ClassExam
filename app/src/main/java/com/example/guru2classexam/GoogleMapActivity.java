package com.example.guru2classexam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

public class GoogleMapActivity extends AppCompatActivity {

    private SupportMapFragment mMapFragment;
    private LocationManager mLocationManager;
    private LatLng mCurPosLatLng; // 현재 위치 저장 위도, 경도 변수
    private int mBtnClickIndex = 0; // 어떤 버튼의 index가 클릭 됐는지를 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        findViewById(R.id.btnMap1).setOnClickListener(mBtnClicks);
        findViewById(R.id.btnMap2).setOnClickListener(mBtnClicks);
        findViewById(R.id.btnMap3).setOnClickListener(mBtnClicks);

        mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        // 구글맵이 로딩이 완료되면 아래의 이벤트가 발생
        mMapFragment.getMapAsync(mapReadyCallback);

        // GPS 가 켜져 있는지 확인
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS 설정하는 Setting 화면으로 이동
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(i);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.
                        ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // GPS 위치를 0.1초마다 10m 간격범위 안에서 이동하면 위치를 Listener로  보내주도록 등록
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                100,10,locationListener);
        // wifi 위치를 0.1초마다 10m 간격범위 안에서 이동하면 위치를 Listener로 보내주도록 등록
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                100,10,locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // 위치 변경시 위도, 경도 정보 update 수신
            mCurPosLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Toast.makeText(getBaseContext(), "현재 위치가 갱신 되었습니다."+
                    mCurPosLatLng.latitude +", "+mCurPosLatLng.longitude, Toast.LENGTH_SHORT)
                    .show();
            // 구글맵을 현재 위치로 이동시킨다.
            mMapFragment.getMapAsync(mapReadyCallback);
            // 현재 위치로 한번만 호출하기 위해 Listener 해지
            mLocationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }

        @Override
        public void onProviderEnabled(String s) { }

        @Override
        public void onProviderDisabled(String s) { }
    };

    private View.OnClickListener mBtnClicks = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btnMap1:
                    mBtnClickIndex =1;
                    mCurPosLatLng = new LatLng(37.572793, 126.976854);
                    break;
                case R.id.btnMap2:
                    mBtnClickIndex =2;
                    mCurPosLatLng = new LatLng(35.156160, 129.161756);
                    break;
                case R.id.btnMap3:
                    mBtnClickIndex =3;
                    mCurPosLatLng = new LatLng(37.628079, 127.090457);
                    break;
            } // end switch
            mMapFragment.getMapAsync(mapReadyCallback); // map refresh
        }
    };

    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission
                    .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.
                            ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            // 현재 위치 버튼 추가
            googleMap.setMyLocationEnabled(true);
            // 줌인 줌아웃 버튼 추가
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            // 나침반
            googleMap.getUiSettings().setCompassEnabled(true);

            if(mCurPosLatLng != null) {
                // 구글맵을 위도, 경도 위치로 이동시킨다.
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
            }
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        }
    };
}
