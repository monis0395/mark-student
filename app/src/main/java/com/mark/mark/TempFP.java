package com.mark.mark;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class TempFP extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private String subject;
    private String teacher;
    private String username;
    private String column;
    private String NFC_UID;
    private String lec_location;
    private String table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_fp);
        subject = getIntent().getStringExtra("subject");
        teacher = getIntent().getStringExtra("teacher");
        lec_location = getIntent().getStringExtra("lec_location");
    }

    public void scanFingerprintWrong (View arg0){
        Toast.makeText(TempFP.this,
                "Authentication unsuccessful.",
                Toast.LENGTH_LONG).show();
    }

    public void scanFingerprint(View arg0) {

//        Toast.makeText(TempFP.this,"Authentication succeeded.\nProceeding to Mark present",Toast.LENGTH_SHORT).show();
//        Toast.makeText(TempFP.this, "Marked Present!", Toast.LENGTH_LONG).show();

        String date = new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime());
        sharedPreferences = getSharedPreferences(MyPREFERENCES, MainActivity.MODE_PRIVATE);
        username = sharedPreferences.getString("username","");
        column = date;
        NFC_UID = sharedPreferences.getString("NFC_UID","");
        table = subject+"-"+teacher;

        new AysnchTempFP(TempFP.this,"present.inc.php");
    }
    private class AysnchTempFP extends GlobalAsyncTask{

        AysnchTempFP(Context context, String url){
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
                Toast.makeText(TempFP.this, "Marked Present!", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("illegal")){

                // If NFC UID not present in database does not match display a error message
                Toast.makeText(TempFP.this, "Illegal NFC tag", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(TempFP.this, "Unable to Mark Present!", Toast.LENGTH_LONG).show();

            }
//            Toast.makeText(TempFP.this, "Kuch b nahi Present!" + result, Toast.LENGTH_LONG).show();
        }

    }


}
