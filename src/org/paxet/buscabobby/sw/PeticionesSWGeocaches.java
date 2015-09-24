package org.paxet.buscabobby.sw;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.paxet.buscabobby.PintadorObjetos;
import org.paxet.buscabobby.R;
import org.paxet.buscabobby.sw.xml.ManejadorGeocaches;
import org.paxet.geolocalizacion.Geocache;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class PeticionesSWGeocaches {
	
	private static PeticionesSWGeocaches singleton = null;
	private String url_servicio;
	private PintadorObjetos actividad = null;
	
	/*No queremos construir este objeto, sólo se podrá instanciar una vez gracias a getSingleton();*/
	private PeticionesSWGeocaches() {
		
	}
	
	public void setServer(String servidor) {
		url_servicio = "http://" + servidor + "/BuscaBobbySW/services/MainSW/";
	}
	
	/*Devuelve la instancia de este objeto*/
	public static PeticionesSWGeocaches getSingleton() {
		if (singleton == null) {
			singleton = new PeticionesSWGeocaches();
		}
		
		return singleton;
	}

	/*Método para recuperar los Objetos*/
	public void getGeocaches(PintadorObjetos act) throws Exception {
		this.actividad = act;
		AsyncTask<Void, Void, Void> async = new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				Vector<Geocache> objetos;
				try {
					URL url = new URL(url_servicio + "getGeocaches");
					HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
					conexion.setRequestMethod("POST");
					conexion.setDoOutput(true);
					OutputStreamWriter sal = new OutputStreamWriter(
							conexion.getOutputStream());
					SharedPreferences pref = ((Activity)actividad).getSharedPreferences(
							"org.paxet.buscabobby_preferences", Context.MODE_PRIVATE);
			    	String nombre = pref.getString("nombre", ((Activity)actividad).getString(R.string.pref_nombre_default));
					sal.write("geocacher=");
					sal.write(URLEncoder.encode(String.valueOf(nombre), "UTF-8"));
					sal.flush();
					if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
						SAXParserFactory fabrica = SAXParserFactory.newInstance();
						SAXParser parser = fabrica.newSAXParser();
						XMLReader lector = parser.getXMLReader();
						ManejadorGeocaches manejadorXML = new ManejadorGeocaches();
						lector.setContentHandler(manejadorXML);
						lector.parse(new InputSource(conexion.getInputStream()));
						objetos = manejadorXML.getGeocaches();
					} else {
						/*Log.e("Asteroides", conexion.getResponseMessage());*/
						objetos = new Vector<Geocache>();
					}
				} catch (Exception e) {
					objetos = new Vector<Geocache>();
					/*Log.d("BuscaBobby - AsyncTask", "Error en el método de getObjetos");*/
				}
				
				actividad.pintaGeocaches(objetos);
				
				return null;
			}
			
		};
		
		async.execute();
	}

}
