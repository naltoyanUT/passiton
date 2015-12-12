package com.mycompany.passiton;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
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
public class CreateOfferActivity extends BaseActivity implements
        AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    public static final int UPLOAD = 2;
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_IMAGE = 0;

    Context context = this;
    private static final String TAG = "image";

    Button uploadButton;
    Bitmap bitmapImage;
    ImageView imgView;
    //TextView txtView;
    String category = "";
    EditText nameView, descriptionView;
    File uploadfile;
    Double latitude, longitude;
    boolean tagWithLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));



        LocationManager manager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener=new LocationListener(){

            @Override
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub
                latitude=location.getLatitude();
                longitude=location.getLongitude();
            }
            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {

            }
            @Override
            public void onProviderEnabled(String provider) {

            }
            @Override
            public void onProviderDisabled(String provider) {

            }

        };

       // checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        int result = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if(result == PackageManager.PERMISSION_GRANTED)
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, (float) 0., listener);



        nameView = (EditText) findViewById(R.id.name);
        nameView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(nameView.getText().length() > 0 && !category.equals(""))
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

        SwitchCompat locationSwitch = (SwitchCompat) findViewById(R.id.location);
        locationSwitch.setOnCheckedChangeListener(this);

        uploadButton = (Button) findViewById(R.id.upload_to_server);
        uploadButton.setEnabled(false);
        uploadButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] encodedImage;
                        if (bitmapImage == null)
                            bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.no_photo);

                        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                        encodedImage = baos.toByteArray();

                        Intent intent = new Intent(context, SelectFriendsActivity.class);
                        intent.putExtra("name", nameView.getText().toString());
                        intent.putExtra("category", category);
                        intent.putExtra("encodedImage", encodedImage);
                        String desc = descriptionView.getText().toString();
                        if (desc.equals("")) desc = "no description provided";
                        intent.putExtra("description", desc);
                        if (tagWithLocation) {
                            intent.putExtra("latitude", latitude + "");
                            intent.putExtra("longitude", longitude + "");
                        }
                        startActivityForResult(intent, UPLOAD);
                    }
                });


        String[] categories = getResources().getStringArray(R.array.create_categories_array);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categories) {

            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        //
        Spinner spinner = (Spinner) findViewById(R.id.categories_spinner);



        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
//                R.array.create_categories_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        //spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        //spinner.getChildAt(0).setEnabled(false);
        spinner.setAdapter(spinnerAdapter);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.location:
                tagWithLocation = isChecked;
                break;
        }

    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        if(pos == 0) {
            category = "";
            uploadButton.setEnabled(false);
        }
        else
        {
            category = (String) parent.getItemAtPosition(pos);
            if(nameView.getText().length() > 0)
                uploadButton.setEnabled(true);
        }
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
        else if (requestCode == UPLOAD && data != null && data.getExtras() != null)
        {
            boolean success = data.getExtras().getBoolean("success", false);
            if(success) finish();
        }
    }


}
