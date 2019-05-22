package com.example.portfolio.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EstablishmentListResponse {

    private ArrayList<Establishment> list;
    private int totalCount;
    private int totalPages;
    private int pageNumber;
    private int itemCount;
    private String url;
    private Map<String,String> params;

    public EstablishmentListResponse(JSONObject jo, String url, Map<String,String> params) throws JSONException {
        Log.d("EstlistResp", "starting making establishment list response");
        JSONObject meta = jo.getJSONObject("meta");
        this.totalCount = meta.getInt("totalCount");
        this.totalPages = meta.getInt("totalPages");
        this.pageNumber = meta.getInt("pageNumber");
        this.itemCount = meta.getInt("itemCount");
        this.url = url;
        this.params = params;

        this.list = new ArrayList<Establishment>();
        JSONArray establishments = jo.getJSONArray("establishments");
        for(int i=0;i<establishments.length();i++) {
            list.add(new Establishment(establishments.getJSONObject(i)));
        }
        Log.d("EstlistResp", "done making establishment list response");
    }

    public ArrayList<Establishment> getList() {
        return list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getItemCount() {
        return itemCount;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setFavourites(FavouritesDao dao) {
        List<Integer> favouriteIDs = dao.getFavouriteIDs();
        for (Establishment e : list) {
            e.setFavouriteDao(dao);
            if (favouriteIDs.contains(Integer.valueOf(e.getId()))) {
                e.setFavourite(true);
            }
        }
    }

    public void setRegions(Map<String, String> map) {
        for (Establishment e : list) {
            e.setRegion(map.get(e.getLocalAuth()));
        }
    }

    public void setDescriptors(Map<Integer, String> confidenceDesc, Map<Integer, String> hygieneDesc, Map<Integer, String> structuralDesc) {
        for (Establishment e : list) {
            if(e.isHasConfidence()) {
                e.setConfidenceString(confidenceDesc.get(e.getConfidenceScore()));
            } else {
                e.setConfidenceString("Not known");
            }
            if(e.isHasHygiene()) {
                e.setHygieneString(hygieneDesc.get(e.getHygieneScore()));
            } else {
                e.setHygieneString("Not known");
            }
            if(e.isHasStruc()) {
                e.setStrucString(structuralDesc.get(e.getStrucScore()));
            } else {
                e.setStrucString("Not known");
            }
        }
    }

    public String getUrl() {
        return url;
    }
}


