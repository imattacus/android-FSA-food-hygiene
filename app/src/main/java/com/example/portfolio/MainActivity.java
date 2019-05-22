package com.example.portfolio;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.portfolio.models.FavouritesDao;
import com.example.portfolio.models.FavouritesDatabase;
import com.example.portfolio.utils.APICommunicator;
import com.example.portfolio.utils.LocationHelper;
import com.example.portfolio.utils.LocationPermissionPresenter;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, LocationPermissionPresenter {

    private final String NEARBY_TAG = "nearby_fragment";
    private final String SEARCH_TAG = "search_fragment";
    private final String FAVOURITES_TAG = "favourites_fragment";
    private final String SETTINGS_TAG = "settings_fragment";

    private FragmentManager fm = getSupportFragmentManager();
    private NearbyFragment nearbyF = new NearbyFragment();
    private SearchFragment searchF = new SearchFragment();
    private FavouritesFragment favF = new FavouritesFragment();
    private SettingsFragment settingsF = new SettingsFragment();
    private LocationHelper locHelp;
    private APICommunicator apiComm;
    private final int FINE_LOCATION_PERMISSION = 1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private FavouritesDatabase favDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {

        }

        // Set up favourites database
        favDb = Room.databaseBuilder(getApplicationContext(), FavouritesDatabase.class, "favourites-database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        // Location helper set up and permissions check
        locHelp = new LocationHelper(this, getApplicationContext());

        apiComm = new APICommunicator(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("preferences",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        ((BottomNavigationView) findViewById(R.id.bottom_navi)).setOnNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            fm.beginTransaction().add(R.id.frag_frame, nearbyF, NEARBY_TAG).commit();
        }
    }

    public LocationHelper getLocationHelper() {
        return locHelp;
    }

    public APICommunicator getApiComm() {
        return apiComm;
    }

    public FavouritesDao getDao() {
        return favDb.favouritesDao();
    }

    public FragmentManager getFm() {
        return fm;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FragmentTransaction ft = fm.beginTransaction();
        switch (menuItem.getItemId()) {
            case R.id.home:
                ft.replace(R.id.frag_frame, nearbyF);
                break;
            case R.id.search:
                ft.replace(R.id.frag_frame, searchF);
                break;
            case R.id.favourites:
                ft.replace(R.id.frag_frame, favF);
                break;
            case R.id.settings:
                ft.replace(R.id.frag_frame, settingsF);
                break;
        }
        ft.commit();
        return true;
    }

    @Override
    public void presentPermissionRequest() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setMessage("This application needs access to your location to find establishments near you, please accept the request.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestLocationPerms();
                        }
                    })
                    .create()
                    .show();
        } else {
            requestLocationPerms();
        }
    }

    private void requestLocationPerms() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case FINE_LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locHelp.attachLocManager();
                } else {
                    locHelp.permissionDenied();
                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("MainActivity", "onBackPressed Called!");
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.d("MainActivity", "On pause");
//        // Save the time app goes into background
//        editor.putString("background_time", String.valueOf(Calendar.getInstance().getTimeInMillis()));
//        editor.commit();
//    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("MainActivity", "on stop");
        editor.putBoolean("logged_in", false);
        editor.putString("background_time", String.valueOf(Calendar.getInstance().getTimeInMillis()));
        editor.commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        String lastTime = sharedPreferences.getString("background_time", "");
        if (lastTime != "") {
            Long diff = Calendar.getInstance().getTimeInMillis() - Long.valueOf(lastTime);
            Log.d("Difference is", String.valueOf(diff));
            if (diff < 10000) {
                // if the app has been gone for less than 10 seconds, log the user back in
                editor.putBoolean("logged_in", true);
                editor.commit();
            }
        }

        if (sharedPreferences.getBoolean("passcode_set", false)) {
            if (!sharedPreferences.getBoolean("logged_in", false)) {
                // ask for passcode
                Log.d("main actit pass hash", sharedPreferences.getString("pass_hash", "none found"));
                Intent passcodeIntent = new Intent(this, PasscodeActivity.class);
                startActivity(passcodeIntent);
            }
        }
    }


//    @Override
//    public void onRestart() {
//        super.onRestart();
//
//    }
}
