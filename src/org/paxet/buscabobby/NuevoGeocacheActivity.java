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
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NuevoGeocacheActivity extends Activity {
	
	protected String latitud, longitud, descripcion, propietario;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nuevo_objeto);
	}

	public void pulsadoNuevo(View view) {
		// Recuperar la posición actual
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location loc = locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		if (loc != null) {
			String desc = ((TextView) findViewById(R.id.etNuevo)).getText().toString();
			SharedPreferences pref = getSharedPreferences("org.paxet.buscabobby_preferences", Context.MODE_PRIVATE);
			String propietario = pref.getString("nombre", getString(R.string.pref_nombre_default));
			enviarNuevoObjeto(String.valueOf(loc.getLatitude()),
					String.valueOf(loc.getLongitude()), desc, propietario);
		} else {
			Toast.makeText( NuevoGeocacheActivity.this, getString(R.string.nuevo_error_noposicion), Toast.LENGTH_LONG).show();
		}
	}

	public void enviarNuevoObjeto(String lat, String lon, String desc,
			String prop) {
		
		this.latitud = lat;
		this.longitud = lon;
		this.descripcion = desc;
		this.propietario = prop;
		
		AsyncTask<Void, Void, Void> async = new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				try {
					SharedPreferences pref = getSharedPreferences(
							"org.paxet.buscabobby_preferences", Context.MODE_PRIVATE);
					String servidor = pref.getString("servidor", getString(R.string.pref_servidor_default));
					String url_servicio = "http://" + servidor + "/BuscaBobbySW/services/MainSW/";
					URL url = new URL(url_servicio + "nuevoGeocache");
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
					sal.write("&propietario=");
					sal.write(URLEncoder.encode(String.valueOf(propietario), "UTF-8"));
					sal.flush();
					if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
						SAXParserFactory fabrica = SAXParserFactory.newInstance();
						SAXParser parser = fabrica.newSAXParser();
						XMLReader lector = parser.getXMLReader();
						ManejadorRespOK manejadorXML = new ManejadorRespOK();
						lector.setContentHandler(manejadorXML);
						lector.parse(new InputSource(conexion.getInputStream()));
						if (manejadorXML.getLista().size() != 1 || !manejadorXML.getLista().get(0).equals("OK")) {
							/*Log.e("BuscaBobby - enviarNuevo", "Error en respuesta servicio Web nueva");*/
							Toast.makeText( NuevoGeocacheActivity.this, getString(R.string.nuevo_error_respuestasw), Toast.LENGTH_LONG).show();
						} else {
							NuevoGeocacheActivity.this.cerrarActivity();
						}
					} else {
						/*Log.e("BuscaBobby - enviarNuevo", conexion.getResponseMessage());*/
					}
					conexion.disconnect();
				} catch (Exception e) {
					/*Log.e("BuscaBobby - enviarNuevo", e.getMessage(), e);*/
				}
				
				return null;
			}
			
		};
		
		async.execute();
		
	}
	
	public void cerrarActivity () {
		runOnUiThread(new Runnable() {
			public void run() {
				finish();
			}
		});
	}

}
