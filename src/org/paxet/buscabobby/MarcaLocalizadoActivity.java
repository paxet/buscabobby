package org.paxet.buscabobby;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.paxet.buscabobby.sw.xml.ManejadorRespOK;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MarcaLocalizadoActivity extends Activity {
	
	protected String latitud, longitud, descripcion, nombre;
	protected TextView tvPregunta;
	protected boolean marcado = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.marcar_localizado);
		
		SharedPreferences pref = getSharedPreferences("org.paxet.buscabobby_preferences", Context.MODE_PRIVATE);
		
		Bundle extras = getIntent().getExtras();
        descripcion = extras.getString("descripcion");
        latitud = extras.getString("latitud");
        longitud = extras.getString("longitud");
        nombre = pref.getString("nombre", getString(R.string.pref_nombre_default));
        
        tvPregunta = (TextView) findViewById(R.id.tvPregunta);
        tvPregunta.setText(getString(R.string.marcarloc_pregunta) + ": " + descripcion + "?");
	}

	public void marcarLocalizado(View view) {
		
		AsyncTask<Void, Void, Void> async = new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				try {
					SharedPreferences pref = getSharedPreferences(
							"org.paxet.buscabobby_preferences", Context.MODE_PRIVATE);
					String servidor = pref.getString("servidor", getString(R.string.pref_servidor_default));
					String url_servicio = "http://" + servidor + "/BuscaBobbySW/services/MainSW/";
					URL url = new URL(url_servicio + "geocacheLocalizado");
					HttpURLConnection conexion = (HttpURLConnection) url
							.openConnection();
					conexion.setRequestMethod("POST");
					conexion.setDoOutput(true);
					OutputStreamWriter sal = new OutputStreamWriter(
							conexion.getOutputStream());
					sal.write("lat=");
					sal.write(URLEncoder.encode(latitud, "UTF-8"));
					sal.write("&lon=");
					sal.write(URLEncoder.encode(longitud, "UTF-8"));
					sal.write("&descripcion=");
					sal.write(URLEncoder.encode(descripcion, "UTF-8"));
					sal.write("&localizador=");
					sal.write(URLEncoder.encode(String.valueOf(nombre), "UTF-8"));
					sal.flush();
					if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
						SAXParserFactory fabrica = SAXParserFactory.newInstance();
						SAXParser parser = fabrica.newSAXParser();
						XMLReader lector = parser.getXMLReader();
						ManejadorRespOK manejadorXML = new ManejadorRespOK();
						lector.setContentHandler(manejadorXML);
						lector.parse(new InputSource(conexion.getInputStream()));
						if (manejadorXML.getLista().size() != 1 || !manejadorXML.getLista().get(0).equals("OK")) {
							/*Log.e("BuscaBobby - marcaLocalizado", "No se pudo marcar como localizado");*/
							Toast.makeText( MarcaLocalizadoActivity.this, getString(R.string.marcaloc_error_respuestasw), Toast.LENGTH_LONG).show();
						} else {
							MarcaLocalizadoActivity.this.cerrarActivity(true);
						}
					} else {
						/*Log.e("BuscaBobby - marcaLocalizado", conexion.getResponseMessage());*/
					}
					conexion.disconnect();
				} catch (Exception e) {
					/*Log.e("BuscaBobby - marcaLocalizado", e.getMessage(), e);*/
				}
				
				return null;
			}
			
		};
		
		async.execute();
		
	}
	
	public void noMarcar(View view) {
		cerrarActivity(false);
	}
	
	public void cerrarActivity (boolean marc) {
		this.marcado = marc;
		
		runOnUiThread(new Runnable() {
			public void run() {
				Intent intent = new Intent();
		        intent.putExtra("resultado", marcado);
		        setResult(RESULT_OK, intent);
		        finish();
			}
		});
	}

}
