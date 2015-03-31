package com.example.marcos.convertidor;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.os.Handler;
import android.os.Message;
import android.net.http.AndroidHttpClient;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.*;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
 Button bt;
 EditText edit;
 TextView resultado1;
String uno,dos;
    String resultado;
    RadioButton c;
    RadioButton f;

    private Handler puente = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Mostramos el mensage recibido del servido en pantalla

            Toast.makeText(getApplicationContext(), "RECIVIENDO",
                    Toast.LENGTH_LONG).show();
            resultado1.setText(String.valueOf(msg.obj));

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Relacionamos con XML como ya sabemos
        bt = (Button)findViewById(R.id.enviar);
        edit = (EditText)findViewById(R.id.texto);
        resultado1 =(TextView)findViewById(R.id.respuesta);
        c=(RadioButton)findViewById(R.id.c);
        f=(RadioButton)findViewById(R.id.f);
       c.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dos=String.valueOf(c.getText());
               f.setChecked(false);
           }
       });

        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dos=String.valueOf(f.getText());
                c.setChecked(false);
            }
        });


        //Añadimos el Listener
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hay que hacerlo dentro del Thread
                //No me dejaba tocar la Clase de Network
                //directamente en el hilo principal
                Thread thr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Enviamos el texto escrito a la funcion
                        EnviarDatos(edit.getText().toString(),dos);
                    }
                });
                //Arrancamos el Hilo
                thr.start();
            }
        });
    }

    private void EnviarDatos(String dato ,String datos){
        //Utilizamos la clase Httpclient para conectar
        HttpClient httpclient = new DefaultHttpClient();
        //Utilizamos la HttpPost para enviar lso datos
        //A la url donde se encuentre nuestro archivo receptor
        HttpPost httppost = new HttpPost("http://192.168.56.1/"+"marcos/index.php");
        try {
            //Añadimos los datos a enviar en este caso solo uno
            //que le llamamos de nombre 'a'
            //La segunda linea podría repetirse tantas veces como queramos
            //siempre cambiando el nombre ('a')
            List<NameValuePair> postValues = new ArrayList<NameValuePair>(2);
            postValues.add(new BasicNameValuePair("a", dato));
            postValues.add(new BasicNameValuePair("b",datos));
            //Encapsulamos
            httppost.setEntity(new UrlEncodedFormEntity(postValues));
            //Lanzamos la petición
            HttpResponse respuesta = httpclient.execute(httppost);
            //Conectamos para recibir datos de respuesta
            HttpEntity entity = respuesta.getEntity();
            //Creamos el InputStream como su propio nombre indica
            InputStream is = entity.getContent();
            //Limpiamos el codigo obtenido atraves de la funcion
            //StreamToString explicada más abajo
             resultado= StreamToString(is);

            //Enviamos el resultado LIMPIO al Handler para mostrarlo
            Message sms = new Message();

            sms.obj = resultado;

            puente.sendMessage(sms);
        }catch (IOException e) {
            //TODO Auto-generated catch block
        }
    }

    //Funcion para 'limpiar' el codigo recibido
    public String StreamToString(InputStream is) {
        //Creamos el Buffer
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            //Bucle para leer todas las líneas
            //En este ejemplo al ser solo 1 la respuesta
            //Pues no haría falta
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //retornamos el codigo límpio
        return sb.toString();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
