package com.mycompany.passiton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemDetailActivity extends AppCompatActivity {

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {


            String name = getIntent().getStringExtra("name");
            String key = getIntent().getStringExtra("key");

            poplulateDetails(key, name);

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, getIntent().getClass()));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void poplulateDetails(final String key, final String name)
    {
        final String request_url = "http://apt-passiton.appspot.com/details";
        RequestParams params = new RequestParams();
        params.put("key", key);
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setUserAgent("android");
        httpClient.get(request_url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject jObject = new JSONObject(new String(response));

                    final String description = jObject.getString("description");
                    final String owner = jObject.getString("owner");
                    final String date = jObject.getString("create_date").substring(0, 10);

                    final String lat = jObject.getString("lat");
                    final String lon = jObject.getString("lon");

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Log.i("offers", lat+","+lon);

                            if(lat.equals("0.0") && lon.equals("0.0"))
                                Snackbar.make(view, "No location was provided for this item", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            else {
                                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + lat + "," + lon + "(" + name + ")");
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                if (mapIntent.resolveActivity(getPackageManager()) != null)
                                    startActivity(mapIntent);

                            }
                        }
                    });


                    Bundle arguments = new Bundle();
                    arguments.putString("name", name);
                    arguments.putString("key", key);

                    arguments.putString("owner", SigninActivity.getFriendName(owner));
                    arguments.putString("date", date);
                    arguments.putString("description", description);

                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.item_detail_container, fragment)
                            .commit();


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
