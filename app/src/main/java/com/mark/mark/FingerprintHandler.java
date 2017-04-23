package com.mark.mark;

/**
 * Created by Winner 10 on 3/15/2017.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends
        FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context appContext;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private String subject;
    private String teacher;
    private String username;
    private String column;
    private String NFC_UID;
    private String lec_location;
    private String table;


    public FingerprintHandler(Context context,String subject,String teacher,String lec_location) {
        appContext = context;
        this.subject = subject;
        this.teacher = teacher;
        this.lec_location = lec_location;
    }

    public void startAuth(FingerprintManager manager,
                          FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();

        if (ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
        Toast.makeText(appContext,
                "Authentication error\n" + errString,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
        Toast.makeText(appContext,
                "Authentication help\n" + helpString,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(appContext,
                "Authentication failed.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {

        Toast.makeText(appContext,"Authentication succeeded.\n Proceeding to Mark present",
                Toast.LENGTH_LONG).show();

        String date = new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime());
        sharedPreferences = appContext.getSharedPreferences(MyPREFERENCES, MainActivity.MODE_PRIVATE);
        username = sharedPreferences.getString("username","");
        column = date;
        NFC_UID = sharedPreferences.getString("NFC_UID","");
        table = subject+"-"+teacher;

        new AsyncMark(appContext,"present.inc.php");
    }

    private class AsyncMark extends GlobalAsyncTask{

        AsyncMark(Context context, String url){
            super(context,url);
            execute();
        }

        @Override
        public Uri.Builder urlBuilder() {
            return new Uri.Builder()
                    .appendQueryParameter("username", username)
                    .appendQueryParameter("column", column)
                    .appendQueryParameter("NFC_UID", NFC_UID)
                    .appendQueryParameter("location", lec_location)
                    .appendQueryParameter("table", table);
        }

        @Override
        public void goPostExecute(String result,String content) {

            if(result.equalsIgnoreCase("true"))
            {
                Toast.makeText(appContext, "Marked Present!", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("illegal")){

                // If NFC UID not present in database does not match display a error message
                Toast.makeText(appContext, "Illegal NFC tag", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(appContext, "Unable to Mark Present!", Toast.LENGTH_LONG).show();

            }
//            Toast.makeText(TempFP.this, "Kuch b nahi Present!" + result, Toast.LENGTH_LONG).show();
        }

    }



}