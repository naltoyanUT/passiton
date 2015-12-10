package com.mycompany.passiton;


import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageTextAdapter extends BaseAdapter {
    protected Context mContext;
    protected ArrayList<String> imageURLs;
    protected LayoutInflater layoutInflater;
    private ArrayList<String> imageText;

    public ImageTextAdapter(Context c, ArrayList<String> imageURLs, ArrayList<String> imageText) {
        mContext = c;
        Log.i("PASSITON", mContext.getClass().toString());
        this.imageURLs = imageURLs;
        this.imageText = imageText;
        layoutInflater = LayoutInflater.from(mContext);
    }

    public int getCount() {
        return imageURLs.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        View grid;
        if(convertView==null){
            grid = new View(mContext);
            grid = layoutInflater.inflate(com.mycompany.passiton.R.layout.grid_layout, null);
        }else{
            grid = (View)convertView;
        }

        int width = 100;
        DisplayMetrics metrics = new DisplayMetrics();
        ((MainActivity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if(metrics.widthPixels != 0)
            width = metrics.widthPixels/2 - 2*10;

        ImageView imageView = (ImageView)grid.findViewById(com.mycompany.passiton.R.id.image);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(width, width));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);//CENTER_CROP);
        //imageView.setImageBitmap(bitmap[position]);
        TextView textView = (TextView)grid.findViewById(com.mycompany.passiton.R.id.text);
        textView.setText(imageText.get(position));

        Picasso.with(mContext).load("http://apt-passiton.appspot.com/image?key=" + imageURLs.get(position)).fit().centerCrop().into(imageView);


        return grid;
    }


}