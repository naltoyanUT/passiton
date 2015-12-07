package com.mycompany.passiton;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ntoyan on 12/5/2015.
 */
public class CreateOfferActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{

    private static final int PICK_IMAGE = 1;
    private static final int TAKE_IMAGE = 0;

    Context context = this;
    private static final String TAG = "image";

    Button uploadButton;
    Bitmap bitmapImage;
    ImageView imgView;
    //TextView txtView;
    String category = "All";
    EditText nameView, descriptionView;
    File uploadfile;

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


        descriptionView = (EditText) findViewById(R.id.description);

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


        FloatingActionButton cameraButton =(FloatingActionButton) this.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "DCIM" + File.separator + "Camera";
                uploadfile = new File(path, "img" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg");
                try {
                    uploadfile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_IMAGE);
            }

        });

        uploadButton = (Button) findViewById(R.id.upload_to_server);
        uploadButton.setEnabled(false);
        uploadButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SelectFriendsActivity.class);
                        startActivity(intent);
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
//                        byte[] b = baos.toByteArray();
//                        postToServer(b);
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

        }
        else if (requestCode == TAKE_IMAGE && data != null && data.getExtras() != null) {
            Bitmap bm=(Bitmap) data.getExtras().get("data");
            try {
                FileOutputStream fout=new FileOutputStream(uploadfile);
                bm.compress(Bitmap.CompressFormat.JPEG, 100,fout);
                fout.flush();
                fout.close();
                InputStream in =new FileInputStream(uploadfile);
                bitmapImage = BitmapFactory.decodeFile(uploadfile.getPath());
                imgView.setImageBitmap(bitmapImage);
                Log.i(TAG, "path -------------- " + uploadfile.toString());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    private void postToServer(byte[] encodedImage){
        RequestParams params = new RequestParams();
        params.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
        params.put("name", nameView.getText());
        params.put("category", "all");
        params.put("file",new ByteArrayInputStream(encodedImage));
        params.put("description", descriptionView.getText());
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
