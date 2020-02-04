package com.dev.methk.arduinoandroid;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SensorAdapter extends ArrayAdapter<Sensor> {

    private final Context context;
    private final ArrayList<Sensor> elementos;
    public SensorAdapter(Context context, ArrayList<Sensor> elementos) {
        super(context, R.layout.line, elementos);
        this.context = context;
        this.elementos = elementos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line, parent,false);
        TextView bar = (TextView) rowView.findViewById(R.id.txtBar);
        TextView psi = (TextView) rowView.findViewById(R.id.txtPsi);
        TextView kpa = (TextView) rowView.findViewById(R.id.txtKpa);
        TextView nome = (TextView) rowView.findViewById(R.id.txtNome);

        bar.setText( elementos.get(position).getBAR());
        psi.setText( elementos.get(position).getPSI());
        kpa.setText( elementos.get(position).getKPA());
        nome.setText( elementos.get(position).getNome());

        return rowView;
    }


}


