package org.paxet.buscabobby.lista;

import java.util.Vector;

import org.paxet.buscabobby.R;
import org.paxet.geolocalizacion.Geocache;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MiAdaptador extends BaseAdapter {

	private final Activity actividad;
    private Vector<Geocache> geocaches;
    
	public MiAdaptador(Activity actividad, Vector<Geocache> lista) {
        this.actividad = actividad;
        this.geocaches = lista;
    }
	
	@Override
	public int getCount() {
		return geocaches.size();
	}

	@Override
	public Object getItem(int position) {
		return geocaches.elementAt(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = actividad.getLayoutInflater();
        View view = inflater.inflate(R.layout.elemento_lista, null, true);
        TextView textView =(TextView)view.findViewById(R.id.lista_titulo);
        textView.setText(geocaches.elementAt(position).getDescripcion());
        textView =(TextView)view.findViewById(R.id.lista_subtitulo);
        textView.setText(geocaches.elementAt(position).getFecha() + " - " + geocaches.elementAt(position).getPropietario());
        ImageView imageView=(ImageView)view.findViewById(R.id.lista_icono);
        imageView.setImageResource(R.drawable.unknown);
        return view;
	}
	
	public void setGeocaches(Vector <Geocache> caches) {
		this.geocaches = caches;
		notifyDataSetChanged();
	}

}
