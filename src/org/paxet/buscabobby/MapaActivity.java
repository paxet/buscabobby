package org.paxet.buscabobby;

import java.util.List;
import java.util.Vector;

import org.paxet.buscabobby.sw.PeticionesSWGeocaches;
import org.paxet.geolocalizacion.Geocache;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapaActivity extends MapActivity implements LocationListener, PintadorObjetos {
	
	private MapController mapController;
	private MapView mapView;
	private LocationManager manejador;
	private String proveedor;
	private Vector<Geocache> geocaches;
	private MyLocationOverlay myLocOver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);
        
        mapView = (MapView) findViewById(R.id.mapa);
		mapView.setBuiltInZoomControls(true); // Activa controles zoom
		mapView.setSatellite(true); // Activa vista satélite
		mapView.setTraffic(false); // Desactiva información de tráfico
		mapController = mapView.getController();
		mapController.setZoom(14); // Zoon 1 ver todo el mundo
		manejador = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		//crit.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
		proveedor = manejador.getBestProvider(crit, true);
		
		//Añadimos el overlay con nuestra posición
		myLocOver = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocOver);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	SharedPreferences pref = this.getSharedPreferences(
				"org.paxet.buscabobby_preferences", Context.MODE_PRIVATE);
    	String intervalo = pref.getString("intervalogps", "1");
    	String distancia = pref.getString("distanciagps", "1");
    	int inter = 5;
    	float dist = 0.5f;
    	if (intervalo.equals("0")) {
    		inter = 1000;
    	} else {
    		if (intervalo.equals("1")) {
        		inter = 5000;
        	} else {
        		if (intervalo.equals("2")) {
            		inter = 10000;
            	}
        	}
    	}
    	if (distancia.equals("0")) {
    		dist = 0.5f;
    	} else {
    		if (distancia.equals("1")) {
        		dist = 1f;
        	} else {
        		if (distancia.equals("2")) {
            		dist = 3f;
            	}
        	}
    	}
    	
    	// Activamos notificaciones de localización
    	manejador.requestLocationUpdates(proveedor, inter, dist, this);
    	//Activamos la brújula
    	myLocOver.enableCompass();
    	//Activamos nuestra posición
    	myLocOver.enableMyLocation();
    	// Estamos activos, hay que refrescar eventos
    	getObjetos();
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	// Desactivamos notificaciones para ahorrar batería
    	manejador.removeUpdates(this);
    	//Desctivamos la brújula
    	myLocOver.disableCompass();
    	//Desactivamos nuestra posición
    	myLocOver.disableMyLocation();
    }

	@Override
	public void onLocationChanged(Location arg0) {
		int lat = (int) (arg0.getLatitude() * 1E6);
		int lng = (int) (arg0.getLongitude() * 1E6);
		GeoPoint point = new GeoPoint(lat, lng);
		mapController.setCenter(point);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void getObjetos() {
		try {
			//Lo primero es establecer la URL del servidor, por si ha cambiado
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
	public void pintaGeocaches(Vector<Geocache> objs) {
		this.geocaches = objs;
		runOnUiThread(new Runnable() {
			public void run() {
				GeoPoint geoPunto;
				int lat, lon;
				for (Geocache geObj : geocaches) {
					lat = (int) (geObj.getLocalizacion().getLatitud() * 1E6);
					lon = (int) (geObj.getLocalizacion().getLongitud() * 1E6);
					geoPunto = new GeoPoint(lat, lon);
					agregarMensajeMapa(MapaActivity.this, geoPunto, geObj.getDescripcion(), geObj.getFecha() + " - " + geObj.getPropietario());
				}
			}
		});
	}
	
	private void agregarMensajeMapa(Context context, GeoPoint puntoLoc, String titulo, String descripcion) {
		MapView mapView = (MapView) findViewById(R.id.mapa);
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = context.getResources().getDrawable(R.drawable.detectado);
		SimpleItemizedOverlay itemizedoverlay = new SimpleItemizedOverlay(drawable, context);
		
		OverlayItem overlayitem = new OverlayItem(puntoLoc, titulo, descripcion);
		itemizedoverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedoverlay);
		
		mapView.invalidate();
	}

    
}
