package com.example.portfolio.utils;

import com.example.portfolio.models.Establishment;

public interface ListDataProviderContract {
    public void listAtEndNeedMoreData();
    public void favouritesChanged();
}
