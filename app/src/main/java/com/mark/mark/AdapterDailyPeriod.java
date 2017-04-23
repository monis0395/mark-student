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

    private final Context context;
    private LayoutInflater inflater;
    List<DailyPeriod> data= Collections.emptyList();
    DailyPeriod current;
    int currentPos=0;
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
                new AsyncAccess(context,"access.inc.php",current_did);
            }
        });

    }

    private class AsyncAccess extends GlobalAsyncTask
    {
        private final String current_did;
        AsyncAccess(Context context, String url,String current_did){
            super(context,url);
            this.current_did = current_did;
            execute();
        }
        @Override
        Uri.Builder urlBuilder() {
            return new Uri.Builder()
                    .appendQueryParameter("did", current_did);
        }

        @Override
        void goPostExecute(String result, String content) {
            Intent intent;
            if(result.equalsIgnoreCase("true"))
            {
//                Toast.makeText(context, "Access Granted!", Toast.LENGTH_LONG).show();

                String NFC_UID = sharedPreferences.getString("NFC_UID","");
                if(!NFC_UID.isEmpty()){
                    String NFC_TimeMills = sharedPreferences.getString("NFC_TimeMills","");
                    long NFC_mills = Long.parseLong(NFC_TimeMills);
                    long timemillis = System.currentTimeMillis();
                    long diff = (timemillis - NFC_mills) / 1000;
                    if(diff < 15){
                        Toast.makeText(context, NFC_UID, Toast.LENGTH_LONG).show();
                        intent =  new Intent(context, TempFP.class);
                    }
                    else {
                        intent =  new Intent(context, NFC.class);
                    }
                    Toast.makeText(context, "Diff: "+diff, Toast.LENGTH_LONG).show();
                }
                else
                    intent =  new Intent(context, NFC.class);

                intent.putExtra("lec_location", current_location);
                intent.putExtra("subject", current_subjectName);
                intent.putExtra("teacher", current_teacherName);
                context.startActivity(intent);
            } else if (result.equalsIgnoreCase("false")){

                // If attendance not started in database does not match display a error message
                Toast.makeText(context, "Access denied!", Toast.LENGTH_LONG).show();

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
