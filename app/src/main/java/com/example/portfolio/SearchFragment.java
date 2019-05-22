package com.example.portfolio;


import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.portfolio.models.BusinessType;
import com.example.portfolio.models.EstablishmentListResponse;
import com.example.portfolio.models.LocalAuthority;
import com.example.portfolio.utils.APICommunicator;
import com.example.portfolio.utils.APICommunicatorListener;
import com.example.portfolio.utils.LocationHelper;
import com.example.portfolio.utils.ReceiveLocationContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements APICommunicatorListener, ReceiveLocationContract {

    private final String BUSINESS_TYPES_LIST = "business_types_list";
    private final String LOCAL_AUTH_LIST = "local_auth_list";

    private APICommunicator apiComm;
    private LocationHelper lh;

    private Location loc;
    private boolean canUseLocation;

    private EditText businessNameEntry;
    private EditText locationEntry;
    private CheckBox useLocationCheck;
    private TextView radiusLabel;
    private SeekBar radiusBar;
    private TextView radiusValueLabel;
    private Spinner authoritySpinner;
    private Spinner ratingNumSpinner;
    private Spinner ratingMethodSpinner;
    private Spinner businessTypeSpinner;
    private Spinner sortBySpinner;
    private CheckBox showAdvancedCheck;
    private CheckBox localAuthAllCheck;
    private CheckBox businessTypesAllCheck;
    private CheckBox ratingAllCheck;
    private RelativeLayout advancedView;

    private boolean useDeviceLocation = false;

    private ArrayList<BusinessType> businessTypesList;
    private ArrayList<LocalAuthority> localAuthorityList;
    private ArrayAdapter<BusinessType> businessTypeAdpt;
    private ArrayAdapter<LocalAuthority> localAuthorityAdpt;

    private String[] ratingNums;
    private String[] ratingMethodsValues;
    private String[] sortByValues;


    public SearchFragment() {
        // Required empty public constructor
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.businessTypesList = new ArrayList<BusinessType>();
            this.localAuthorityList = new ArrayList<LocalAuthority>();
            canUseLocation = false;
        } else {
            this.businessTypesList = (ArrayList<BusinessType>) savedInstanceState.getSerializable(BUSINESS_TYPES_LIST);
            this.localAuthorityList = (ArrayList<LocalAuthority>) savedInstanceState.getSerializable(LOCAL_AUTH_LIST);
        }

        if (businessTypeAdpt == null) {
            this.businessTypeAdpt = new ArrayAdapter<BusinessType>(getContext(), android.R.layout.simple_spinner_dropdown_item, businessTypesList);
        }

        if (localAuthorityAdpt == null) {
            this.localAuthorityAdpt = new ArrayAdapter<LocalAuthority>(getContext(), android.R.layout.simple_spinner_dropdown_item,localAuthorityList);
        }



        ratingNums = getResources().getStringArray(R.array.rating_values);
        ratingMethodsValues = getResources().getStringArray(R.array.rating_methods_values);
        sortByValues = getResources().getStringArray(R.array.api_sort_methods_values);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lh = ((MainActivity) getActivity()).getLocationHelper();
        apiComm = ((MainActivity) getActivity()).getApiComm();

        apiComm.getBusinessTypes(this);
        apiComm.getLocalAuthoritiesList(this);

        lh.getLocation(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.search_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        this.setHasOptionsMenu(true);

        // Get references to all the views
        businessNameEntry = (EditText) view.findViewById(R.id.businessname_entry);
        locationEntry = (EditText) view.findViewById(R.id.location_entry);
        useLocationCheck = (CheckBox) view.findViewById(R.id.location_check);
        radiusLabel = (TextView) view.findViewById(R.id.radius_label);
        radiusBar = (SeekBar) view.findViewById(R.id.radiusBar);
        radiusValueLabel = (TextView) view.findViewById(R.id.radius_value_label);
        authoritySpinner = (Spinner) view.findViewById(R.id.authority_spinner);
        ratingNumSpinner = (Spinner) view.findViewById(R.id.rating_num_spinner);
        ratingMethodSpinner = (Spinner) view.findViewById(R.id.rating_method_spinner);
        businessTypeSpinner = (Spinner) view.findViewById(R.id.business_type_spinner);
        sortBySpinner = (Spinner) view.findViewById(R.id.sortby_spinner);
        showAdvancedCheck = (CheckBox) view.findViewById(R.id.search_show_advanced);
        ratingAllCheck = (CheckBox) view.findViewById(R.id.search_rating_check);
        businessTypesAllCheck = (CheckBox) view.findViewById(R.id.search_businesstype_check);
        localAuthAllCheck = (CheckBox) view.findViewById(R.id.search_authority_check);
        advancedView = (RelativeLayout) view.findViewById(R.id.search_advanced_view);


        // Set initial value and position of radius value label
        int initialSeekbarValue = (radiusBar.getProgress() * (radiusBar.getWidth() - 2 * radiusBar.getThumbOffset()))/radiusBar.getMax();
        radiusValueLabel.setText(String.valueOf(radiusBar.getProgress()));
        radiusValueLabel.setX(radiusBar.getX()+initialSeekbarValue+radiusBar.getThumbOffset() / 2);

        // make seek bar textview follow the scrubber of bar
        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                radiusValueLabel.setText("" + progress);
                radiusValueLabel.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        useLocationCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CompoundButton) v).isChecked()) {
                    // Is checked so disable location entry and show search radius info
                    useDeviceLocation = true;
                    locationEntry.setHint(R.string.using_my_location);
                    locationEntry.setText("");
                    locationEntry.setEnabled(false);
                    radiusLabel.setVisibility(View.VISIBLE);
                    radiusBar.setVisibility(View.VISIBLE);
                    radiusValueLabel.setVisibility(View.VISIBLE);
                } else {
                    // Is unchecked so hide the things
                    useDeviceLocation = false;
                    locationEntry.setHint(R.string.value_optional);
                    locationEntry.setText("");
                    locationEntry.setEnabled(true);
                    radiusLabel.setVisibility(View.GONE);
                    radiusBar.setVisibility(View.GONE);
                    radiusValueLabel.setVisibility(View.GONE);
                }
            }
        });

        // Set spinner adapters
        businessTypeSpinner.setAdapter(businessTypeAdpt);
        authoritySpinner.setAdapter(localAuthorityAdpt);


        // If its the first time view loaded, set up default check values
        if (savedInstanceState == null) {
            showAdvancedCheck.setChecked(false);
            businessTypesAllCheck.setChecked(true);
            localAuthAllCheck.setChecked(true);
            ratingAllCheck.setChecked(true);
            advancedView.setVisibility(View.GONE);
            businessTypeSpinner.setEnabled(false);
            ratingMethodSpinner.setEnabled(false);
            ratingNumSpinner.setEnabled(false);
            authoritySpinner.setEnabled(false);
        }

        // Set up on click listeners for checkboxes
        showAdvancedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show advanced view
                int visibility = showAdvancedCheck.isChecked() ? View.VISIBLE : View.GONE;
                advancedView.setVisibility(visibility);
            }
        });

        businessTypesAllCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                businessTypeSpinner.setEnabled(!businessTypesAllCheck.isChecked());
            }
        });

        ratingAllCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingNumSpinner.setEnabled(!ratingAllCheck.isChecked());
                ratingMethodSpinner.setEnabled(!ratingAllCheck.isChecked());
            }
        });

        localAuthAllCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authoritySpinner.setEnabled(!localAuthAllCheck.isChecked());
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the business type list and local authority list so don't have to get them again
        outState.putSerializable(BUSINESS_TYPES_LIST, businessTypesList);
        outState.putSerializable(LOCAL_AUTH_LIST, localAuthorityList);
    }

    private void doSearch() {
        Map<String, String> params = new HashMap<>();
        // See what values have been set for the search

        // Business Name
        String businessName = businessNameEntry.getText().toString();
        if(!businessName.equals("")) {
            params.put("name", businessName);
        }

        // Check what location they have supplied
        if (useDeviceLocation && canUseLocation) {
            params.put("longitude", String.valueOf(loc.getLongitude()));
            params.put("latitude", String.valueOf(loc.getLatitude()));
            // Get desired search radius
            params.put("maxDistanceLimit", String.valueOf(radiusBar.getProgress()));
        } else if (useDeviceLocation && !canUseLocation) {
            new AlertDialog.Builder(getContext())
                    .setMessage("Cannot load nearby establishments without your location.")
                    .create()
                    .show();
            return;
        } else if (!locationEntry.getText().toString().equals("")) {
            params.put("address", locationEntry.getText().toString());
        }

        // If using advanced search settings
        if (showAdvancedCheck.isChecked()) {
            // Local Authority
            if (!localAuthAllCheck.isChecked()){
                LocalAuthority selectedLa = (LocalAuthority) authoritySpinner.getSelectedItem();
                params.put("localAuthorityId", String.valueOf(selectedLa.getApiId()));
            }
            // Rating
            if (!ratingAllCheck.isChecked()) {
                params.put("ratingKey", ratingNums[ratingNumSpinner.getSelectedItemPosition()]);
                params.put("ratingOperatorKey", ratingMethodsValues[ratingMethodSpinner.getSelectedItemPosition()]);
            }
            // Business type
            if (!businessTypesAllCheck.isChecked()) {
                BusinessType selectedBt = (BusinessType) businessTypeSpinner.getSelectedItem();
                params.put("businessTypeId", String.valueOf(selectedBt.getId()));
            }
        }

        // Sort by method
        params.put("sortOptionKey", sortByValues[sortBySpinner.getSelectedItemPosition()]);

        // Tell API communicator to do the search, and switch to search results fragment which will receive the results.
        SearchResultsFragment resultsFrag = new SearchResultsFragment();

        apiComm.getAdvancedSearchEstablishments(params, resultsFrag);

        FragmentManager fm = ((MainActivity) getActivity()).getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.frag_frame, resultsFrag)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
    }

    @Override
    public void receiveEstablishmentListResponse(EstablishmentListResponse response) {
        // Should never happen
        Log.wtf("SearchFragment", "Received EstablishmentList response?!?!");
    }

    @Override
    public void receiveLocalAuthoritiesListResponse(List<LocalAuthority> response) {
        // Populate localauthorities spinner with the received data
        Log.d("SearchFrag", "Received local authorities list");
        if (localAuthorityList != null) {
            localAuthorityList.clear();
            localAuthorityList.addAll(response);
        } else {
            localAuthorityList = new ArrayList<>();
            localAuthorityList.addAll(response);
        }
        if (localAuthorityAdpt != null) {
            localAuthorityAdpt.notifyDataSetChanged();
        }
    }

    @Override
    public void receiveBusinessTypesListResponse(List<BusinessType> response) {
        Log.d("SearchFrag", "Received business types list");
        // Populate businesstypes spinner with the received data
        if (businessTypesList != null) {
            businessTypesList.clear();
            businessTypesList.addAll(response);
        } else {
            businessTypesList = new ArrayList<>();
            businessTypesList.addAll(response);
        }

        if (businessTypeAdpt != null) {
            businessTypeAdpt.notifyDataSetChanged();
        }
    }

    @Override
    public void receiveLocation(Location loc) {
        this.loc = loc;
        this.canUseLocation = true;
    }

    @Override
    public void receiveLocationFailure() {
        this.canUseLocation = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.submit_search:
                // Search button clicked so perform search
                doSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
