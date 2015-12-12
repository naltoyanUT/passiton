package com.mycompany.passiton;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SigninActivity extends Activity {

    public final static String TAG = "LOGIN";

    CallbackManager callbackManager;
    LoginButton loginButton;
    TextView info;
    Context context = this; //addeded by me
    Activity activity = this;
    private ProgressDialog progressDialog;
    static String email = "not set yet";
    static String name = "not set yet";
    //public static ArrayList<String> friends = new ArrayList<String>();
    public static ArrayList<Friend> friends = new ArrayList<Friend>();

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());


        setContentView(R.layout.activity_signin);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        info = (TextView)findViewById(R.id.info);

        callbackManager = CallbackManager.Factory.create();

        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile", "user_friends"));

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //Toast.makeText(context, "Gathering your info..", Toast.LENGTH_SHORT).show();
                        updateFriends(loginResult.getAccessToken());
                        // finish();
                    }

                    @Override
                    public void onCancel() {
                        info.setText("Login attempt canceled.");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        info.setText("Login attempt failed.\n" + exception.toString());
                    }
                });

   }

    private void updateFriends(AccessToken accessToken)
    {
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                accessToken,
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(
                            JSONArray object,
                            GraphResponse response) {
                        // Application code
                        Log.v(TAG, response.toString());
                        friends = new ArrayList<Friend>(); //reset
                        if(object == null) return;
                        for (int i = 0, size = object.length(); i < size; i++) {
                            JSONObject friend = null;
                            try {
                                friend = object.getJSONObject(i);
                                String id = friend.getString("id");
                                String name = friend.getString("name");

                                friends.add(new Friend(id, name));
//                                friends.add(new Friend("12345678", "Alice A"));
//                                friends.add(new Friend("98765432", "Bob B"));


                                Log.i(TAG, "FRIENDS name/id=" + name + "/" + id);


                            } catch (JSONException e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                        //email = "najd.altoyan@gmail.com";
                    }
                });
//                        Bundle parameters = new Bundle();
//                        parameters.putString("fields", "id,name,email,gender, birthday");
//                        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(isLoggedIn())
        {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            Log.i(TAG, "2----" + accessToken + "");
            Log.i(TAG, "user id = " + accessToken.getUserId());

            GraphRequest request = new GraphRequest(
                    accessToken,
                    "/"+accessToken.getUserId(),
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            JSONObject json = response.getJSONObject();
                            if(json == null) return;
                            try {
                                name = json.getString("name");
                                email = json.getString("email");
                                Log.i(TAG, "name/email=" + name + "/" + email);
                            } catch (JSONException e) {
                                Log.d(TAG, e.toString());
                            }
                        }
                    }
            );
            Bundle parameters = new Bundle();
            parameters.putString("fields", "name,email");
            request.setParameters(parameters);
            request.executeAsync();

            updateFriends(accessToken);

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("EMAIL", email);
            intent.putExtra("USER_ID", accessToken.getUserId());
            startActivity(intent);
            finish();
            //LoginManager.getInstance().logOut();
            return;
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        progressDialog = new ProgressDialog(SigninActivity.this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static void logOut()
    {
        LoginManager.getInstance().logOut();
        Log.i(TAG, "4----" + AccessToken.getCurrentAccessToken() + "");
        AccessToken.setCurrentAccessToken(null);
    }

    public static String [] getFriendsNames()
    {
        String [] names =  new String [friends.size()];
        for(int i = 0; i< friends.size(); ++i)
            names[i] = friends.get(i).getName();
        return names;
    }

    public static String getFriendName(String id)
    {
        if(id.equals(AccessToken.getCurrentAccessToken().getUserId()))
            return name;
        for(Friend friend: friends){
            if(friend.getId().equals(id))
                return friend.getName();
        }
        return id;
    }

}