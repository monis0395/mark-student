package com.mark.mark;

import android.app.ProgressDialog;
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
    public static String HOSTNAME;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static String subject;
    public static String teacher;
    private TextView textError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_fp);
        subject = getIntent().getStringExtra("subject");
        teacher = getIntent().getStringExtra("teacher");
        HOSTNAME = getString(R.string.hostname);
//        textError = (TextView) findViewById(R.id.errortext);
    }

    public void scanFingerprintWrong (View arg0){
        Toast.makeText(TempFP.this,
                "Authentication unsuccessful.",
                Toast.LENGTH_LONG).show();
    }

    public void scanFingerprint(View arg0) {

        Toast.makeText(TempFP.this,
                "Authentication succeeded.\nProceeding to Mark present",
                Toast.LENGTH_LONG).show();
        Toast.makeText(TempFP.this, "Marked Present!", Toast.LENGTH_LONG).show();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(c.getTime());

        sharedPreferences = getSharedPreferences(MyPREFERENCES, MainActivity.MODE_PRIVATE);
        String NFC_UID = sharedPreferences.getString("NFC_UID","");
        String username = sharedPreferences.getString("username","");


        String column = date;
        String table = subject+"-"+teacher;
        String output = "NFC_UID "+NFC_UID+"\nusername "+username+" \ncolumn "+column+" \ntable "+table+"\n";
//        textError.setText(output);

        new AsyncMark().execute(username,column,NFC_UID,table);
    }

    private class AsyncMark extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(TempFP.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(HOSTNAME+"present.inc.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception MalformedURLException";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("column", params[1])
                        .appendQueryParameter("NFC_UID", params[2])
                        .appendQueryParameter("table", params[3]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception conn";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception response_code ";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();

            if(result.equalsIgnoreCase("true"))
            {
                Toast.makeText(TempFP.this, "Marked Present!", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("illegal")){

                // If NFC UID not present in database does not match display a error message
                Toast.makeText(TempFP.this, "Illegal NFC tag", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(TempFP.this, "Unable to Mark Present!", Toast.LENGTH_LONG).show();

            } else {
                textError.append(Html.fromHtml(result));
                Toast.makeText(TempFP.this, "OOPs! Something went wrong. Connection Problem."+result, Toast.LENGTH_LONG).show();

            }

        }

    }
}
