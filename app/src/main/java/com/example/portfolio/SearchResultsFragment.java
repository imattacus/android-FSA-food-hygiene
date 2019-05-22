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

import com.example.portfolio.models.BusinessType;
import com.example.portfolio.models.EstablishmentListResponse;
import com.example.portfolio.models.LocalAuthority;
import com.example.portfolio.utils.APICommunicator;
import com.example.portfolio.utils.APICommunicatorListener;
import com.example.portfolio.utils.ListDataProviderContract;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultsFragment extends Fragment implements APICommunicatorListener, ListDataProviderContract {

    private final String LIST_TAG = "results_list";

    private EstablishmentListFragment listFrag;
    private Toolbar toolbar;

    private EstablishmentListResponse lastResponse;

    private APICommunicator apiComm;

    public SearchResultsFragment() {
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

            // nullify any old data
            lastResponse = null;
        } else {
            listFrag = (EstablishmentListFragment) getChildFragmentManager().findFragmentByTag(LIST_TAG);
            listFrag.setDataProviderContract(this);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        apiComm = ((MainActivity) getActivity()).getApiComm();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.search_results_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getSupportFragmentManager().popBackStack();
            }
        });

        this.setHasOptionsMenu(true);

        if (lastResponse != null) {
            toolbar.setTitle(String.valueOf(lastResponse.getTotalCount()) + " results");
        }

        return view;
    }

    @Override
    public void receiveEstablishmentListResponse(EstablishmentListResponse response) {
        // received search results
        this.toolbar.setTitle(String.valueOf(response.getTotalCount()) + " results");
        if (lastResponse == null) {
            this.listFrag.setData(response.getList());
        } else {
            this.listFrag.addData(response.getList());
        }
        this.lastResponse = response;
    }

    @Override
    public void receiveLocalAuthoritiesListResponse(List<LocalAuthority> response) {

    }

    @Override
    public void receiveBusinessTypesListResponse(List<BusinessType> response) {

    }

    @Override
    public void listAtEndNeedMoreData() {
        Log.d("Search results", "list needs more data");
        if (lastResponse != null) {
            // Request next page from API communicator
            if(lastResponse.getPageNumber() < lastResponse.getTotalPages()) {
                apiComm.getEstablishmentsNextPage(lastResponse, this);
            }
        }
    }

    @Override
    public void favouritesChanged() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.clear();
        menuInflater.inflate(R.menu.results_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.results_filter:
                // Open the filtersort dialog
                //listFrag.showSortFilterDialog();
                listFrag.showSortFilterDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
