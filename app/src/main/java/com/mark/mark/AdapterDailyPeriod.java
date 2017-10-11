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

    AdapterDailyPeriod(Context context, List<DailyPeriod> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_daily, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

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
                    new AsyncAccess(context, "check_access.php", current);
                }
            });
        } else {
            myHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.LightGrey));
        }
    }

    private class AsyncAccess extends GlobalAsyncTask {
        private final DailyPeriod current;

        AsyncAccess(Context context, String url, DailyPeriod current) {
            super(context, url);
            this.current = current;
            execute();
        }

        @Override
        Uri.Builder urlBuilder() {
            return new Uri.Builder().appendQueryParameter("did", current.did);
        }

        @Override
        void goPostExecute(String result, String content) {
            Intent intent;
            if (result.equalsIgnoreCase("true")) {
                intent = new Intent(context, NFC.class);
                intent.putExtra("lec_location", current.location);
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
