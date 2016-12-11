package es.schooleando.ut3ejercicio2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ruben on 18/11/16.
 */

public class DownloadURLTask extends AsyncTask<String,Integer, Bitmap>{

    private Activity main;
    private URL url;
    private HttpURLConnection conexion;
    private ProgressBar dialogo;
    private ProgressDialog progreso;
    Handler manejador;
    private int porcentaje;
    private int total = 0;

    public DownloadURLTask(Activity main) {
        this.main = main;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialogo = (ProgressBar) main.findViewById(R.id.progressBar);
        manejador = new Handler();
        progreso = new ProgressDialog(main);
        progreso.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progreso.setMessage("Descargando...");
        progreso.setCancelable(true);
        progreso.setMax(100);
        progreso.setProgress(0);//Valores del progressbar
    }

    @Override
    protected Bitmap doInBackground(String[] params) {
        Bitmap bmp = null;

        try{
            url = new URL(params[0]); //Recibimos la url solicitada
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("HEAD"); //Peticion de cabeceras
            conexion.connect(); //Conectamos
            if(conexion.getResponseCode() == 200 ){//conexion correcta
                int tamanyo = tamRecurso(); //Conseguimos el tamaño del recurso (imagen)
                if(tamanyo < 0){ //Si el tamaño devuelto es -1
                    manejador.post(new Runnable() {
                    @Override
                    public void run() {
                        dialogo.setVisibility(View.VISIBLE); //Mostramos el progressBar de la activity
                        }
                });

                    }else{ //Si nos devuelve tamaño sup a -1 y 0 mostramos barra de progreso
                        int parte = tamanyo / 2048; //Nuestra matriz de bytes sera de 2048, calculamos las partes
                        porcentaje = 100 / parte;//Calculamos el tamaño en porcentaje de cada parte
                        manejador.post(new Runnable() {
                    @Override
                    public void run() {
                        progreso.show();
                    }
                });//mostramos la barra de progreso
            }
                if (conexion.getContentType().startsWith("image/")) {//El recurso solicitado es una imagen
                    InputStream input = url.openStream(); //Flujo de datos
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int n;
                    byte[] buf = new byte[2048];
                    while ((n = input.read(buf)) > 0){//Comienza la descarga
                        if(tamanyo > 0){//Si el tamaño es sup a 0 vamos añadiendo porcentaje a la barra de progreso
                            total = total + porcentaje;
                            publishProgress(total);//barra de progreso aumenta
                        }
                        bos.write(buf,0,n);
                    }
                    input.close();//Cerramos flujo de datos

                    byte[] byteImg = bos.toByteArray();
                    bmp = BitmapFactory.decodeByteArray(byteImg, 0, byteImg.length);
                    return bmp;//retornamos el bitmap con los datos binarios
                }
            }
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;//Retornamos bitmap null
    }

    @Override
    protected void onProgressUpdate(Integer... porc) {

        super.onProgressUpdate(porc);
        int prog = porc[0].intValue();
        progreso.setProgress(prog);
    }

    @Override
    protected void onPostExecute(Bitmap res) {
        super.onPostExecute(res);
        if(res == null){ //Si el bitmap no tiene datos muestra mensaje
            Toast.makeText(main, "Error en la descarga", Toast.LENGTH_SHORT).show();
        }else{//Cargamos la imagen en el ImageView
            ImageView imgMain = (ImageView) main.findViewById(R.id.imageView);
            imgMain.setImageBitmap(res);
        }
        progreso.dismiss();
        dialogo.setVisibility(View.INVISIBLE);
    }

    public int tamRecurso(){ //consegimos el tamaño del recurso de la cabecera solicitada (HEAD)
        int tamanyo = conexion.getContentLength();
        return tamanyo;
    }
}
