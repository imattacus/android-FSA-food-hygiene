package com.example.portfolio;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.portfolio.models.Establishment;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class EstablishmentDetailFragment extends Fragment {

    private Establishment establishment;
    private Context mContext;


    public EstablishmentDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_establishment_detail, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.detail_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mContext = getContext();

        // Get the establishment to be displayed
        establishment = (Establishment) getArguments().getSerializable("establishment");

        // Update the view with info for this establishment
        // Set toolbar title to name of establishment
        toolbar.setTitle(establishment.getName());

        // Set rating
        TextView ratingLabel = view.findViewById(R.id.detail_rating);
        if (establishment.isHasRating()) {
            int rating = establishment.getRating();
            if (rating == 5) {
                ratingLabel.setTextColor(mContext.getResources().getColor(R.color.colorBestRating));
            } else if (rating == 4 | rating == 3) {
                ratingLabel.setTextColor(mContext.getResources().getColor(R.color.colorGoodRating));
            } else if (rating == 2 | rating == 1) {
                ratingLabel.setTextColor(mContext.getResources().getColor(R.color.colorLowRating));
            } else {
                ratingLabel.setTextColor(mContext.getResources().getColor(R.color.colorWorstRating));
            }
            ratingLabel.setText(String.valueOf(rating) + "/5");
        } else {
            // Establishment is exempt or awaiting inspection so does not have a rating!
            ratingLabel.setTextColor(mContext.getResources().getColor(R.color.colorNoRating));
            if (establishment.isExempt()) {
                ratingLabel.setText("Exempt");
            } else {
                ratingLabel.setText("Awaiting Inspection");
            }
        }

        // Set rating breakdown values
        ((TextView) view.findViewById(R.id.hygiene_value)).setText(establishment.getHygieneString());
        ((TextView) view.findViewById(R.id.confidence_value)).setText(establishment.getConfidenceString());
        ((TextView) view.findViewById(R.id.structure_value)).setText(establishment.getStrucString());

        // Set last inspection date
        ((TextView) view.findViewById(R.id.lastcheck_value)).setText(establishment.getInspectionDateString());

        // Set business type
        ((TextView) view.findViewById(R.id.businesstype_value)).setText(establishment.getBusinessType());

        // Business address
        if (!establishment.getAddress_line1().equals("")) {
            ((TextView) view.findViewById(R.id.address_line1)).setText(establishment.getAddress_line1());
        } else {
            ((TextView) view.findViewById(R.id.address_line1)).setVisibility(View.GONE);
        }

        if (!establishment.getAddress_line2().equals("")) {
            ((TextView) view.findViewById(R.id.address_line2)).setText(establishment.getAddress_line2());
        } else {
            ((TextView) view.findViewById(R.id.address_line2)).setVisibility(View.GONE);
        }

        if (!establishment.getAddress_line3().equals("")) {
            ((TextView) view.findViewById(R.id.address_line3)).setText(establishment.getAddress_line3());
        } else {
            ((TextView) view.findViewById(R.id.address_line3)).setVisibility(View.GONE);
        }

        if (!establishment.getAddress_line4().equals("")) {
            ((TextView) view.findViewById(R.id.address_line4)).setText(establishment.getAddress_line4());
        } else {
            ((TextView) view.findViewById(R.id.address_line4)).setVisibility(View.GONE);
        }

        if (!establishment.getPostcode().equals("")) {
            ((TextView) view.findViewById(R.id.postcode)).setText(establishment.getPostcode());
        } else {
            ((TextView) view.findViewById(R.id.postcode)).setVisibility(View.GONE);
        }

        // Set local authority
        ((TextView) view.findViewById(R.id.local_auth_value)).setText(establishment.getLocalAuth());

        // set region
        ((TextView) view.findViewById(R.id.region_value)).setText(establishment.getRegion());
        return view;
    }


}
