package com.example.a133935.pcdpandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class adapter extends BaseAdapter {

    int rlayout;
    Context c;
    List<csData> ldata;

    public adapter(int rlayout, Context c, List<csData> ldata) {
        this.rlayout = rlayout;
        this.c = c;
        this.ldata = ldata;
    }

    @Override
    public int getCount() {
        return ldata.size();
    }

    @Override
    public Object getItem(int position) {
        return ldata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView= LayoutInflater.from(c).inflate(R.layout.poll_card,null);
        TextView t1=(TextView)convertView.findViewById(R.id.pollname);
        t1.setText(ldata.get(position).getName());
        if(ldata.get(position).getName().equals(" ")){
            convertView.setVisibility(View.INVISIBLE);}
        return convertView;
    }

}
