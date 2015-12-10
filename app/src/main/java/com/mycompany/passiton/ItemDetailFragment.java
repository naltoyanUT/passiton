package com.mycompany.passiton;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


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
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        appBarLayout.setTitle(getArguments().getString("name"));

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        String key = getActivity().getIntent().getStringExtra("key");
        ImageView itemImage = (ImageView) rootView.findViewById(R.id.item_detail_image);
        Picasso.with(this.getActivity()).load("http://apt-passiton.appspot.com/image?key="+key).into(itemImage);
        ((TextView) rootView.findViewById(R.id.item_detail_owner)).setText(getArguments().getString("owner"));
        ((TextView) rootView.findViewById(R.id.item_detail_date)).setText(getArguments().getString("date"));
        ((TextView) rootView.findViewById(R.id.item_detail_description)).setText(getArguments().getString("description"));

        return rootView;
    }
}
