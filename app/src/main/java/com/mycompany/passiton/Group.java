package com.mycompany.passiton;

/**
 * http://www.vogella.com/tutorials/AndroidListView/article.html
 */
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Group {

    public String string;
    public final List<Item> children = new ArrayList<Item>();

    public Group(String string) {
        this.string = string;
    }
    public boolean remove(int childPosition, Context context)
    {
        boolean removed = removeItem(childPosition, context);
        if(removed)
            children.remove(childPosition);
        return removed;
    }


    private boolean cancelItem()
    {
        return false;
    }

    private boolean removeItem(int childPosition, final Context context)
    {
        final String request_url = "http://apt-passiton.appspot.com/delete";
        RequestParams params = new RequestParams();
        params.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
        params.put("key", children.get(childPosition).getKey());
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setUserAgent("android");
        httpClient.post(request_url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    Log.e("mylist", jObject.toString());
                    String result = jObject.getString("result");

                    if (result.equalsIgnoreCase("success"))
                        Toast.makeText(context, "Deletion Successful", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Unable to delete item", Toast.LENGTH_SHORT).show();

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Toast.makeText(context, "There was a problem in deleting the item", Toast.LENGTH_SHORT).show();
                Log.e("PASSITON", "There was a problem in deleting the item " + e.toString());

            }
        });
        return false;
    }

    public Object get(int childPosition)
    {
        return children.get(childPosition);
    }


    public static class Item
    {
        private String key;
        private String name;
        private String description;


        public Item(String key, String name, String description)
        {
            this.name = name;
            this.key = key;
            this.description = description;
        }

        public String getKey()
        {
            return key;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}