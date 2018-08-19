package com.trilokynath.iscon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MyListAdapter extends ArrayAdapter<DATA> {
    List<DATA> DATAList;
    Context context;
    int resource;
    public MyListAdapter(Context context, int resource, List<DATA> DATAList) {
        super(context, resource, DATAList);
        this.context = context;
        this.resource = resource;
        this.DATAList = DATAList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(resource, null, false);
        Log.d("STATUS", "In MyListAdapter");
        TextView textViewName = view.findViewById(R.id.contact_name);
        TextView textViewTeam = view.findViewById(R.id.contact_address);
        TextView textViewMobile = view.findViewById(R.id.contact_mobile);
        DATA DATA = DATAList.get(position);
        textViewName.setText(DATA.getName());
        textViewTeam.setText(DATA.getAddress());
        textViewMobile.setText(DATA.getMobile());
        return view;
    }
}