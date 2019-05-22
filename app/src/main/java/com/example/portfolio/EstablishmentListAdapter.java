package com.example.portfolio;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.portfolio.models.Establishment;
import com.example.portfolio.models.FavouritesDao;
import com.example.portfolio.models.FilterOptions;
import com.example.portfolio.utils.FavouritesContract;
import com.example.portfolio.utils.ListDataProviderContract;

import java.util.ArrayList;
import java.util.List;

public class EstablishmentListAdapter extends ArrayAdapter<Establishment> implements Filterable {

    private Context mContext;
    private List<Establishment> origList;
    private List<Establishment> dispList;
    private FavouritesContract ldpc;

    private FilterOptions filterOptions;

    public EstablishmentListAdapter(@NonNull  Context context, List<Establishment> list, FavouritesContract ldpc) {
        super(context, 0, list);
        mContext = context;
        this.origList = list;
        this.dispList = list;
        this.ldpc = ldpc;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        // Check if view is being recycled or not yet created
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.establishmentlist_item, parent, false);
        }

        final Establishment estab = dispList.get(position);

        // Set business name
        TextView nameLabel = (TextView) listItem.findViewById(R.id.list_item_name);
        nameLabel.setText(estab.getName());

        // Set business location info, either miles away if distance known or address
        TextView locLabel = (TextView) listItem.findViewById(R.id.list_item_location);
        if (estab.isHasDistance()) {
            locLabel.setText(String.valueOf(estab.getDistance()) + " miles");
        } else {
            locLabel.setText(estab.getAddress_line1() + ", " + estab.getPostcode());
        }

        // Set rating
        TextView ratingLabel = (TextView) listItem.findViewById(R.id.list_item_rating);
        if (estab.isHasRating()) {
            int rating = estab.getRating();
            if (rating == 5) {
                ratingLabel.setTextColor(mContext.getResources().getColor(R.color.colorBestRating));
            } else if (rating == 4 | rating == 3) {
                ratingLabel.setTextColor(mContext.getResources().getColor(R.color.colorGoodRating));
            } else if (rating == 2 | rating == 1) {
                ratingLabel.setTextColor(mContext.getResources().getColor(R.color.colorLowRating));
            } else {
                ratingLabel.setTextColor(mContext.getResources().getColor(R.color.colorWorstRating));
            }
            ratingLabel.setText(String.valueOf(rating));
        } else {
            // Establishment is exempt or awaiting inspection so does not have a rating!
            ratingLabel.setTextColor(mContext.getResources().getColor(R.color.colorNoRating));
            ratingLabel.setText("?");
        }

        // Favourites on click listener
        ToggleButton toggleButton = listItem.findViewById(R.id.list_item_fav_button);
        if (estab.isFavourite()) {
            toggleButton.setChecked(true);
        } else {
            toggleButton.setChecked(false);
        }

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (estab.isFavourite()) {
                    ldpc.setFavourite(estab, false);
                } else {
                    ldpc.setFavourite(estab, true);
                }
            }
        });

        return listItem;
    }

    @Override
    public Establishment getItem(int position) {
        return dispList.get(position);
    }

    @Override
    public int getCount() {
        return dispList.size();
    }

    public void setFilterOptions(FilterOptions options) {
        filterOptions = options;
    }

    public FilterOptions getFilterOptions() {
        return filterOptions;
    }

    public void resetData() {
        dispList = origList;
        notifyDataSetChanged();
    }

//    public void resetAllFilters() {
//        showExempt = true;
//    }

    @Override
    public Filter getFilter() {
        final Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Establishment> fList = new ArrayList<>();

                for (int i=0; i<origList.size(); i++) {
                    Establishment e = origList.get(i);
                    if (!filterOptions.getName().equals("") & !e.getName().contains(filterOptions.getName())) {
                        continue;
                    }
                    if (!filterOptions.isShowExempt() & e.isExempt()) {
                        continue;
                    }
                    if (!filterOptions.isRegionAll()) {
                        if (!filterOptions.getRegion().equals(e.getRegion())) {
                            continue;
                        }
                    }
                    if (!filterOptions.isBusinessTypeAll()) {
                        if (!filterOptions.getBusinessType().getName().equals(e.getBusinessType())) {
                            continue;
                        }
                    }
                    if (!filterOptions.isLocalAuthorityAll()) {
                        if (!filterOptions.getLocalAuthority().getName().equals(e.getLocalAuth())) {
                            continue;
                        }
                    }
                    fList.add(e);
                }

                results.values = fList;
                results.count = fList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count==0) {
                    notifyDataSetInvalidated();
                } else {
                    dispList = (List<Establishment>)results.values;
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }
}

