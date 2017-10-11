package com.mark.mark;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class NFC extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    TextView textViewInfo;
    public static DailyPeriod current;
    public static String lec_location;
    public static String subject;
    public static String teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        setTitle("Scan NFC Tag");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//        textViewInfo = (TextView) findViewById(R.id.textInfo);
        current = (DailyPeriod) getIntent().getSerializableExtra("current");
    }

    // Triggers when Scan NFC Button clicked
    public void scanFingerprint(View arg0) {
        Intent intent = new Intent(NFC.this, ScanFPActivity.class);
        intent.putExtra("subject", subject);
        intent.putExtra("teacher", teacher);
        intent.putExtra("lec_location", lec_location);
        intent.putExtra("current", current);
        startActivity(intent);
        finish();
    }

    // list of NFC technologies detected:
    private final String[][] techList = new String[][]{
            new String[]{
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // creating pending intent:
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            String tagUID = "" + ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));

//            ((TextView) findViewById(R.id.textInfo)).setText("NFC Tag\n" + tagUID);

            Toast.makeText(NFC.this, "NFC Detected", Toast.LENGTH_LONG).show();
            Intent _intent = new Intent(NFC.this, ScanFPActivity.class);
            _intent.putExtra("current", current);
            _intent.putExtra("tagUID", tagUID);

            startActivity(intent);
            finish();
        }
    }

    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
}
