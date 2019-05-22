package com.example.portfolio;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private EditText newPasscodeEntry;
    private EditText oldPasscodeEntry;
    private Button setPasscodeButton;
    private Button disablePasscodeButton;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean isPasscodeSet;


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("preferences",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isPasscodeSet = sharedPreferences.getBoolean("passcode_set", false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        newPasscodeEntry = view.findViewById(R.id.newpasscode_entry);
        oldPasscodeEntry = view.findViewById(R.id.oldpasscode_entry);

        setPasscodeButton = view.findViewById(R.id.set_passcode_button);
        disablePasscodeButton = view.findViewById(R.id.disable_passcode_button);

        if (isPasscodeSet) {
            oldPasscodeEntry.setVisibility(View.VISIBLE);
            disablePasscodeButton.setVisibility(View.VISIBLE);
        }

        // If set passcode button clicked set the passcode
        setPasscodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if already should have a passcode set
                if (isPasscodeSet) {
                    if (oldPasscodeEntry.getText().toString().equals("")) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("You must enter your old passcode!")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }})
                                .create().show();
                    }
                    // verify old passcode
                    String suppliedOldpassHash = MD5(oldPasscodeEntry.getText().toString());
                    Log.d("Oldpassocde", sharedPreferences.getString("pass_hash", ""));
                    if(suppliedOldpassHash.equals(sharedPreferences.getString("pass_hash", ""))) {
                        setPasscode(newPasscodeEntry.getText().toString());
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setMessage("Your old passcode is incorrect!")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }})
                                .create().show();
                        oldPasscodeEntry.setText("");
                        oldPasscodeEntry.invalidate();
                    }
                }
                setPasscode(newPasscodeEntry.getText().toString());
            }
        });

        disablePasscodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldPasscodeEntry.getText().length() == 0) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("You must enter your old passcode!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }})
                            .create().show();
                } else {
                    // verify old passcode
                    String suppliedOldpassHash = MD5(oldPasscodeEntry.getText().toString());
                    if(suppliedOldpassHash.equals(sharedPreferences.getString("pass_hash", ""))) {
                        disablePasscode();
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setMessage("Your old passcode is incorrect!")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }})
                                .create().show();
                        oldPasscodeEntry.setText("");
                        oldPasscodeEntry.invalidate();
                    }
                }
            }
        });

        return view;
    }

    private void setPasscode(String newpasscode) {
        Log.d("Setting passcode", newpasscode);
        String passHash = MD5(newpasscode);
        editor.putString("pass_hash", passHash);
        editor.putBoolean("passcode_set", true);
        editor.commit();
        oldPasscodeEntry.setVisibility(View.VISIBLE);
        disablePasscodeButton.setVisibility(View.VISIBLE);
        isPasscodeSet = true;
        new AlertDialog.Builder(getContext())
                .setMessage("Passcode updated!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }})
                .create().show();
        oldPasscodeEntry.setText("");
        newPasscodeEntry.setText("");
    }

    private void disablePasscode() {
        editor.putBoolean("passcode_set", false);
        editor.commit();
        oldPasscodeEntry.setVisibility(View.GONE);
        disablePasscodeButton.setVisibility(View.GONE);
        oldPasscodeEntry.setText("");
        newPasscodeEntry.setText("");
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
