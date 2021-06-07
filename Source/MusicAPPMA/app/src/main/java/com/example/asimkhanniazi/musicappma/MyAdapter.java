package com.example.asimkhanniazi.musicappma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class MyAdapter extends ArrayAdapter<SongObject> {
    Context context;
    int resource;
    ArrayList<SongObject> list;

    public MyAdapter(Context context, int resource, ArrayList<SongObject> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.list=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;

        //Initializing view which will point to layout file list_item
        view= LayoutInflater.from(context).inflate(resource,parent,false);

        //Initializing TextView
        TextView fileName=(TextView)view.findViewById(R.id.textSong);
        ImageView image = (ImageView) view.findViewById(R.id.imageView);

        SongObject sdOb=list.get(position);
        //Setting the Icon and FileName
        fileName.setText(sdOb.getFileName());

        return view;
    }
}
