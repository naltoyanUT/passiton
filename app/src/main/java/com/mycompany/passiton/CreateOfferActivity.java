package com.mycompany.passiton;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by ntoyan on 12/5/2015.
 */
public class CreateOfferActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{

    private static final int PICK_IMAGE = 1;
    private static final int USE_IMAGE = 0;

    Context context = this;
    private static final String TAG = "image";

    Button uploadButton;
    Bitmap bitmapImage;
    ImageView imgView;
    //TextView txtView;
    String category = "All";
    EditText nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        nameView = (EditText) findViewById(R.id.name);
        nameView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(nameView.getText().length() > 0)
                    uploadButton.setEnabled(true);
                else
                    uploadButton.setEnabled(false);
            }
        });


        imgView = (ImageView) findViewById(R.id.thumbnail);
        //txtView = (TextView) findViewById(R.id.text);

        // Choose image from library
        FloatingActionButton chooseFromLibraryButton = (FloatingActionButton) findViewById(R.id.choose_from_library);
        chooseFromLibraryButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // To do this, go to AndroidManifest.xml to add permission
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Start the Intent
                        startActivityForResult(galleryIntent, PICK_IMAGE);
                    }
                }
        );


        uploadButton = (Button) findViewById(R.id.upload_to_server);
        uploadButton.setEnabled(false);
        uploadButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Get photo caption

                        //String photoCaption = commentsText.getText().toString();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                        byte[] b = baos.toByteArray();
                        //byte[] encodedImage = Base64.encode(b, Base64.DEFAULT);

                        postToServer(b);
                    }
                });

        Spinner spinner = (Spinner) findViewById(R.id.categories_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        category = (String) parent.getItemAtPosition(pos);
        Log.i(TAG, "selected cat is = " + category);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();

            // User had pick an image.

            String[] filePathColumn = {MediaStore.Images.ImageColumns.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            // Link to the image

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imageFilePath = cursor.getString(columnIndex);
            cursor.close();

            // Bitmap imaged created and show thumbnail



            //txtView.setText("preparing to view..");
            bitmapImage = BitmapFactory.decodeFile(imageFilePath);
            imgView.setImageBitmap(bitmapImage);

            // Enable the upload button once image has been uploaded


            //uploadButton.setEnabled(true);

        }
//        }else if(requestCode == USE_IMAGE && data != null){
//
//            try {
//                uploadfile =(File) data.getExtras().get("FILE_NAME");
//                Log.i(TAG, "path retrieved --------------- " + uploadfile.toString());
//                bitmapImage = BitmapFactory.decodeFile(uploadfile.getPath());
//                imgView.setImageBitmap(bitmapImage);
//                uploadButton.setEnabled(true);
//
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                Log.e(TAG, e.getMessage());
//            }
//
//
//            //tv.setText(uploadfile.toString());
//        }
    }


    private void postToServer(byte[] encodedImage){
        RequestParams params = new RequestParams();
        params.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
        params.put("category", "all");
        params.put("file",new ByteArrayInputStream(encodedImage));
        params.put("description","this is a test");
        params.put("latitude",0);
        params.put("longitude", 0);
        params.put("friends", "bob,eddy");
      //  Log.i(TAG, "lat and long ----------- " + latitude + ", " + longitude);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setUserAgent("android");
        client.post("http://apt-passiton.appspot.com/new", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.w("async", "success!!!!");
                Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show();
                imgView.setImageBitmap(null);
                uploadButton.setEnabled(false);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e(TAG, "There was a problem in uploading the image : " + e.toString());
            }
        });
    }
}
