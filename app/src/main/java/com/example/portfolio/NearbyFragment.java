package com.example.portfolio;


import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.portfolio.models.BusinessType;
import com.example.portfolio.models.Establishment;
import com.example.portfolio.models.EstablishmentListResponse;
import com.example.portfolio.models.LocalAuthority;
import com.example.portfolio.utils.APICommunicator;
import com.example.portfolio.utils.APICommunicatorListener;
import com.example.portfolio.utils.ListDataProviderContract;
import com.example.portfolio.utils.LocationHelper;
import com.example.portfolio.utils.ReceiveLocationContract;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment implements APICommunicatorListener, ListDataProviderContract, ReceiveLocationContract {

    private final String LIST_TAG = "nearby_list";
    private final String NEARBY_TAG = "nearby_fragment";

    private LocationHelper lh;
    private APICommunicator apiComm;
    private EstablishmentListFragment listFrag;
    private EstablishmentListResponse lastResponse;
    private Location loc;

    public NearbyFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("NearbyFragment", "OnCreate called!");

        if (savedInstanceState == null) {
            // When fragment loaded or popped from backstack this will be null
            FragmentManager fm = getChildFragmentManager();
            listFrag = (EstablishmentListFragment) getChildFragmentManager().findFragmentByTag(LIST_TAG);
            if (listFrag != null) {
                Log.d("nearby", "restored old fragment!!!?");
                fm.beginTransaction().remove(listFrag).commit();
            }
            Log.d("nearby", "making a new listfrag");
            listFrag = new EstablishmentListFragment();
            listFrag.setDataProviderContract(this);

            fm.beginTransaction().replace(R.id.list_frame, listFrag, LIST_TAG).commit();

            // nullify any old data
            lastResponse = null;

        } else {
            // When screen rotated this happens just get existing frag
            Log.d("nearby", "saved instance not null, get frag from tag");
            listFrag = (EstablishmentListFragment) getChildFragmentManager().findFragmentByTag(LIST_TAG);
            listFrag.setDataProviderContract(this);
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (lh == null) {
            lh = ((MainActivity) getActivity()).getLocationHelper();
            lh.getLocation(this);
        }

        if (apiComm == null) {
            apiComm = ((MainActivity) getActivity()).getApiComm();
        }

        if (lastResponse == null) {

            apiComm.getNearbyEstablishments(loc, 0.5f, this);
        }
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d("nearby", "on resume");
//        listFrag = (EstablishmentListFragment) ((MainActivity) getActivity()).getSupportFragmentManager().findFragmentByTag(LIST_TAG);
//        ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.list_frame, listFrag, LIST_TAG).commit();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add toolbar
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.nearby_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        this.setHasOptionsMenu(true);

//        if (savedInstanceState == null) {
//            Log.d("nearby", "creating new list frag");
//
//
//            if (loc != null) {
//                apiComm.getNearbyEstablishments(loc, 0.5f, this);
//            }
//        } else {
//            Log.d("nearby", "accessing old list frag");
//            listFrag = (EstablishmentListFragment) ((MainActivity) getActivity()).getSupportFragmentManager().findFragmentByTag(LIST_TAG);
//        }



        return view;
    }

    @Override
    public void receiveLocation(Location loc) {
        this.loc = loc;
//        if (apiComm != null) {
//            apiComm.getNearbyEstablishments(loc, 0.5f, this);
//        }
    }

    @Override
    public void receiveLocationFailure() {
        new AlertDialog.Builder(getContext())
                .setMessage("Cannot load nearby establishments without your location.")
                .create()
                .show();
    }

    @Override
    public void receiveEstablishmentListResponse(EstablishmentListResponse response) {
        // Called when establishment list successfully received from the server
        Log.d("NearbyFragment", "Received establishment list length " + response.getList().size());
        if (lastResponse == null) {
            // first load from API
            this.listFrag.setData(response.getList());
        } else {
            this.listFrag.addData(response.getList());
        }
        lastResponse = response;
    }

    @Override
    public void receiveLocalAuthoritiesListResponse(List<LocalAuthority> response) {
    }

    @Override
    public void receiveBusinessTypesListResponse(List<BusinessType> response) {
    }

    @Override
    public void listAtEndNeedMoreData() {
        Log.d("nearbyfrag", "list needs more data");
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
        menuInflater.inflate(R.menu.nearby_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nearby_sort:
                // Open the filtersort dialog
                //listFrag.showSortFilterDialog();
                listFrag.showSortFilterDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
