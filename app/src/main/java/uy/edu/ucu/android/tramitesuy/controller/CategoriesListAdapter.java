package uy.edu.ucu.android.tramitesuy.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.tramitesuy.R;

/**
 * Created by juliorima on 28/06/2015.
 */
public class CategoriesListAdapter extends ArrayAdapter<Proceeding> implements Filterable {
    private final Context mContext;

    private List<Proceeding> mItems;
    private List<Proceeding> mItemsFiltered;
    private ItemFilter mFilter = new ItemFilter();

    public CategoriesListAdapter(Context context, int resource, List<Proceeding> items) {
        super(context, resource, items);

        this.mContext = context;
        this.mItems = new ArrayList<>();
        this.mItems.addAll(items);

        this.mItemsFiltered = new ArrayList<>();
        this.mItemsFiltered.addAll(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.proceeding_list_item, parent, false);

        Proceeding proceeding = mItemsFiltered.get(position);

        TextView nameTextView = (TextView) rowView.findViewById(R.id.proceeding_title);
        TextView descTeamTextView = (TextView) rowView.findViewById(R.id.proceeding_desc);

        nameTextView.setText(proceeding.getTitle());
        descTeamTextView.setText(proceeding.getDescription());

        return rowView;
    }

    @Override
    public int getCount() {
        return mItemsFiltered.size();
    }

    @Override
    public Proceeding getItem(int position) {
        return mItemsFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setmItems(List<Proceeding> mItems) {
        this.mItems.clear();
        this.mItems.addAll(mItems);
        this.mItemsFiltered.clear();
        this.mItemsFiltered.addAll(mItems);
        notifyDataSetChanged();
    }


    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mItemsFiltered.clear();
            mItemsFiltered.addAll((List<Proceeding>) results.values);
            notifyDataSetChanged();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            ArrayList<Proceeding> filteredArrayNames = new ArrayList<>();

            constraint = constraint.toString().toLowerCase();
            for (Proceeding item : mItems) {
                String proceedingTitle = item.getTitle();
                if (proceedingTitle.toLowerCase().contains(constraint.toString())) {
                    filteredArrayNames.add(item);
                }

            }

            results.count = filteredArrayNames.size();
            results.values = filteredArrayNames;
            return results;
        }
    }

}


