package com.rakesh.demoapplication.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.Utils.ServiceUtils;
import com.rakesh.demoapplication.pojo.PojoPlace;
import com.rakesh.demoapplication.pojo.Prediction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rakesh_rajyaguru
 * on 23-Jan-17.
 */

public class BookAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<Prediction> resultList = new ArrayList<>();
    private  String API_URL = "";
    public
    BookAutoCompleteAdapter(Context context,String url) {
        mContext = context;
        API_URL=url;
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                final FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    new AsyncTask<String, Void, String>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            if (!ServiceUtils.checkConnectivity(mContext)) {
                                this.cancel(true);
                            }
                        }

                        protected String doInBackground(String... input) {
                            try {
                                String tmp = URLEncoder.encode(input[0], "UTF-8");
                                URL url = new URL(API_URL + tmp);
                                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                try {
                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                                            urlConnection.getInputStream()));
                                    StringBuilder stringBuilder = new StringBuilder();
                                    String line;
                                    while ((line = bufferedReader.readLine()) != null) {
                                        stringBuilder.append(line).append("\n");
                                    }
                                    bufferedReader.close();
                                    return stringBuilder.toString();
                                } finally {
                                    urlConnection.disconnect();
                                }
                            } catch (Exception e) {
                                Log.e("ERROR", e.getMessage(), e);
                                return null;
                            }

                        }

                        protected void onPostExecute(String response) {
                            if (response != null && response.trim().length() > 0) {
                                Gson gson = new Gson();
                                PojoPlace ps = gson.fromJson(response, PojoPlace.class);
                                List<Prediction> locations = ps.getPredictions();
                                filterResults.values = locations;
                                filterResults.count = locations.size();
                                publishResults(constraint, filterResults);
                            }
                        }
                    }.execute(constraint.toString());

                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }


}
