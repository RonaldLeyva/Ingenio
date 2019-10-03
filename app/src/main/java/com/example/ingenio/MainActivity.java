package com.example.ingenio;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnEnviar;
    EditText editUsuario;
    EditText editPassword;
    String UsuStr;
    String PassStr;
    String Resultado;
    ImageButton btnLlamar;

    public static String URL = "http://192.168.0.12/ws/wsBuscar.asmx";
    public static String NAMESPACE = "http://tempuri.org/";
    public static String METHOD_NAME = "ValidaUsuario";
    public static String SOAP_ACTION = "http://tempuri.org/ValidaUsuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLlamar = (ImageButton) findViewById(R.id.imageButton2);

        editUsuario =(EditText)findViewById(R.id.txtUsuario);
        editPassword=(EditText)findViewById(R.id.txtPassword);

        btnEnviar = (Button)findViewById(R.id.btnIngresar);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsuStr = editUsuario.getText().toString();
                PassStr = editPassword.getText().toString();
                if(UsuStr.isEmpty() || PassStr.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Por favor ingresa un usuario y contraseña", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DiaglogoProgresoCircular(v);
                    new Login().execute(UsuStr, PassStr);
                }
            }
        });
        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numerotel = "7441815264";//llamar.getText().toString();
                    String llamarA = "tel:" + numerotel;
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(llamarA)));
            }
        });
    }

    public void DiaglogoProgresoCircular(View view) {
        final ProgressDialog progressDialogCircular = ProgressDialog.show(MainActivity.this, "Iniciando", "Espere por favor...",
                true);
        progressDialogCircular.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // progreso del circulo 2 segundos de espera
                    Thread.sleep(2000);
                } catch (Exception e) {

                }
                //permite cerrar el dialog después de pasar 2 segundos en caso de que el usuario o la contraseña sean incorrectos
                progressDialogCircular.dismiss();
            }
        }).start();
    }

    private class Login extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo usuario = new PropertyInfo();
            usuario.setName("Usuario");
            usuario.setType(String.class);
            usuario.setValue(strings[0].toString());
            request.addProperty(usuario);

            PropertyInfo pass = new PropertyInfo();
            pass.setName("Password");
            pass.setType(String.class);
            pass.setValue(strings[1].toString());
            request.addProperty(pass);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    Resultado = result.getProperty(0).toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
            return Resultado;
        }

        protected void onPostExecute(String result){
            if(result.equals("true"))
            {
                Intent caratula = new Intent(MainActivity.this, TabeddCaratula.class);
                Toast.makeText(MainActivity.this, "Bienvenido "+ UsuStr.toString(), Toast.LENGTH_LONG).show();
                startActivity(caratula);
            }
            else{
                Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectas", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

