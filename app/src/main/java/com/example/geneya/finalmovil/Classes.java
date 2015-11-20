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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Classes extends AppCompatActivity implements ViewAdapter.RecyclerClickListner {

    //Datos de usuario
    String user;
    String type;

    List<Information> data;

    boolean sepuedecrearcurso ;

    final Context context = this;

    String classname;
    String classid;
    String enable;
    ArrayList<String> list;

    Button boton;

    List<ParseObject> ob;
    String database;
    private ProgressDialog pDialog;
    private ArrayList values;
    private ArrayList values2;

    private ViewAdapter viewAdapter;
    private RecyclerView mRecyclerView;

    public static final String MyPREFERENCES2 = "MyPrefs2" ;
    public static final String CLASS = "classKey";
    public static final String IDCLASS = "idclassKey";
    SharedPreferences sharedpreferences2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);
        sharedpreferences2 = getSharedPreferences(MyPREFERENCES2, Context.MODE_PRIVATE);

        list=new ArrayList<>();
        enable="(Abierto)";
        mRecyclerView=(RecyclerView)findViewById(R.id.recycle);
        SharedPreferences settings = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        user = settings.getString("userKey", "");
        type = settings.getString("typeKey", "");
        database = settings.getString("dbKey", "");
        boton=(Button)findViewById(R.id.button8);


        switch(type){
            case "1":
                boton.setVisibility(View.INVISIBLE);
                break;
            case "2":
                boton.setVisibility(View.VISIBLE);
                boton.setText("Agregar Curso");
                break;
            case "3":
                boton.setVisibility(View.VISIBLE);
                boton.setText("Inscribir Curso");
                break;
        }
        data=new ArrayList<>();
        new GetData().execute();
        viewAdapter= new ViewAdapter(this,data);
        viewAdapter.setRecyclerClickListner(this);
        mRecyclerView.setAdapter(viewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    public void EditClass(View v){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.custom_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        final EditText userInput2 = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput2);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                classname=userInput.getText().toString();
                                classid=userInput2.getText().toString();
                                switch(type){
                                    case "2":
                                        new CheckData().execute();
                                        break;
                                    case "3":
                                        new Register().execute();
                                        break;
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    @Override
    public void itemClick(View view, int position) {
        if(!type.equals("3")){
            String[] busqueda=values.get(position).toString().split("\n");
            busqueda=busqueda[0].split(" ");

            SharedPreferences.Editor editor = sharedpreferences2.edit();
            editor.putString(CLASS, busqueda[0]);
            editor.putString(IDCLASS, busqueda[1]);
            editor.commit();

            Toast toast1 = Toast.makeText(getApplicationContext(), "Curso "+busqueda[0]+" "+busqueda[1], Toast.LENGTH_SHORT);
            toast1.show();

            Intent i = new Intent(this, Students.class);
            startActivity(i);
        }else{
            //es alumno y ve directamente sus notas
        }
    }

    private class Register extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Classes.this);
            pDialog.setTitle("Buscando curso");
            pDialog.setMessage("Espere...");
            pDialog.setIndeterminate(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            values = new ArrayList<String>();
            values2 = new ArrayList<String>();
            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("classes");
                ob = query.find();
                for (ParseObject dato : ob) {
                    values.add(dato.get("classname")+" "+dato.get("classid"));
                    values2.add(dato.get("enabled"));
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }

            if(values.contains(classname+" "+classid) && values2.contains("(Abierto)")){
                sepuedecrearcurso=false;
            }
            else{
                sepuedecrearcurso=true;
            }
            pDialog.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(sepuedecrearcurso){
                AlertDialog.Builder builder = new AlertDialog.Builder(Classes.this);
                builder.setTitle("Error");
                builder.setMessage("La clase que busca no existe o ya no esta habilitada");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.show();
            }else{
                new Matricula().execute();
            }
        }
    }

    private class CheckData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Classes.this);
            pDialog.setTitle("Creando curso");
            pDialog.setMessage("Espere...");
            pDialog.setIndeterminate(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            values = new ArrayList<String>();
            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("classes");
                ob = query.find();
                for (ParseObject dato : ob) {
                    values.add(dato.get("classname")+" "+dato.get("classid"));
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }

            if(values.contains(classname+" "+classid)){
                sepuedecrearcurso=false;
            }
            else{
                sepuedecrearcurso=true;
            }
            pDialog.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(sepuedecrearcurso){
                new SenData().execute();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(Classes.this);
                builder.setTitle("Error");
                builder.setMessage("La clase con ese id ya existe");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.show();
            }
        }
    }

    private class SenData extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... arg0){
           ParseObject test=new ParseObject("classes");
            test.put("classname",classname);
            test.put("classid",classid);
            test.put("teacher",user);
            test.put("enabled",enable);
            test.put("students",list);
            test.saveInBackground();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            new GetData().execute();
        }

        //new GetData().execute();
    }

    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Classes.this);
            pDialog.setTitle("Buscando Cursos");
            pDialog.setMessage("Espere...");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            values = new ArrayList<String>();
            values2 = new ArrayList<String>();
            switch(type){
                case "1":
                    try {
                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("classes");
                        ob = query.find();
                        for (ParseObject dato : ob) {
                            if(dato.get("enabled").equals("(Abierto)")){
                                values.add(dato.get("classname")+" "+dato.get("classid")+"\n"+dato.get("teacher")+"\n"+dato.get("enabled"));
                            }else{
                                values2.add(dato.get("classname")+" "+dato.get("classid")+"\n"+dato.get("teacher")+"\n"+dato.get("enabled"));
                            }
                        }
                    } catch (com.parse.ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                //---------------------------------------------------------------------------------------------------------
                case "2":
                    try {
                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("classes");
                        ob = query.find();
                        for (ParseObject dato : ob) {
                           if(dato.get("teacher").equals(user)){
                               if(dato.get("enabled").equals("(Abierto)")){
                                   values.add(dato.get("classname")+" "+dato.get("classid")+"\n"+dato.get("teacher")+"\n"+dato.get("enabled"));
                               }else{
                                   values2.add(dato.get("classname")+" "+dato.get("classid")+"\n"+dato.get("teacher")+"\n"+dato.get("enabled"));
                               }
                           }
                        }
                    } catch (com.parse.ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                //----------------------------------------------------------------------------------------------------
                case "3":
                    try {
                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("classes");
                        ob = query.find();

                        for (ParseObject dato : ob) {
                            ArrayList<String> test=new ArrayList<>();
                            if(dato.getJSONArray("students")!=null) {
                                int len=dato.getJSONArray("students").length();
                                for(int i=0;i<len;i++){
                                    test.add(dato.getJSONArray("students").getString(i));
                                }

                                if(test.contains(user)){
                                    if(dato.get("enabled").equals("(Abierto)")){
                                        values.add(dato.get("classname")+" "+dato.get("classid")+"\n"+dato.get("teacher")+"\n"+dato.get("enabled"));
                                    }else{
                                        values2.add(dato.get("classname")+" "+dato.get("classid")+"\n"+dato.get("teacher")+"\n"+dato.get("enabled"));
                                    }
                                }
                            }
                        }
                    } catch (com.parse.ParseException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }


            //--------------

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            // Pass the results into ListViewAdapter.java
            values.addAll(values2);
            data.clear();
            for (int i=0; i<values.size();i++){
                Information info = new Information(values.get(i).toString());
                    data.add(info);
            }
            viewAdapter.notifyDataSetChanged();
            pDialog.dismiss();
        }
    }

    private class Matricula extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

                values = new ArrayList<String>();
                ParseQuery<ParseObject> query;
                query=ParseQuery.getQuery("classes");
                query.whereEqualTo("classname", classname);
                query.whereEqualTo("classid", classid);

                for (ParseObject dato : ob) {
                    if(dato.get("classname").equals(classname)&& dato.get("classid").equals(classid)){
                        dato.add("students",user);
                        dato.saveInBackground();
                    }
                }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            // Pass the results into ListViewAdapter.java
            new GetData().execute();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_classes, menu);
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
