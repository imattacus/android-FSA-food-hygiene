package com.example.portfolio.utils;

import com.example.portfolio.models.BusinessType;
import com.example.portfolio.models.EstablishmentListResponse;
import com.example.portfolio.models.LocalAuthority;

import java.util.List;

public interface APICommunicatorListener {
    public void receiveEstablishmentListResponse(EstablishmentListResponse response);
    public void receiveLocalAuthoritiesListResponse(List<LocalAuthority> response);
    public void receiveBusinessTypesListResponse(List<BusinessType> response);
}
