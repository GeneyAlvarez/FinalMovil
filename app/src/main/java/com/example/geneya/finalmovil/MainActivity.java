package com.example.geneya.finalmovil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner sp;
    EditText ed1;
    EditText ed2;
    Button login, signup;
    String type;
    int usertype;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String USER = "userKey";
    public static final String TYPE = "typeKey";
    public static final String DB = "dbKey";
    SharedPreferences sharedpreferences;

    private ProgressDialog pDialog;
    List<ParseObject> ob;
    String database;

    private ArrayList values;
    //Info de usuario
    Boolean logged;
    String user;
    String pass;

    String prueba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp=(Spinner)findViewById(R.id.spinner);
        ed1=(EditText)findViewById(R.id.eTuser);
        ed2=(EditText)findViewById(R.id.eTpass);
        login=(Button)findViewById(R.id.button);
        signup=(Button)findViewById(R.id.button2);
        logged=false;

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                type=sp.getItemAtPosition(position).toString();
                if(type.equals("Estudiante")){
                    signup.setVisibility(View.VISIBLE);
                    database="student";
                    usertype=3;
                }
                else {
                    signup.setVisibility(View.INVISIBLE);
                    database = "high_user";
                    if(type.equals("Coordinador")){
                        usertype=1;
                    }
                    else{
                        usertype=2;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                signup.setVisibility(View.INVISIBLE);
                database = "high_user";
            }

        });

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;


        //Parse.initialize(this, "EqedFZWw1Si2Ls4M4ELK3M4EEEPWRqx9of3O9YBP", "oBMcG3Y36nB6KUFpcuBscjUtyEgc6iozNwB42trS");
    }

    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Conectando");
            pDialog.setMessage("Espere...");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            values = new ArrayList<String>();
            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(database);
                ob = query.find();
                for (ParseObject dato : ob) {
                    values.add(dato.get("username")+""+dato.get("password"));
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            // Pass the results into ListViewAdapter.java
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            if (values.contains(user+""+pass)){
                logged=true;
                Intent i = new Intent(MainActivity.this, Home.class);
                startActivity(i);

                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(USER, user);
                editor.putString(TYPE, ""+usertype);
                editor.putString(DB,database);
                editor.commit();

            }
            else{
                logged=false;
                builder.setTitle("Error");
                builder.setMessage("Usuario no existe o la contraseña esta equivocada");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.show();
            }
            pDialog.dismiss();
        }
    }


    public void Log_In(View v){
            user=ed1.getText().toString();
            pass=ed2.getText().toString();

        if(!user.isEmpty() && !pass.isEmpty()){
            new GetData().execute();

        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Error");
            builder.setMessage("No especifico alguno de los campos");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.show();
        }

    }

    public void Sign_Up(View v){
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
