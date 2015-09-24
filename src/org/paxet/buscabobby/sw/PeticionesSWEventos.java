package org.paxet.buscabobby.sw;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.paxet.buscabobby.EventosActivity;
import org.paxet.buscabobby.sw.xml.ManejadorEventos;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.os.AsyncTask;

public class PeticionesSWEventos {
	
	private static PeticionesSWEventos singleton = null;
	private String url_servicio;
	private EventosActivity evAct = null;
	
	/*No queremos construir este objeto, sólo se podrá instanciar una vez gracias a getSingleton();*/
	private PeticionesSWEventos() {
		
	}
	
	public void setServer(String servidor) {
		url_servicio = "http://" + servidor + "/BuscaBobbySW/services/MainSW/";
	}
	
	/*Devuelve la instancia de este objeto*/
	public static PeticionesSWEventos getSingleton() {
		if (singleton == null) {
			singleton = new PeticionesSWEventos();
		}
		
		return singleton;
	}

	/*Método para recuperar los Objetos*/
	public void getEventos(EventosActivity evAc) throws Exception {
		this.evAct = evAc;
		AsyncTask<Void, Void, Void> async = new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				Vector<String> eventos;
				try {
					URL url = new URL(url_servicio + "getEventos");
					HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
					conexion.setRequestMethod("POST");
					conexion.setDoOutput(true);
					OutputStreamWriter sal = new OutputStreamWriter(
							conexion.getOutputStream());
					//sal.write("maximo=");
					//sal.write(URLEncoder.encode(String.valueOf(cantidad), "UTF-8"));
					sal.flush();
					if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
						SAXParserFactory fabrica = SAXParserFactory.newInstance();
						SAXParser parser = fabrica.newSAXParser();
						XMLReader lector = parser.getXMLReader();
						ManejadorEventos manejadorXML = new ManejadorEventos();
						lector.setContentHandler(manejadorXML);
						lector.parse(new InputSource(conexion.getInputStream()));
						eventos = manejadorXML.getEventos();
					} else {
						/*Log.e("Asteroides", conexion.getResponseMessage());*/
						eventos = new Vector<String>();
					}
				} catch (Exception e) {
					eventos = new Vector<String>();
					/*Log.d("BuscaBobby - AsyncTask", "Error en el método de getEventos");*/
				}
				evAct.pintaEventos(eventos);
				return null;
			}
			
		};
		
		async.execute();
	}
}
