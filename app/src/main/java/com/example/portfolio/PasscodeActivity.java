package com.example.portfolio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PasscodeActivity extends AppCompatActivity {

    private EditText passcodeEntry;
    private Button passcodeButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        passcodeEntry = findViewById(R.id.passcode_entry);
        passcodeButton = findViewById(R.id.passcode_button);
        sharedPreferences = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        passcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check supplied password
                if (passcodeEntry.getText().toString().equals("")) {
                    new AlertDialog.Builder(getApplicationContext())
                            .setMessage("You must enter a passcode!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }})
                            .create().show();
                } else {
                    String suppliedpasshash = MD5(passcodeEntry.getText().toString());
                    Log.d("supplid pash has", suppliedpasshash);
                    Log.d("actual pass hash", sharedPreferences.getString("pass_hash", "none found"));
                    if (sharedPreferences.getString("pass_hash", "").equals(suppliedpasshash)) {
                        // Success
                        editor.putBoolean("logged_in", true);
                        editor.commit();
                        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainIntent);
                    } else {
                        passcodeEntry.invalidate();
                        passcodeEntry.setText("");
                        new AlertDialog.Builder(PasscodeActivity.this)
                                .setMessage("Incorrect passcode!")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }})
                                .create().show();
                    }
                }
            }
        });

    }

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        } catch(java.io.UnsupportedEncodingException ex){
        }
        return null;
    }

}
