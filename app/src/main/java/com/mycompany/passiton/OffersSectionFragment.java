package com.mycompany.passiton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A fragment that launches other parts of the demo application.
 */
public class OffersSectionFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    Context context;
    GridView gridview;

    String category = "All";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_offers, container, false);
        context = container.getContext();

        Spinner spinner = (Spinner) rootView.findViewById(R.id.categories_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        gridview = (GridView) rootView.findViewById(com.mycompany.passiton.R.id.gridView);

        return rootView;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        category = (String) parent.getItemAtPosition(pos);
        Log.i("PASSITON", "selected cat is = " + category);

        //make call to server and get related info (user, category)
        getOffers();
//
//        imageURLs.add("https://lh5.googleusercontent.com/udv54w7Y8-koEhdyOU3KgJ01b_yZHPS1_ZIx80W6EuLJ2BPpSYgnYLCUCwCNt_1jLZ7ytw=s190");
//        imageCaps.add("test1");
//

//        gridview.setAdapter(new ImageTextAdapter(context, imageURLs, imageCaps));
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void getOffers()
    {
        final String request_url = "http://apt-passiton.appspot.com/offers";
        RequestParams params = new RequestParams();
        params.put("user_id", "Alice A");//AccessToken.getCurrentAccessToken().getUserId());
        params.put("category", category.toLowerCase());
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setUserAgent("android");
        httpClient.get(request_url, params, new AsyncHttpResponseHandler() {

            final ArrayList<String> itemKeys = new ArrayList<String>();
            final ArrayList<String> itemNames = new ArrayList<String>();
//            final ArrayList<String> itemLats = new ArrayList<String>();
//            final ArrayList<String> itemLons = new ArrayList<String>();

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray items = jObject.getJSONArray("result");

                    //tej.getString("url")); ///image?image_id={{image_key.url}}

                    for (int i = 0; i < items.length(); i++) {
                        itemKeys.add(items.getJSONObject(i).getString("key"));
                        itemNames.add(items.getJSONObject(i).getString("name"));
//                        itemLats.add(items.getJSONObject(i).getString("lat"));
//                        itemLons.add(items.getJSONObject(i).getString("lon"));
                    }

                    gridview.setAdapter(new ImageTextAdapter(context, new ArrayList<String>(), new ArrayList<String>()));//clear grid first
                    gridview.setAdapter(new ImageTextAdapter(context, itemKeys, itemNames));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Intent detailIntent = new Intent(context, ItemDetailActivity.class);
                            detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, position);
                            detailIntent.putExtra("name", itemNames.get(position));
                            detailIntent.putExtra("key", itemKeys.get(position));
//                            detailIntent.putExtra("lat", itemLats.get(position));
//                            detailIntent.putExtra("lon", itemLons.get(position));
                            Log.i("bla", "The initial url is = " + itemKeys.get(position));
                            startActivity(detailIntent);

                        }
                    });

                } catch (JSONException j)
                {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("PASSITON", "There was a problem in retrieving the url : " + e.toString());
            }
        });
    }
}
