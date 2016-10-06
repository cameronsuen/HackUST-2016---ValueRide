package com.hackust.taxi.taxihitchhike;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

/**
 * Created by cameron on 4/16/2016.
 */
public class AutoCompleteAdapter extends ArrayAdapter implements Filterable{

    private ArrayList<LocationMetaData> result;

    public AutoCompleteAdapter(Context context, int resId) {
        super(context, resId);
    }

    @Override
    public int getCount()
    {
        return result.size();
    }

    @Override
    public String getItem(int index)
    {
        return result.get(index).getResult();
    }

    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraints) {
                FilterResults fResult = new FilterResults();
                if (constraints != null) {
                    result = MapsActivity.autocomplete(constraints.toString());

                    fResult.values = result;
                    fResult.count = result.size();
                }

                return fResult;
            }

            @Override
            protected void publishResults(CharSequence constraints, FilterResults result) {
                if (result != null && result.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    public String getId(int index) { return result.get(index).getID(); }


}
