package org.paxet.buscabobby;

import java.text.DecimalFormat;
import java.util.Vector;

import org.paxet.buscabobby.lista.MiAdaptador;
import org.paxet.buscabobby.sw.PeticionesSWGeocaches;
import org.paxet.geolocalizacion.Geocache;
import org.paxet.geolocalizacion.Punto;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class SeguimientoActivity extends ListActivity implements PintadorObjetos {

	private Vector<Geocache> geocaches;
	private MiAdaptador miAdaptadorLista;
	private final int DISTANCIA_MAX_LOCALIZACION = 15;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seguimiento);
		miAdaptadorLista = new MiAdaptador(SeguimientoActivity.this, new Vector<Geocache>());
		setListAdapter(miAdaptadorLista);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Estamos activos, hay que refrescar eventos
		getObjetos();
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position,
			long id) {

		super.onListItemClick(listView, view, position, id);
		Geocache obj = (Geocache) getListAdapter().getItem(position);
		String textoToast = "";
		//Recuperar la posición actual
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location loc = locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		if (loc != null) {
			Punto posActual = new Punto(loc.getLatitude(), loc.getLongitude());
			
			String dist[] = transformaKM(calcularDistancia(posActual, obj.getLocalizacion()));
			if (Integer.valueOf(dist[0]) == 0 && Integer.valueOf(dist[1]) < DISTANCIA_MAX_LOCALIZACION ) {
				Intent intent = new Intent(this, MarcaLocalizadoActivity.class);
		        intent.putExtra("descripcion", obj.getDescripcion());
		        intent.putExtra("latitud", String.valueOf(obj.getLocalizacion().getLatitud()));
		        intent.putExtra("longitud", String.valueOf(obj.getLocalizacion().getLongitud()));
		        startActivityForResult(intent, 1);
			} else {
				textoToast = getString(R.string.seguimiento_distancia) + ": " + dist[0] + "km " + dist[1] + "m " + getString(R.string.seguimiento_deposicion);
				Toast.makeText( this, textoToast, Toast.LENGTH_LONG).show();
			}
		} else {
			textoToast = "Esperando posición GPS";
			Toast.makeText( this, textoToast, Toast.LENGTH_LONG).show();
		}
		
	}
	
	@Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	if (requestCode == 1 && resultCode == RESULT_OK && data.getExtras().getBoolean("resultado")) {
    		Toast.makeText( this, R.string.marcaloc_localizadocorrectamente, Toast.LENGTH_LONG).show();
    	}
    }

	private void getObjetos() {
		try {
			//Lo primero es establecer el servidor
			SharedPreferences pref = getSharedPreferences(
					"org.paxet.buscabobby_preferences", Context.MODE_PRIVATE);
	    	String servidor = pref.getString("servidor", getString(R.string.pref_servidor_default));
	    	PeticionesSWGeocaches.getSingleton().setServer(servidor);
	    	PeticionesSWGeocaches.getSingleton().getGeocaches(this);
		} catch (Exception e) {
			/*Log.d("BuscaBobby - Seguimiento", e.getClass().toString());*/
		}
	}
	
	@Override
	public void pintaGeocaches(Vector<Geocache> caches) {
		this.geocaches = caches;
		runOnUiThread(new Runnable() {
			public void run() {
				miAdaptadorLista.setGeocaches(geocaches);
			}
		});
	}
	
	/**
	 * Calcula la distancia entre el Punto A y el B
	 * @param inicio Punto A
	 * @param destino Punto B
	 * @return Devuelve la distancia en km en formato double
	 */
	private double calcularDistancia(Punto inicio, Punto destino) {
		final double earthRadius = 6371;
	    double dLat = Math.toRadians(destino.getLatitud() - inicio.getLatitud());
	    double dLng = Math.toRadians(destino.getLongitud() - inicio.getLongitud());
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	           Math.cos(Math.toRadians(inicio.getLatitud())) * Math.cos(Math.toRadians(destino.getLatitud())) *
	           Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    return earthRadius * c;
	}
	
	/**
	 * Transforma un double con distancia en km a un vector de String
	 * @param kilometros Distancia en km
	 * @return String con los km en primera posición y metros en la segunda. Ejemplo 1,5km: [0]= 1, [1]= 500
	 */
	private String[] transformaKM(double kilometros) {
		String [] aDevolver = new String [2];
		DecimalFormat myFormatter = new DecimalFormat("0.000");
		String tmpKM = myFormatter.format(kilometros);
		String [] distCompleta = tmpKM.indexOf('.')>0?tmpKM.split("\\."):tmpKM.split("\\,");
		//Si los metros tienen tres dígitos o mas, hemos de coger los tres primeros. Si tiene dos o menos, cogemos los que hay.
		String metros = distCompleta[1].length()>=3?distCompleta[1].substring(0, 3):distCompleta[1];
		aDevolver[0] = distCompleta[0];
		aDevolver[1] = metros;
		
		return aDevolver;
	}
}
