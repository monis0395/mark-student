package com.mark.mark;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.Html;
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


abstract class GlobalAsyncTask extends android.os.AsyncTask<String, String, String>
{
    private Context context;
    private String URL;
    private URL url= null;
    private HttpURLConnection conn;
    private ProgressDialog pdLoading;
    private static final int CONNECTION_TIMEOUT=10000;
    private static final int READ_TIMEOUT=15000;

    abstract Uri.Builder urlBuilder();
    abstract void goPostExecute(String result, String content);

    GlobalAsyncTask(Context context, String fileName){
        this.context = context;
        StringRes sr = ((StringRes)context.getApplicationContext());
        String HOSTNAME = sr.getHOSTNAME();
        URL = HOSTNAME +""+fileName;
        pdLoading = new ProgressDialog(context);
    }

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
            url = new URL(URL);

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "URL exception\n<br>"+e;
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

            Uri.Builder builder = urlBuilder();
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
            return "HTTP exception\n<br>"+e1;
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
                return(result.toString());

            }else{

                return("unsuccessful");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "IO exception\n<br>"+e;
        } finally {
            conn.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String result) {

        //this method will be running on UI thread
        pdLoading.dismiss();

       if(!result.contains("exception") && !result.contains("unsuccessful"))
        {
            String content = conn.getContentType();
            goPostExecute(result, content);

        }else if (result.equalsIgnoreCase("unsuccessful")) {

            Toast.makeText(context, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

        }else {
           AlertDialog alertDialog = new AlertDialog.Builder(context).create();
           alertDialog.setTitle("Error");
           alertDialog.setMessage(Html.fromHtml(result)+"\n");
           alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                   new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   });
           alertDialog.show();

       }
    }

}
