package com.example.portfolio;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.portfolio.models.FavouritesDao;
import com.example.portfolio.utils.ListDataProviderContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment implements ListDataProviderContract {

    private static final String LIST_TAG = "favourites_list";

    private EstablishmentListFragment listFrag;
    private FavouritesDao dao;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // When fragment loaded or popped from backstack this will be null
            FragmentManager fm = getChildFragmentManager();
            listFrag = (EstablishmentListFragment) getChildFragmentManager().findFragmentByTag(LIST_TAG);
            if (listFrag != null) {
                fm.beginTransaction().remove(listFrag).commit();
            }
            listFrag = new EstablishmentListFragment();
            listFrag.setDataProviderContract(this);

            fm.beginTransaction().replace(R.id.list_frame, listFrag, LIST_TAG).commit();
        } else {
            listFrag = (EstablishmentListFragment) getChildFragmentManager().findFragmentByTag(LIST_TAG);
            listFrag.setDataProviderContract(this);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (dao == null) {
            dao = ((MainActivity) getActivity()).getDao();
        }

        listFrag.setData(dao.getAllFavourites());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.favourites_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        this.setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.clear();
        menuInflater.inflate(R.menu.favourites_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.favourites_filter:
                // Open the filtersort dialog
                //listFrag.showSortFilterDialog();
                listFrag.showSortFilterDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void listAtEndNeedMoreData() {
        // Favourites already loads all of its data, so there is nothing else here to display
    }

    @Override
    public void favouritesChanged() {
        // Reset the list data with new favourites
        listFrag.setData(dao.getAllFavourites());
    }
}
