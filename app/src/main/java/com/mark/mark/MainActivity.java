package com.mark.mark;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedPreferences;
    private EditText etUsername;
    private EditText etPassword;
    private String username;
    private String password;
    private StringRes sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Reference to variables
        etUsername = (EditText) findViewById(R.id.username);
        etPassword = (EditText) findViewById(R.id.password);
        sr = ((StringRes)getApplicationContext());
        sharedPreferences = getSharedPreferences(MyPREFERENCES, MainActivity.MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");
        if(!username.isEmpty()){
            Intent intent = new Intent(MainActivity.this,Success2Activity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    }

    // Triggers when LOGIN Button clicked
    public void checkLogin(View arg0) {

        // Get text from username and password field
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        // Initialize  AsyncLogin() class with username and password
        new AysnchLogin(MainActivity.this,"login.inc.php");
    }

    public void changeHost(View arg0) {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                sr.setHOSTNAME(userInput.getText().toString());
                                Toast.makeText(MainActivity.this,"HOST set successfully!",Toast.LENGTH_LONG).show();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
//        Intent intent = new Intent(Login.this,SetHost.class);
//        startActivity(intent);

    }

    private class AysnchLogin extends GlobalAsyncTask{

        AysnchLogin(Context context, String url){
            super(context,url);
            execute();
        }

        @Override
        public Uri.Builder urlBuilder() {
            return new Uri.Builder()
                    .appendQueryParameter("username", username)
                    .appendQueryParameter("password", password);
        }

        @Override
        public void goPostExecute(String result,String content) {

            if(content.equalsIgnoreCase("application/json"))
//            if(result.equalsIgnoreCase("true"))
            {
                try {
                    JSONArray jArray = new JSONArray(result);
                    JSONObject json_data = jArray.getJSONObject(0);
                    String uid = json_data.getString("uid");

//                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.putString("uid", uid);
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this,Success2Activity.class);
                    startActivity(intent);
                    MainActivity.this.finish();

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.toString() + result, Toast.LENGTH_LONG).show();
                }

            }else if (result.equalsIgnoreCase("false")){
                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
            }
        }

    }
}
