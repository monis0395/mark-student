package com.mark.mark;

/**
 * Created by Winner 10 on 2/19/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

class AdapterDailyPeriod extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private LayoutInflater inflater;
    private List<DailyPeriod> data = Collections.emptyList();

    private String current_location, current_subjectName, current_teacherName;
    private SharedPreferences sharedPreferences;
    private static final String MyPREFERENCES = "MyPrefs";

    AdapterDailyPeriod(Context context, List<DailyPeriod> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_daily, parent, false);
        MyHolder holder = new MyHolder(view);
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, LoginActivity.MODE_PRIVATE);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        MyHolder myHolder = (MyHolder) holder;
        final DailyPeriod current = data.get(position);

        myHolder.textSubjectName.setText(current.subname);
        myHolder.textTeacherName.setText(" - " + current.tname);
        myHolder.textTime.setText(current.START + " - " + current.END);
        myHolder.textLocation.setText(current.location);

        if (!current.access.equalsIgnoreCase("2")) {
            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AsyncAccess(context, "access.inc.php", current.did);
                }
            });
        } else {
            myHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.LightGrey));
        }

        current_location = current.location;
        current_subjectName = current.subname;
        current_teacherName = current.tname;

    }

    private class AsyncAccess extends GlobalAsyncTask {
        private final String current_did;

        AsyncAccess(Context context, String url, String current_did) {
            super(context, url);
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
            if (result.equalsIgnoreCase("true")) {

                String NFC_UID = sharedPreferences.getString("NFC_UID", "");
                if (!NFC_UID.isEmpty()) {
                    String NFC_TimeMills = sharedPreferences.getString("NFC_TimeMills", "");
                    long NFC_mills = Long.parseLong(NFC_TimeMills);
                    long timemillis = System.currentTimeMillis();
                    long diff = (timemillis - NFC_mills) / 1000;
                    if (diff < 15) {
                        Toast.makeText(context, NFC_UID, Toast.LENGTH_LONG).show();
                        intent = new Intent(context, TempFP.class);
                    } else {
                        intent = new Intent(context, NFC.class);
                    }
                    Toast.makeText(context, "Diff: " + diff, Toast.LENGTH_LONG).show();
                } else
                    intent = new Intent(context, NFC.class);

                intent.putExtra("lec_location", current_location);
                intent.putExtra("subject", current_subjectName);
                intent.putExtra("teacher", current_teacherName);
                context.startActivity(intent);
            } else if (result.equalsIgnoreCase("false")) {

                Toast.makeText(context, "Access denied!", Toast.LENGTH_LONG).show();

            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView textSubjectName;
        TextView textTeacherName;
        TextView textTime;
        TextView textLocation;

        public MyHolder(View itemView) {
            super(itemView);
            textSubjectName = (TextView) itemView.findViewById(R.id.textSubjectName);
            textTeacherName = (TextView) itemView.findViewById(R.id.textTeacherName);
            textTime = (TextView) itemView.findViewById(R.id.textTime);
            textLocation = (TextView) itemView.findViewById(R.id.textLocation);
        }

    }

}
