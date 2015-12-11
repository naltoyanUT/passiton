package com.mycompany.passiton;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ntoyan on 12/9/2015.
 */
public class MyListSectionFragment extends Fragment {

    private static String TAG = "mylist" ;
    ArrayList<Group> groups = new ArrayList<Group>();
    Group offeredGroup, wantedGroup, reservedGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_mylist, container, false);
        createData();
        ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.listView);
        //ListView listView = (ListView) rootView.findViewById(R.id.listView);
        ExpandableListAdapter adapter = new ExpandableListAdapter(getActivity(), groups);
        listView.setAdapter(adapter);
        adapter.setListView(listView);

        return rootView;
    }

    public void createData() {
        //{ "offered": [], "pending": [], "wanted": [] }

        offeredGroup = new Group("I Offer", Group.OFFERED_GROUP);
        groups.add(offeredGroup);

        wantedGroup = new Group("I Want", Group.WANTED_GROUP);
        groups.add(wantedGroup);

        reservedGroup = new Group("Reserved", Group.RESERVED_GROUP);
        groups.add(reservedGroup);

        popluateGroups();
    }

    public void popluateGroups()
    {
        //reset previous data
//        offeredGroup.children = new ArrayList<Group.Item>();
//        wantedGroup.children = new ArrayList<Group.Item>();
//        reservedGroup.children = new ArrayList<Group.Item>();

        final String request_url = "http://apt-passiton.appspot.com/mylist";
        RequestParams params = new RequestParams();
        params.put("user_id", "Alice A");//AccessToken.getCurrentAccessToken().getUserId());
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setUserAgent("android");
        httpClient.get(request_url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    Log.d(TAG, jObject.toString());
                    JSONArray jOffered = jObject.getJSONArray("offered");
                    JSONArray jWanted = jObject.getJSONArray("wanted");
                    JSONArray jPending = jObject.getJSONArray("pending");

                    for (int i = 0; i < jOffered.length(); i++) {

                        Group.Item item = new Group.Item(jOffered.getJSONObject(i).getString("key"),
                                jOffered.getJSONObject(i).getString("name"),
                                "");
                        offeredGroup.children.add(item);
                    }

                    for (int i = 0; i < jWanted.length(); i++) {

                        Group.Item item = new Group.Item(jWanted.getJSONObject(i).getString("key"),
                                jWanted.getJSONObject(i).getString("name"),
                                "");
                        wantedGroup.children.add(item);
                    }

                    for (int i = 0; i < jPending.length(); i++) {

                        Group.Item item = new Group.Item(jPending.getJSONObject(i).getString("key"),
                                jPending.getJSONObject(i).getString("name"),
                                "");
                        reservedGroup.children.add(item);
                    }



                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("PASSITON", "There was a problem in retrieving items : " + e.toString());
            }
        });
    }
}

