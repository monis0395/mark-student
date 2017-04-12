package com.mark.mark;

/**
 * Created by Winner 10 on 2/19/2017.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Collections;
import java.util.List;

public class AdapterDailyPeriod extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<DailyPeriod> data= Collections.emptyList();
    DailyPeriod current;
    int currentPos=0;
    public static String HOSTNAME;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public String current_location;
    public String current_subjectName;
    public String current_teacherName;
    private SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    // create constructor to innitilize context and data sent from MainActivity
    public AdapterDailyPeriod(Context context, List<DailyPeriod> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
        HOSTNAME = context.getString(R.string.hostname);
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.container_daily, parent,false);
        MyHolder holder=new MyHolder(view);
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, MainActivity.MODE_PRIVATE);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

       // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        DailyPeriod current=data.get(position);
        myHolder.textSubjectName.setText(current.subjectName);
        myHolder.textTeacherName.setText(" - " + current.teacherName);
        myHolder.textTime.setText(current.startTime + " - " + current.endTime);
        myHolder.textLocation.setText(current.location);
        current_location = current.location;
        current_subjectName = current.subjectName;
        current_teacherName = current.teacherName;
        final String current_did = current.did;
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Your code
                new AsyncAccess().execute(current_did);
            }
        });

    }

    private class AsyncAccess extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(context);
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
                url = new URL(HOSTNAME+"access.inc.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
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
                        .appendQueryParameter("did", params[0]);
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
                return "exception";
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
                return "exception";
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
//                Toast.makeText(context, "Access Granted!", Toast.LENGTH_LONG).show();

                String NFC_location = sharedPreferences.getString("NFC_location","");
                Toast.makeText(context, NFC_location, Toast.LENGTH_LONG).show();
                if(!NFC_location.isEmpty() && NFC_location.equalsIgnoreCase(current_location)){
                    String NFC_TimeMills = sharedPreferences.getString("NFC_TimeMills","");
                    long NFC_mills = Long.parseLong(NFC_TimeMills);
                    long timemillis = System.currentTimeMillis();
                    long diff = timemillis - NFC_mills;
                    if(diff < 301){
                        Toast.makeText(context, "Diff: "+diff, Toast.LENGTH_LONG).show();
                        Intent intent =  new Intent(context, TempFP.class);
                        intent.putExtra("subject", current_subjectName);
                        intent.putExtra("teacher", current_teacherName);
                        context.startActivity(intent);
                    }
                }
                else{
                    Intent intent =  new Intent(context, NFC.class);
                    intent.putExtra("lec_location", current_location);
                    intent.putExtra("subject", current_subjectName);
                    intent.putExtra("teacher", current_teacherName);
                    context.startActivity(intent);
                }





            } else if (result.equalsIgnoreCase("false")){

                // If attendance not started in database does not match display a error message
                Toast.makeText(context, "Access denied!", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(context, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }

        }

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView textSubjectName;
        TextView textTeacherName;
        TextView textTime;
        TextView textLocation;
        LinearLayout lnrLayout;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textSubjectName= (TextView) itemView.findViewById(R.id.textSubjectName);
            textTeacherName = (TextView) itemView.findViewById(R.id.textTeacherName);
            textTime = (TextView) itemView.findViewById(R.id.textTime);
            textLocation = (TextView) itemView.findViewById(R.id.textLocation);
        }

    }

}
