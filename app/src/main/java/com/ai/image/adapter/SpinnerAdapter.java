package com.ai.image.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ai.image.R;
import com.ai.image.model.HomeModel;

import java.util.ArrayList;


/**
 * Created by meysam.ab on 10/7/2017.
 */

public class SpinnerAdapter extends ArrayAdapter<HomeModel> {

    private Context ctx;
    private ArrayList<HomeModel> contentArray;

    public SpinnerAdapter(Context context, ArrayList<HomeModel> list) {
        super(context, R.layout.item_spinner, list);
        this.ctx = context;
        this.contentArray = list;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_spinner, parent, false);
        TextView textView = (TextView) row.findViewById(R.id.tvName);
        textView.setText(contentArray.get(position).getName());
        return row;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_spinner_list, parent, false);

        TextView textView = (TextView) row.findViewById(R.id.tvName);
        textView.setText(contentArray.get(position).getName());
       /* switch (position) {
            case 0:
                textView.setTextColor(Color.parseColor("#FFA000"));
                break;
            case 1:
                textView.setTextColor(Color.parseColor("#00E5FF"));
                break;
            case 2:
                textView.setTextColor(Color.parseColor("#AD1457"));
                break;
            case 3:
                textView.setTextColor(Color.parseColor("#e53935"));
                break;
        }*/


        return row;
    }


    public ArrayList<HomeModel> getData() {
        return contentArray;
    }
}