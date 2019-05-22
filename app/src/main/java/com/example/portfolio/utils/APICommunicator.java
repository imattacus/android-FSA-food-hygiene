package com.example.portfolio.utils;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.portfolio.MainActivity;
import com.example.portfolio.models.BusinessType;
import com.example.portfolio.models.Establishment;
import com.example.portfolio.models.EstablishmentListResponse;
import com.example.portfolio.models.FavouritesDao;
import com.example.portfolio.models.LocalAuthority;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class APICommunicator {

    private List<LocalAuthority> localAuthorities;
    private boolean hasAuthorities;
    private Map<String, String> authRegions;
    private boolean hasRegions;
    private RequestQueue rq;
    private List<BusinessType> businessTypes;
    private boolean hasBusinessTypes;
    private Map<Integer,String> confidenceScoreDesc;
    private Map<Integer,String> hygieneScoreDesc;
    private Map<Integer,String> structuralScoreDesc;
    private boolean hasDescriptors;

    private Map<APICommunicatorListener, EstablishmentListResponse> waitingForResponse;

    // Utility class for communicating with FSA API
    private static final String url = "http://api.ratings.food.gov.uk";
    private Context context;
    private FavouritesDao dao;

    public APICommunicator(Context context) {
        Log.d("APICommunicator", "created");
        this.context = context;
        this.dao = ((MainActivity) context).getDao();
        rq = Volley.newRequestQueue(context);
        this.authRegions = new HashMap<>();
        this.hasRegions = false;
        this.localAuthorities = new ArrayList<>();
        this.hasAuthorities = false;
        this.businessTypes = new ArrayList<>();
        this.hasBusinessTypes = false;
        this.waitingForResponse = new HashMap<>();
        this.confidenceScoreDesc = new HashMap<>();
        this.hygieneScoreDesc = new HashMap<>();
        this.structuralScoreDesc = new HashMap<>();
        this.hasDescriptors = false;
        initAuthorityRegionMap();
        initScoreDescriptors();
    }

    public void getNearbyEstablishments(Location location, float radius, final APICommunicatorListener listener) {
        Log.d("APICommunicator", "Getting nearby restaurants");
        final Map<String, String> params = new HashMap<>();
        params.put("latitude", String.valueOf(location.getLatitude()));
        params.put("longitude", String.valueOf(location.getLongitude()));
        params.put("maxDistanceLimit", String.valueOf(radius));
        params.put("sortOptionKey", "distance");
        params.put("pageSize", "100");

        try {
            String paramQuery = getQueryParams(params);
            final String queryUrl = url+"/Establishments"+paramQuery;
            JsonObjectRequest request = new JsonObjectRequest(queryUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("APIComm", "Got response!");
                            // Return the list of Establishments
                            try {
                                EstablishmentListResponse elResponse = new EstablishmentListResponse(response, queryUrl, params);
                                elResponse.setFavourites(dao);
                                if (hasRegions && hasDescriptors) {
                                    Log.d("ApiComm", "Already have regions and descriptors!");
                                    elResponse.setRegions(authRegions);
                                    elResponse.setDescriptors(confidenceScoreDesc, hygieneScoreDesc, structuralScoreDesc);
                                    listener.receiveEstablishmentListResponse(elResponse);
                                } else {
                                    waitingForResponse.put(listener,elResponse);
                                }
                            } catch (JSONException e) {
                                Log.e("HTTPresponse JSON error", e.getMessage());
                                // TODO
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO
                            Log.wtf("HTTP error", error.getMessage());
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("x-api-version", "2");
                            headers.put("response-type", "MIME");
                            return headers;
                        }
                    };
            rq.add(request);
        } catch (UnsupportedEncodingException e) {
            Log.e("APIgetnearestabrequest", e.getMessage());
            // TODO
        }
    }

    public void getAdvancedSearchEstablishments(final Map<String, String> params, final APICommunicatorListener listener) {
        Log.d("ApiCommunicator", "Performing advanced search");
        params.put("pageSize", "100");

        try {
            String paramQuery = getQueryParams(params);
            final String queryUrl = url+"/Establishments"+paramQuery;
            JsonObjectRequest request = new JsonObjectRequest(queryUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("APIComm", "Got response!");
                            // Return the list of Establishments
                            try {
                                EstablishmentListResponse elResponse = new EstablishmentListResponse(response, queryUrl, params);
                                elResponse.setFavourites(dao);
                                if (hasRegions && hasDescriptors) {
                                    Log.d("ApiComm", "Already have regions and desriptors!");
                                    elResponse.setRegions(authRegions);
                                    elResponse.setDescriptors(confidenceScoreDesc, hygieneScoreDesc, structuralScoreDesc);
                                    listener.receiveEstablishmentListResponse(elResponse);
                                } else {
                                    waitingForResponse.put(listener,elResponse);
                                }
                            } catch (JSONException e) {
                                Log.e("HTTPresponse JSON error", e.getMessage());
                                // TODO
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO
                    Log.wtf("HTTP error", error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("x-api-version", "2");
                    headers.put("response-type", "MIME");
                    return headers;
                }
            };
            rq.add(request);
        } catch(UnsupportedEncodingException e) {
            Log.e("ApiCommAdvSearchParams", e.getMessage());
        }
    }

    public void getEstablishmentsNextPage(EstablishmentListResponse lastResponse, final APICommunicatorListener listener) {
        final Map<String,String> params = lastResponse.getParams();
        params.put("pageNumber", String.valueOf(lastResponse.getPageNumber()+1));
        try {
            String paramQuery = getQueryParams(params);
            final String queryUrl = url+"/Establishments"+paramQuery;
            JsonObjectRequest request = new JsonObjectRequest(queryUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("APIComm", "Got response!");
                            // Return the list of Establishments
                            try {
                                EstablishmentListResponse elResponse = new EstablishmentListResponse(response, queryUrl, params);
                                elResponse.setFavourites(dao);
                                if (hasRegions && hasDescriptors) {
                                    Log.d("ApiComm", "Already have regions and descriptors!");
                                    elResponse.setRegions(authRegions);
                                    elResponse.setDescriptors(confidenceScoreDesc, hygieneScoreDesc, structuralScoreDesc);
                                    listener.receiveEstablishmentListResponse(elResponse);
                                } else {
                                    waitingForResponse.put(listener,elResponse);
                                }
                            } catch (JSONException e) {
                                Log.e("HTTPresponse JSON error", e.getMessage());
                                // TODO
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO
                    Log.wtf("HTTP error", error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("x-api-version", "2");
                    headers.put("response-type", "MIME");
                    return headers;
                }
            };
            rq.add(request);
        } catch(UnsupportedEncodingException e) {
            Log.e("ApiCommAdvSearchParams", e.getMessage());
        }
    }

    public void getBusinessTypes(final APICommunicatorListener listener) {
        Log.d("APICommunicator", "Getting all business types");

        if (hasBusinessTypes) {
            Log.d("APIComm", "business types from cache");
            listener.receiveBusinessTypesListResponse(businessTypes);
            return;
        }
        String queryUrl = url+"/BusinessTypes/basic";
        JsonObjectRequest request = new JsonObjectRequest(queryUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("APIComm", "Got business types response!");
                        // Return the list of Business types to listener
                        try {
                            ArrayList<BusinessType> res = new ArrayList<BusinessType>();
                            JSONArray typesJSON = response.getJSONArray("businessTypes");
                            for(int i=0;i<typesJSON.length(); i++) {
                                BusinessType bt = new BusinessType(typesJSON.getJSONObject(i));
                                if (!bt.getName().equals("All")) {
                                    res.add(bt);
                                }
                            }
                            businessTypes = res;
                            hasBusinessTypes = true;
                            listener.receiveBusinessTypesListResponse(res);
                        } catch (JSONException e) {
                            Log.e("businessTypesJSON error", e.getMessage());
                            // TODO
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO
                Log.wtf("HTTP error", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("x-api-version", "2");
                headers.put("response-type", "MIME");
                return headers;
            }
        };
        rq.add(request);
    }

    public void getLocalAuthoritiesList(final APICommunicatorListener listener) {
        Log.d("APICommunicator", "Getting all authorites");

        if (hasAuthorities) {
            Log.d("APICommunicator", "Local authorities from cache");
            listener.receiveLocalAuthoritiesListResponse(localAuthorities);
            return;
        }

        String queryUrl = url+"/Authorities";
        JsonObjectRequest request = new JsonObjectRequest(queryUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("APIComm", "Got authoritylist response!");
                        // Return the list of Authorities to listener
                        try {
                            ArrayList<LocalAuthority> res = new ArrayList<LocalAuthority>();
                            JSONArray typesJSON = response.getJSONArray("authorities");
                            for(int i=0;i<typesJSON.length(); i++) {
                                LocalAuthority la = new LocalAuthority(typesJSON.getJSONObject(i));
                                if (!la.getRegion().equals("Scotland")) {
                                    res.add(new LocalAuthority(typesJSON.getJSONObject(i)));
                                }
                            }
                            listener.receiveLocalAuthoritiesListResponse(res);
                        } catch (JSONException e) {
                            Log.e("localAuthsJSON error", e.getMessage());
                            // TODO
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO
                Log.wtf("HTTP error", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("x-api-version", "2");
                headers.put("response-type", "MIME");
                return headers;
            }
        };
        rq.add(request);
    }

    public void initAuthorityRegionMap() {
        // This class needs the list of authorities and their regions from the API so has to be initialised with the data first
        Log.d("ApiComm", "Getting authorities and region map");
        String queryUrl = url+"/Authorities";
        JsonObjectRequest request = new JsonObjectRequest(queryUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("APIComm", "Got authoritylist response!");
                        // Return the list of Authorities to listener
                        try {
                            ArrayList<LocalAuthority> res = new ArrayList<LocalAuthority>();
                            JSONArray typesJSON = response.getJSONArray("authorities");
                            for(int i=0;i<typesJSON.length(); i++) {
                                LocalAuthority la = new LocalAuthority(typesJSON.getJSONObject(i));
                                authRegions.put(la.getName(), la.getRegion());
                                res.add(new LocalAuthority(typesJSON.getJSONObject(i)));
                            }
                            Log.d("APICommMaplength", String.valueOf(authRegions.size()));
                            localAuthorities = res;
                            hasAuthorities = true;
                            hasRegions = true;

                            if (hasRegions && hasDescriptors) {
                                sendWaiting();
                            }

                        } catch (JSONException e) {
                            Log.e("localAuthsJSON error", e.getMessage());
                            // TODO
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO
                Log.wtf("HTTP error", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("x-api-version", "2");
                headers.put("response-type", "MIME");
                return headers;
            }
        };
        rq.add(request);
    }

    private void initScoreDescriptors() {
        // This class needs the list of score descriptors from the API so has to be initialised with the data first
        Log.d("ApiComm", "Getting score descriptors");
        String queryUrl = url+"/ScoreDescriptors";
        JsonObjectRequest request = new JsonObjectRequest(queryUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("APIComm", "Got score descriptors response!");
                        // Return the list of Authorities to listener
                        try {
                            JSONArray scoreDescriptorsJSON = response.getJSONArray("scoreDescriptors");
                            for (int i=0; i<scoreDescriptorsJSON.length(); i++) {
                                JSONObject descriptor = scoreDescriptorsJSON.getJSONObject(i);
                                switch (descriptor.getString("ScoreCategory")) {
                                    case "Confidence":
                                        confidenceScoreDesc.put(descriptor.getInt("Score"), descriptor.getString("Description"));
                                        break;
                                    case "Hygiene":
                                        hygieneScoreDesc.put(descriptor.getInt("Score"), descriptor.getString("Description"));
                                        break;
                                    case "Structural":
                                        structuralScoreDesc.put(descriptor.getInt("Score"), descriptor.getString("Description"));
                                        break;
                                }
                            }

                            hasDescriptors = true;

                            if (hasRegions && hasDescriptors) {
                                sendWaiting();
                            }

                        } catch (JSONException e) {
                            Log.e("localAuthsJSON error", e.getMessage());
                            // TODO
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO
                Log.wtf("HTTP error", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("x-api-version", "2");
                headers.put("response-type", "MIME");
                return headers;
            }
        };
        rq.add(request);
    }

    private void sendWaiting() {
        // Send any listeners waiting their response
        Iterator it = waitingForResponse.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<APICommunicatorListener, EstablishmentListResponse> pair = (Map.Entry) it.next();
            pair.getValue().setRegions(authRegions);
            pair.getValue().setDescriptors(confidenceScoreDesc, hygieneScoreDesc, structuralScoreDesc);
            pair.getKey().receiveEstablishmentListResponse(pair.getValue());
        }
    }

    private String getQueryParams(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for (Map.Entry<String, String> e : params.entrySet()) {
            sb.append(URLEncoder.encode(e.getKey(), "UTF-8"));
            sb.append('=');
            sb.append(URLEncoder.encode(e.getValue(), "UTF-8"));
            sb.append("&");
        }
        String res = sb.toString();
        Log.d("APIComm.getQueryParams", res);
        return res.length() > 0 ? res.substring(0, res.length()-1) : res;
    }


}
