package com.mark.mark;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NFC extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    TextView textViewInfo;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static String lec_location;
    public static String subject;
    public static String teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        textViewInfo = (TextView) findViewById(R.id.textInfo);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, LoginActivity.MODE_PRIVATE);
        lec_location = getIntent().getStringExtra("lec_location");
        subject = getIntent().getStringExtra("subject");
        teacher = getIntent().getStringExtra("teacher");
        setTitle("Scan NFC Tag");
    }

    // Triggers when Scan NFC Button clicked
    public void scanFingerprint(View arg0) {
        Intent intent = new Intent(NFC.this, ScanFPActivity.class);
        intent.putExtra("subject", subject);
        intent.putExtra("teacher", teacher);
        intent.putExtra("lec_location", lec_location);
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
            ((TextView) findViewById(R.id.textInfo)).setText("NFC Tag\n" + tagUID);
            long time = System.currentTimeMillis();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("NFC_TimeMills", time + "");
            editor.putString("NFC_UID", tagUID);
            editor.commit();

            Toast.makeText(NFC.this, "NFC Detected UID: " + tagUID, Toast.LENGTH_LONG).show();
            Toast.makeText(NFC.this, "Token Generated", Toast.LENGTH_SHORT).show();
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
