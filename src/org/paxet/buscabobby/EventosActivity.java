package org.paxet.buscabobby;

import java.util.Vector;

import org.paxet.buscabobby.sw.PeticionesSWEventos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class EventosActivity extends Activity {

	private TextView tvEventos;
	private Vector <String> eventos;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventos);
        		
        tvEventos = (TextView) findViewById(R.id.tvEventosTexto);
	}
	
	@Override
    protected void onResume() {
    	super.onResume();
    	//Estamos activos, hay que refrescar eventos
    	getEventos();
    }
	
	private void getEventos() {
		try {
			//Lo primero es establecer el servidor
			SharedPreferences pref = getSharedPreferences(
					"org.paxet.buscabobby_preferences", Context.MODE_PRIVATE);
	    	String servidor = pref.getString("servidor", getString(R.string.pref_servidor_default));
	    	PeticionesSWEventos.getSingleton().setServer(servidor);
	    	PeticionesSWEventos.getSingleton().getEventos(this);
		} catch (Exception e) {
			/*Log.d("BuscaBobby - Seguimiento", e.getClass().toString());*/
		}
	}

	public void pintaEventos(Vector<String> events) {
		this.eventos = events;
		runOnUiThread(new Runnable() {
			public void run() {
				StringBuilder textoEventos = new StringBuilder();
				for (String evento : eventos) {
					textoEventos.insert(0, evento + ".\n\n");
				}
				tvEventos.setText(textoEventos);
			}
		});
	}
}
