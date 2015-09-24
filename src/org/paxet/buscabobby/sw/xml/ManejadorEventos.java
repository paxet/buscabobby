package org.paxet.buscabobby.sw.xml;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Vector;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ManejadorEventos extends DefaultHandler {

	private Vector<String> vEventos;
	private StringBuilder cadena;

	public Vector<String> getEventos() {
		return vEventos;
	}

	@Override
	public void startDocument() throws SAXException {
		cadena = new StringBuilder();
		vEventos = new Vector<String>();
	}

	@Override
	public void characters(char ch[], int comienzo, int longitud) {
		cadena.append(ch, comienzo, longitud);
	}

	@Override
	public void endElement(String uri, String nombreLocal,
			String nombreCualif) throws SAXException {
		
		if (nombreLocal.equals("return")) {
			try {
				vEventos.add(URLDecoder.decode(cadena.toString(), "UTF8"));
			} catch (UnsupportedEncodingException e) {
				/*Log.e("Asteroides", e.getMessage(), e);*/
			}
		}
		cadena.setLength(0);
	}
}
