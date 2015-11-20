package com.example.geneya.finalmovil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SignUp extends AppCompatActivity {

    EditText Ed1,Ed2,Ed3,Ed4,Ed5;
    Button bt;
    Boolean sw;
    List<ParseObject> ob;

    private ArrayList values;
    //Datos
    String Names,LastNames,User,Password,nPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Ed1=(EditText)findViewById(R.id.editText);
        Ed2=(EditText)findViewById(R.id.editText2);
        Ed3=(EditText)findViewById(R.id.editText3);
        Ed4=(EditText)findViewById(R.id.editText4);
        Ed5=(EditText)findViewById(R.id.editText5);
        bt=(Button)findViewById(R.id.button3);
        sw=false;
        new GetData().execute();
    }

    private class SenData extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... arg0){
            ParseObject test=new ParseObject("student");
            test.put("username",User);
            test.put("password",Password);
            test.put("name",Names);
            test.put("lastname",LastNames);
            test.put("usertype",3);
            test.saveInBackground();
            return null;
        }
    }

    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            values = new ArrayList<String>();
            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("student");
                ob = query.find();
                for (ParseObject dato : ob) {
                    values.add(dato.get("username"));
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }



    public void Check(View v){
        Names=Ed1.getText().toString();
        LastNames=Ed2.getText().toString();
        User=Ed3.getText().toString();
        Password=Ed4.getText().toString();
        nPassword=Ed5.getText().toString();

        if(Names.isEmpty()||LastNames.isEmpty()||User.isEmpty()||Password.isEmpty()||nPassword.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("Debe rellenar todos los campos");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.show();
        }else{
            if(!Password.equals(nPassword)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Las contraseñas deben ser iguales");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.show();
            }
            else{
                if(values.contains(User)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("El codigo ya esta registrado");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    builder.show();
                }
                else{
                    sw=true;
                    new SenData().execute();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Informacion");
                    builder.setMessage("Usuario creado exitosamente");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    });
                    builder.show();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
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
