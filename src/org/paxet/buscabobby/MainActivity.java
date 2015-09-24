package org.paxet.buscabobby;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class MainActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TabHost tabHost = getTabHost();

		TabSpec firstTabSpec = tabHost.newTabSpec("tid1");
		TabSpec secondTabSpec = tabHost.newTabSpec("tid2");
		TabSpec thirdTabSpec = tabHost.newTabSpec("tid3");

		/** TabSpec setIndicator() se usa para ponerle el nombre a la Pestanya. */
		/**
		 * TabSpec setContent() para establecer el contenido a la pestanya en
		 * particular.
		 */
		firstTabSpec.setIndicator(getString(R.string.tab1_titulo)).setContent(
				new Intent(this, SeguimientoActivity.class));
		secondTabSpec.setIndicator(getString(R.string.tab2_titulo)).setContent(
				new Intent(this, MapaActivity.class));
		thirdTabSpec.setIndicator(getString(R.string.tab3_titulo)).setContent(
				new Intent(this, EventosActivity.class));

		/** Anyadimos el tabSpec al TabHost */
		tabHost.addTab(firstTabSpec);
		tabHost.addTab(secondTabSpec);
		tabHost.addTab(thirdTabSpec);
		
		if (!checkInternetConnection()) {
			//TODO Enviar al Strings
			Toast.makeText( this, "Se requiere conexión a internet para funcionar", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean consumido = false;
        switch (item.getItemId()) {
        case R.id.menu_nuevo:
        	lanzarNuevo(null);
        	consumido = true;
        	break;
        case R.id.menu_settings:
        	lanzarPreferencias(null);
        	consumido = true;
        	break;
        case R.id.menu_acercade:
        	lanzarAcercaDe(null);
        	consumido = true;
        	break;
        }
        return consumido;
	}
	
	public void lanzarNuevo(View view){
		Intent i = new Intent(this, NuevoGeocacheActivity.class);
		startActivity(i);
	}
	
	public void lanzarPreferencias(View view){
		Intent i = new Intent(this, PreferenciasActivity.class);
		startActivity(i);
	}

	public void lanzarAcercaDe(View view){
		Intent i = new Intent(this, AcercaDeActivity.class);
		startActivity(i);
	}

	private boolean checkInternetConnection() {
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}
}
