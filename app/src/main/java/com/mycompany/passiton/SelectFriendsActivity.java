package com.mycompany.passiton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;

/**
 * Created by ntoyan on 12/8/2015.
 */
public class SelectFriendsActivity extends BaseActivity {

    private static final String TAG = "image";
    private static final String SEPARATOR = ",";

    String friends = "";
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView lv = (ListView) findViewById(R.id.friendsListView);

        ArrayAdapter<Friend> adapter = new CheckboxListAdapter(this, SigninActivity.friends);
        lv.setAdapter(adapter);

        Button nextButton = (Button) findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Friend friend : SigninActivity.friends) {
                    if (friend.isSelected())
                        friends += friend.getName() + SEPARATOR;
                }
                if (friends.length() > 0)
                    friends = friends.substring(0, friends.length() - 1);

                postToServer();
            }
        });


    }

    private void postToServer(){
        RequestParams params = new RequestParams();
        params.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
        params.put("name", getIntent().getExtras().getString("name"));
        params.put("category", getIntent().getExtras().getString("category"));
        params.put("file", new ByteArrayInputStream(getIntent().getExtras().getByteArray("encodedImage")));
        params.put("description", getIntent().getExtras().getString("description"));
        params.put("latitude", getIntent().getExtras().getString("latitude"));
        params.put("longitude", getIntent().getExtras().getString("longitude"));
        params.put("friends", friends);
        //  Log.i(TAG, "lat and long ----------- " + latitude + ", " + longitude);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setUserAgent("android");
        client.post("http://apt-passiton.appspot.com/new", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.w("async", "success!!!!");
                Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.putExtra("success", true);
                setResult(CreateOfferActivity.UPLOAD, intent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Toast.makeText(context, "Upload Unsuccessful. Try again", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "There was a problem in uploading the image : " + e.toString());
                Intent intent=new Intent();
                intent.putExtra("success", false);
                setResult(CreateOfferActivity.UPLOAD, intent);
                finish();
            }
        });
    }

}
