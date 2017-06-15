package com.rakesh.demoapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.pojo.Prediction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rakesh_rajyaguru
 * on 23-Jan-17.
 */

public class BookAutoCompleteAdapter extends BaseAdapter {
    private Context mContext;
    private List<Prediction> resultList = new ArrayList<>();

    public BookAutoCompleteAdapter(Context context, List<Prediction> list) {
        mContext = context;
        resultList = list;

    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Prediction getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.geo_search_result, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.geo_search_result_text)).setText(getItem(position).getDescription());

        return convertView;
    }


}
