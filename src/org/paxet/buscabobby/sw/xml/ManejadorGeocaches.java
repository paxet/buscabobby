package org.paxet.buscabobby.sw.xml;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Vector;

import org.paxet.geolocalizacion.Geocache;
import org.paxet.geolocalizacion.Punto;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ManejadorGeocaches extends DefaultHandler {

	private Vector<Geocache> vGeocaches;
	private StringBuilder cadena;
	private Geocache geocache;
	private Punto p;

	public Vector<Geocache> getGeocaches() {
		return vGeocaches;
	}

	@Override
	public void startDocument() throws SAXException {
		cadena = new StringBuilder();
		vGeocaches = new Vector<Geocache>();
		geocache = new Geocache();
	}

	@Override
	public void characters(char ch[], int comienzo, int longitud) {
		cadena.append(ch, comienzo, longitud);
	}
	
	/*@Override
	public void startElement(String uri, String nombreLocal, String nombreCualif, Attributes attributes) {
		
	}*/

	@Override
	public void endElement(String uri, String nombreLocal,
			String nombreCualif) throws SAXException {
		
		try {
			if(nombreLocal.equals("descripcion")) {
				geocache.setDescripcion(URLDecoder.decode(cadena.toString(), "UTF8"));
			} else {
				if(nombreLocal.equals("fecha")) {
					geocache.setFecha(URLDecoder.decode(cadena.toString(), "UTF8"));
				} else {
					if(nombreLocal.equals("localizacion")) {
						geocache.setLocalizacion(p);
						//objeto.setDescripcion(URLDecoder.decode(cadena.toString(), "UTF8"));
					} else {
						if(nombreLocal.equals("propietario")) {
							geocache.setPropietario(URLDecoder.decode(cadena.toString(), "UTF8"));
						} else {
							if (nombreLocal.equals("latitud")) {
								p = new Punto(0,0);
								p.setLatitud(Double.parseDouble(URLDecoder.decode(cadena.toString(), "UTF8")));
							} else {
								if (nombreLocal.equals("longitud")) {
									p.setLongitud(Double.parseDouble(URLDecoder.decode(cadena.toString(), "UTF8")));
								} else {
									if (nombreLocal.equals("return")) {
										vGeocaches.add(geocache);
										geocache = new Geocache();
									} else {
										/*Log.d("BuscaBobby - Manejador", "nombreLocal no encontrado: " + nombreLocal +
												"\nCadena vale: " + URLDecoder.decode(cadena.toString(), "UTF8"));*/
									}
								}
							}
						}
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			/*Log.e("Asteroides", e.getMessage(), e);*/
		}
		
		cadena.setLength(0);
	}
}
