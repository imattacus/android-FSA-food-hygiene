package com.example.portfolio;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.portfolio.models.BusinessType;
import com.example.portfolio.models.EstablishmentListResponse;
import com.example.portfolio.models.FilterOptions;
import com.example.portfolio.models.LocalAuthority;
import com.example.portfolio.utils.APICommunicator;
import com.example.portfolio.utils.APICommunicatorListener;
import com.example.portfolio.utils.FilterResultsContract;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FilterSortDialogFragment extends DialogFragment implements APICommunicatorListener {

    private FilterResultsContract callback;
    private FilterOptions options;

    private APICommunicator apiComm;

    private EditText businessName;
    private Spinner businessTypeSpinner;
    private CheckBox exemptCheck;
    private Spinner authoritySpinner;
    private Spinner regionSpinner;
    private CheckBox businessTypeCheck;
    private CheckBox authorityCheck;
    private CheckBox regionCheck;

    private List<BusinessType> businessTypesList;
    private List<LocalAuthority> localAuthorityList;
    private ArrayAdapter<BusinessType> businessTypeAdpt;
    private ArrayAdapter<LocalAuthority> localAuthorityAdpt;
    private String[] regions;


    public FilterSortDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If options not already supplied create new from default
        if (options == null) {
            options = new FilterOptions();
        }

        businessTypesList = new ArrayList<BusinessType>();
        localAuthorityList = new ArrayList<LocalAuthority>();

        // Get data for spinners
        businessTypeAdpt = new ArrayAdapter<BusinessType>(getContext(), android.R.layout.simple_spinner_dropdown_item, businessTypesList);
        localAuthorityAdpt = new ArrayAdapter<LocalAuthority>(getContext(), android.R.layout.simple_spinner_dropdown_item, localAuthorityList);

        apiComm = ((MainActivity) getActivity()).getApiComm();
        apiComm.getLocalAuthoritiesList(this);
        apiComm.getBusinessTypes(this);

        regions = getResources().getStringArray(R.array.regions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter_sort_dialog, container, false);
        getDialog().setTitle("Filter results");

        businessName = view.findViewById(R.id.filter_business_name_value);
        businessTypeSpinner = view.findViewById(R.id.filter_businesstype_spinner);
        exemptCheck = view.findViewById(R.id.filter_exempt_checkbox);
        authoritySpinner = view.findViewById(R.id.filter_authority_spinner);
        regionSpinner = view.findViewById(R.id.filter_region_spinner);
        businessTypeCheck = view.findViewById(R.id.filter_businesstype_check);
        authorityCheck = view.findViewById(R.id.filter_authority_check);
        regionCheck = view.findViewById(R.id.filter_region_check);

        businessName.setText(options.getName());

        businessTypeCheck.setChecked(options.isBusinessTypeAll());
        businessTypeSpinner.setEnabled(!options.isBusinessTypeAll());
        businessTypeSpinner.setAdapter(businessTypeAdpt);

        authorityCheck.setChecked(options.isLocalAuthorityAll());
        authoritySpinner.setEnabled(!options.isLocalAuthorityAll());
        authoritySpinner.setAdapter(localAuthorityAdpt);

        regionCheck.setChecked(options.isRegionAll());
        regionSpinner.setEnabled(!options.isRegionAll());

        exemptCheck.setChecked(options.isShowExempt());

        if (options.getLocalAuthorityPosition() != -1) {
            //Update spinner with last position
            authoritySpinner.setSelection(options.getLocalAuthorityPosition());
        }

        if (options.getBusinessTypePosition() != -1) {
            businessTypeSpinner.setSelection(options.getBusinessTypePosition());
        }

        if (options.getRegionPosition() != -1) {
            regionSpinner.setSelection(options.getRegionPosition());
        }

        Button applyButton = view.findViewById(R.id.filter_apply_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Apply filters button clicked: save options - tell callback and close dialog
                updateOptions();
                callback.acceptFilterResults(options);
                dismiss();
            }
        });

        Button resetButton = view.findViewById(R.id.filter_reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset filters button clicked - tell callback and close dialog
                callback.acceptFilterResults(new FilterOptions());
                dismiss();
            }
        });

        businessTypeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                businessTypeSpinner.setEnabled(!businessTypeCheck.isChecked());
            }
        });

        regionCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regionSpinner.setEnabled(!regionCheck.isChecked());
            }
        });

        authorityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authoritySpinner.setEnabled(!authorityCheck.isChecked());
            }
        });

        return view;
    }

    public void setCallback(FilterResultsContract cb) {
        this.callback = cb;
    }

    public void setOptions(FilterOptions options) {
        this.options = options;
    }

    private void updateOptions() {
        options.setName(businessName.getText().toString());
        options.setBusinessTypeAll(businessTypeCheck.isChecked());
        options.setShowExempt(exemptCheck.isChecked());
        options.setLocalAuthorityAll(authorityCheck.isChecked());
        options.setRegionAll(regionCheck.isChecked());
        if (!options.isRegionAll()) {
            options.setRegion(regions[regionSpinner.getSelectedItemPosition()]);
            options.setRegionPosition(regionSpinner.getSelectedItemPosition());
        }
        if (!options.isLocalAuthorityAll()) {
            options.setLocalAuthority((LocalAuthority) authoritySpinner.getSelectedItem());
            options.setLocalAuthorityPosition(authoritySpinner.getSelectedItemPosition());
        }
        if (!options.isBusinessTypeAll()) {
            options.setBusinessType((BusinessType) businessTypeSpinner.getSelectedItem());
            options.setBusinessTypePosition(businessTypeSpinner.getSelectedItemPosition());
        }
    }


    @Override
    public void receiveEstablishmentListResponse(EstablishmentListResponse response) {

    }

    @Override
    public void receiveLocalAuthoritiesListResponse(List<LocalAuthority> response) {
        localAuthorityList.clear();
        localAuthorityList.addAll(response);
        localAuthorityAdpt.notifyDataSetChanged();
    }

    @Override
    public void receiveBusinessTypesListResponse(List<BusinessType> response) {
        businessTypesList.clear();
        businessTypesList.addAll(response);
        businessTypeAdpt.notifyDataSetChanged();

    }
}
