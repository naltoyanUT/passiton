package com.mycompany.passiton;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    //private DummyContent.DummyItem mItem;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        appBarLayout.setTitle(getArguments().getString("name"));
        final String key =  getArguments().getString("key");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        final String key = getActivity().getIntent().getStringExtra("key");
        ImageView itemImage = (ImageView) rootView.findViewById(R.id.item_detail_image);
        Picasso.with(this.getActivity()).load("http://apt-passiton.appspot.com/image?key="+key).into(itemImage);
        ((TextView) rootView.findViewById(R.id.item_detail_owner)).setText(getArguments().getString("owner"));
        ((TextView) rootView.findViewById(R.id.item_detail_date)).setText(getArguments().getString("date"));
        ((TextView) rootView.findViewById(R.id.item_detail_description)).setText(getArguments().getString("description"));


        Button wantButton = (Button) rootView.findViewById(R.id.want_button);
        wantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String request_url = "http://apt-passiton.appspot.com/want";
                RequestParams params = new RequestParams();
                params.put("user_id", "Alice A");//AccessToken.getCurrentAccessToken().getUserId());
                params.put("key", key);
                final AsyncHttpClient httpClient = new AsyncHttpClient();
                httpClient.setUserAgent("android");
                httpClient.post(request_url, params, new AsyncHttpResponseHandler() {


                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        try {
                            JSONObject jObject = new JSONObject(new String(response));
                            String result = jObject.getString("result");
                            if (result.equalsIgnoreCase("success"))
                                Toast.makeText(getActivity(), "Successfully reserved for you", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getActivity(), "Unable to reserve it for you", Toast.LENGTH_SHORT).show();

                            getActivity().finish();
                        } catch (JSONException j) {
                            Log.e("PASSITON", "JSON error : " + j.toString());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        Toast.makeText(getActivity(), "Error encountered. Try again!", Toast.LENGTH_SHORT).show();
                        Log.e("PASSITON", "There was a problem in trying to reserve the item : " + e.toString());
                    }
                });
            }

        });


        return rootView;
    }
}
