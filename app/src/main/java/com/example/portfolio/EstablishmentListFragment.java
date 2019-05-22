package com.example.portfolio;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.portfolio.models.Establishment;
import com.example.portfolio.models.FavouritesDao;
import com.example.portfolio.models.FilterOptions;
import com.example.portfolio.utils.FavouritesContract;
import com.example.portfolio.utils.FilterResultsContract;
import com.example.portfolio.utils.ListDataProviderContract;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EstablishmentListFragment extends Fragment implements FilterResultsContract, FavouritesContract {

    private static final String STATE_ITEMS = "establishment_items";
    private static final String STATE_FILTER_OPTIONS = "filter_options";

    private String return_tag;

    private ArrayList<Establishment> data;
    private EstablishmentListAdapter listAdpt;
    private ListView listView;
    //private MapViewFragment mapView;


    public final int AUTOLOAD_THRESHOLD = 10;

    private boolean isLoading;

    private ListDataProviderContract dataProviderContract;

    private FilterOptions filterOptions;

    public EstablishmentListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore from saved state if list is already been provided.
        if (savedInstanceState != null) {
            Log.d("ListFrag", "Reloading data and filter options from previously saved state");
            this.data = (ArrayList<Establishment>) savedInstanceState.getSerializable(STATE_ITEMS);
            this.filterOptions = (FilterOptions) savedInstanceState.getSerializable(STATE_FILTER_OPTIONS);
        }

        if (this.data == null) {
            Log.d("Listfrag", "Creating new empty list data arraylist");
            this.data = new ArrayList<Establishment>();
        }

        if (listAdpt == null) {
            Log.d("listfrag", "list adapter created");
            listAdpt = new EstablishmentListAdapter(getContext(), data, this);
        }

        if (filterOptions == null) {
            filterOptions = new FilterOptions();
        }

        isLoading =  true;
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_establishment_list, container, false);

        listView = (ListView) view.findViewById(R.id.est_list);
        listView.setAdapter(listAdpt);
        listView.setOnItemClickListener(getOnClickListener());

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && dataProviderContract != null) {
                    if (totalItemCount - AUTOLOAD_THRESHOLD <= firstVisibleItem + visibleItemCount) {
                        isLoading = true;
                        dataProviderContract.listAtEndNeedMoreData();
                    }
                }
            }
        });

        return view;
    }

    private AdapterView.OnItemClickListener getOnClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Item clicked", "Item clicked!!");
                Establishment clickEst = (Establishment) listAdpt.getItem(position);
                EstablishmentDetailFragment detailFragment = new EstablishmentDetailFragment();

                Log.d("ClickedOn", clickEst.getName());

                Bundle bundle = new Bundle();
                bundle.putSerializable("establishment", clickEst);
                detailFragment.setArguments(bundle);

                FragmentManager fm = ((MainActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frag_frame, detailFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        };
    }

    @Override
    public void setFavourite(Establishment e, boolean fav) {
        e.setFavourite(fav);
        if (fav) {
            ((MainActivity) getActivity()).getDao().insertFavourite(e);
        } else {
            ((MainActivity) getActivity()).getDao().deleteFavourite(e);
        }
        dataProviderContract.favouritesChanged();
    }

    public void setData(List<Establishment> newData) {
        Log.d("ListFrag", "data set");
        if (this.data == null) {
            Log.w("listfrag", "list datalist was null");
            this.data = new ArrayList<Establishment>();
        }
        this.data.clear();
        this.data.addAll(newData);
        this.isLoading = false;
        if (listAdpt != null) {
            listAdpt.notifyDataSetChanged();
        } else {
            Log.e("listfrag", "adapter is null");
        }
    }

    public void addData(List<Establishment> newData) {
        Log.d("Listfrag", "more data added");
        // Add data to the existing list and make sure the list view is updated,  and filters reapplied
        this.data.addAll(newData);
        listAdpt.notifyDataSetChanged();
        listAdpt.setFilterOptions(filterOptions);
        listAdpt.getFilter().filter(null);
        this.isLoading = false;
    }

    public void setReturnTag (String returntag) {
        this.return_tag = returntag;
    }

    public void showSortFilterDialog() {
        FilterSortDialogFragment frag = new FilterSortDialogFragment();
        frag.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_filter_sort_dialog);
        frag.setCallback(this);
        frag.setOptions(filterOptions);
        FragmentManager fm = ((MainActivity) getActivity()).getSupportFragmentManager();
        frag.show(fm, "filtersortdialog");
    }

    public List<Establishment> getData() {
        return data;
    }

    public void filter() {
        listAdpt.getFilter().filter(null);
    }

    public void reset() {
        listAdpt.resetData();
    }

    public void setDataProviderContract(ListDataProviderContract dataProviderContract) {
        this.dataProviderContract = dataProviderContract;
    }

    @Override
    public void acceptFilterResults(FilterOptions options) {
        // Received new filter results from the dialog fragment
        filterOptions = options;
        // Update list adapter with new filter options and refilter
        listAdpt.setFilterOptions(filterOptions);
        listAdpt.getFilter().filter(null);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save list contents to bundle
        state.putSerializable(STATE_ITEMS, data);
        state.putSerializable(STATE_FILTER_OPTIONS, filterOptions);
    }

}
