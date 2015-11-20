package com.example.geneya.finalmovil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    //Datos de usuario
    String user;
    String type;
    String name;
    String lastname;
    String id;

    //High User
    TextView tx1;
    TextView tx2;
    TextView tx3;
    Button bt1;
    Button bt2;

    ImageButton img;

    List<ParseObject> ob;
    String database;
    private ArrayList values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences settings = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        user = settings.getString("userKey", "");
        type = settings.getString("typeKey", "");
        database = settings.getString("dbKey", "");
        int i= Integer.parseInt(type);
        Carga(i);

    }

    public void Carga(int i){
        RelativeLayout item = (RelativeLayout)findViewById(R.id.ventanita);
        item.removeAllViews();
        View child;
        switch (i){
            case 1:
                child = getLayoutInflater().inflate(R.layout.high_user, null);
                item.addView(child);
                InicializarHigh();
                new GetData().execute();
                tx1.setText("Bienvenido, Coordinador");
                tx2.setText("" + user);
                break;
            case 2:
                child = getLayoutInflater().inflate(R.layout.high_user, null);
                item.addView(child);
                InicializarHigh();
                new GetData().execute();
                tx1.setText("Bienvenido, Profesor");
                tx2.setText("" + user);
                break;
            case 3:
                child = getLayoutInflater().inflate(R.layout.student, null);
                item.addView(child);
                InicializarStudent();
                new GetData().execute();
                tx1.setText("Bienvenido, Estudiante");
                tx2.setText("" + user);
                break;
        }

    }

    public void InicializarStudent(){
        img=(ImageButton)findViewById(R.id.imageButton);
        tx1=(TextView)findViewById(R.id.textView6);
        tx2=(TextView)findViewById(R.id.textView7);
        tx3=(TextView)findViewById(R.id.textView8);
        bt1=(Button)findViewById(R.id.button6);
        bt2=(Button)findViewById(R.id.button7);
    }

    public void InicializarHigh(){
        tx1=(TextView)findViewById(R.id.textView3);
        tx2=(TextView)findViewById(R.id.textView4);
        tx3=(TextView)findViewById(R.id.textView5);
        bt1=(Button)findViewById(R.id.button4);
        bt2=(Button)findViewById(R.id.button5);
    }

    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            values = new ArrayList<String>();
            ParseQuery<ParseObject> query;
            query=ParseQuery.getQuery(database);
            query.whereEqualTo("username", user);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    for (ParseObject dato : objects) {
                        values.add(dato.get("name"));
                        values.add(dato.get("lastname"));
                        values.add(dato.get("objectId"));
                        tx3.setText(values.get(0)+" "+values.get(1));
                        id=""+values.get(2);
                    }

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            // Pass the results into ListViewAdapter.java
        }
    }

    public void Cursos(View v){
        Intent k = new Intent(this, Classes.class);
        startActivity(k);
    }

    public void Cerrar(View v){
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("Informacion");
        builder.setMessage("Esta seguro que desea salir?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.show();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
