package com.mark.mark;

/**
 * Created by Winner 10 on 3/15/2017.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends
        FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context context;
    private String tagUID;
    private DailyPeriod current;


    public FingerprintHandler(Context context, String tagUID, DailyPeriod current) {
        this.context = context;
        this.tagUID = tagUID;
        this.current = current;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Toast.makeText(context,
                "Authentication error\n" + errString,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Toast.makeText(context,
                "Authentication help\n" + helpString,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context,
                "Authentication failed.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

//        Toast.makeText(context, "Authentication succeeded.\nProceeding to Mark present",Toast.LENGTH_SHORT).show();

        new AsyncMark(context, "markAT.php");
    }

    private class AsyncMark extends GlobalAsyncTask {


        AsyncMark(Context context, String url) {
            super(context, url);
            execute();
        }

        @Override
        public Uri.Builder urlBuilder() {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String date = df.format(Calendar.getInstance().getTime());
            return new Uri.Builder()
                    .appendQueryParameter("subid", current.subid)
                    .appendQueryParameter("tid", current.tid)
                    .appendQueryParameter("date", date)
                    .appendQueryParameter("start", current.START)
                    .appendQueryParameter("sid", UserDetails.sid)
                    .appendQueryParameter("tagid", tagUID)
                    .appendQueryParameter("loc", current.location);
        }

        @Override
        public void goPostExecute(String result, String content) {

            if (result.equalsIgnoreCase("true")) {
                Toast.makeText(context, "Marked Present!", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("nfc false")) {
                Toast.makeText(context, "Incorrect Location", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("false")) {
                Toast.makeText(context, "Unable to Mark Present!", Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show();
            }
            ((Activity) context).finish();
        }

    }


}