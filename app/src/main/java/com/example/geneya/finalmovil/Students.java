package com.example.geneya.finalmovil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Students extends AppCompatActivity implements ViewAdapter.RecyclerClickListner {

    //Datos de usuario
    String user;
    String type;
    String classname;
    String classid;
    boolean bool;

    List<ParseObject> ob;

    private ProgressDialog pDialog;
    private ArrayList values;

    Button boton;
    List<Information> data;

    private ViewAdapter viewAdapter;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        SharedPreferences settings = getSharedPreferences("MyPrefs2", MODE_PRIVATE);
        classname = settings.getString("classKey", "");
        classid = settings.getString("idclassKey", "");
        SharedPreferences settings2 = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        user = settings2.getString("userKey", "");
        type = settings2.getString("typeKey", "");

        mRecyclerView=(RecyclerView)findViewById(R.id.recycle2);
        boton=(Button)findViewById(R.id.button80);

        switch(type){
            case "1":
                boton.setVisibility(View.INVISIBLE);
                break;
            case "2":
                boton.setVisibility(View.VISIBLE);
                boton.setText("Habilitar/Inhabilitar");
                break;
        }

        data=new ArrayList<>();
        new GetData().execute();
        viewAdapter= new ViewAdapter(this,data);
        viewAdapter.setRecyclerClickListner(this);
        mRecyclerView.setAdapter(viewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_students, menu);
        return true;
    }

    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Students.this);
            pDialog.setTitle("Buscando Alumnos");
            pDialog.setMessage("Espere...");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            values = new ArrayList<String>();
            ArrayList<String> test=new ArrayList<>();
            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("classes");
                ob = query.find();
                for (ParseObject dato : ob) {

                    if(dato.get("classname").equals(classname)&& dato.get("classid").equals(classid)){
                        if(dato.getJSONArray("students")!=null) {
                            int len=dato.getJSONArray("students").length();
                            for(int i=0;i<len;i++){
                                test.add(dato.getJSONArray("students").getString(i));
                            }
                        }
                    }
                }

                ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("student");
                ob = query2.find();
                for (ParseObject dato : ob) {
                    if(test.contains(dato.get("username"))){
                        values.add(dato.get("name")+" "+dato.get("lastname"));
                    }
                }

            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            // Pass the results into ListViewAdapter.java
            data.clear();
            for (int i=0; i<values.size();i++){
                Information info = new Information(values.get(i).toString());
                data.add(info);
            }
            viewAdapter.notifyDataSetChanged();
            pDialog.dismiss();
        }
    }

    public void Inhabilitar(View v){
        new SenData().execute();
    }

    private class SenData extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... arg0){

            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("classes");
                ob = query.find();
                for (ParseObject dato : ob) {
                    if (dato.get("classname").equals(classname) && dato.get("classid").equals(classid)) {
                        if(dato.get("enabled").equals("(Abierto)")) {
                            dato.put("enabled", "(Cerrado)");
                            bool=false;
                            dato.saveInBackground();
                        }else{
                            dato.put("enabled", "(Abierto)");
                            bool=true;
                            dato.saveInBackground();
                        }
                    }
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if(bool){
                Toast toast1 = Toast.makeText(getApplicationContext(), "Abierto exitosamente", Toast.LENGTH_SHORT);toast1.show();
            }else{
                Toast toast1 = Toast.makeText(getApplicationContext(), "Cerrado exitosamente", Toast.LENGTH_SHORT);toast1.show();
            }

        }


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

    @Override
    public void itemClick(View view, int position) {
        Toast toast1 = Toast.makeText(getApplicationContext(),""+values.get(position).toString(), Toast.LENGTH_SHORT);
        toast1.show();

    }
}
