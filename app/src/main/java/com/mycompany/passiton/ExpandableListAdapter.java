package com.mycompany.passiton;

/**
 * http://www.vogella.com/tutorials/AndroidListView/article.html
 */

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final ArrayList<Group> groups;
    public LayoutInflater inflater;
    public Activity activity;
    public final static String TAG = "mylist";

    private View listView;


    public ExpandableListAdapter(Activity act, ArrayList<Group> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }



   @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Group.Item child = (Group.Item) getChild(groupPosition, childPosition);
        TextView text = null;

        View workingView = null;

        if (convertView == null) {
            workingView = inflater.inflate(R.layout.listrow_details, null);
            //convertView = inflater.inflate(R.layout.listrow_details, null);

//            MyListObject myList = new MyListObject(activity, R.layout.listrow_details);
//            //listView.setAdapter(myList);
//            myList.setListView(convertView);

        }

        else workingView = convertView;

//
//        text = (TextView) convertView.findViewById(R.id.textView1);
//        text.setText(children);
//        convertView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(activity, children,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });


        final ObjectHolder holder = getObjectHolder(workingView);

        // set values here //

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mainView.getLayoutParams();
        params.rightMargin = 0;
        params.leftMargin = 0;
        holder.mainView.setLayoutParams(params);

        holder.mainView.setVisibility(View.VISIBLE);//reset view
        ImageView itemImage = (ImageView) holder.mainView.findViewById(R.id.itemImage);
        Picasso.with(activity)
                .load("http://apt-passiton.appspot.com/image?key=" + child.getKey())
                .transform(new CropCircleTransformation())
                //.fit().centerInside()
                .into(itemImage);

        TextView itemText = (TextView) holder.mainView.findViewById(R.id.itemText);
        itemText.setText(child.toString());

        final SwipeDetector swipeDetector = new SwipeDetector(holder, groupPosition, childPosition);
        workingView.setOnTouchListener(swipeDetector);
        ImageView close = (ImageView) holder.deleteView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.mainView.getVisibility() == View.GONE)
                    holder.mainView.setVisibility(View.VISIBLE);
            }
        });

       ImageView details = (ImageView) holder.deleteView.findViewById(R.id.details);
       details.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(holder.mainView.getVisibility() == View.GONE) {
                   Intent detailIntent = new Intent(activity, ItemDetailActivity.class);
                   //detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, position);
                   detailIntent.putExtra("name", child.toString());
                   detailIntent.putExtra("key", child.getKey());
//                            detailIntent.putExtra("lat", itemLats.get(position));
//                            detailIntent.putExtra("lon", itemLons.get(position));
                   //Log.i("bla", "The initial url is = " + itemKeys.get(position));
                   activity.startActivity(detailIntent);
                   holder.mainView.setVisibility(View.VISIBLE);
               }

           }
       });

       ImageView delete = (ImageView) holder.deleteView.findViewById(R.id.delete);
       delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(holder.mainView.getVisibility() == View.GONE) {
                   swipeDetector.swipeRemove();
               }

           }
       });


        return workingView;
        //return convertView;
    }


    private ObjectHolder getObjectHolder(View workingView) {
        Object tag = workingView.getTag();
        ObjectHolder holder = null;

        if (tag == null || !(tag instanceof ObjectHolder)) {
            holder = new ObjectHolder();
            holder.mainView = (LinearLayout)workingView.findViewById(R.id.mainview);
            holder.deleteView = (RelativeLayout)workingView.findViewById(R.id.deleteview);

            /* initialize other views here */

            workingView.setTag(holder);
        } else {
            holder = (ObjectHolder) tag;
        }

        return holder;
    }


    public static class ObjectHolder {
        public LinearLayout mainView;
        public RelativeLayout deleteView;
        //public TextView itemText = (TextView) mainView.findViewById(R.id.itemText);;

        /* other views here */
    }


    public void setListView(View view) {
        listView = view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
    }

    @Override
    public Group getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }
        Group group = (Group) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.string);
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }



    public class SwipeDetector implements View.OnTouchListener {

        private ExpandableListAdapter.ObjectHolder holder;
        private int groupPosition, childPosition;

        public SwipeDetector(ExpandableListAdapter.ObjectHolder h, int gpos, int cpos) {
            holder = h;
            groupPosition =gpos;
            childPosition = cpos;
        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {

                    return true; // allow other events like Click to be processed
                }

                case MotionEvent.ACTION_MOVE: {
                        holder.mainView.setVisibility(View.GONE);

                      return true;
                }

                case MotionEvent.ACTION_UP:
                    //holder.deleteView.setVisibility(View.VISIBLE);
                    return true;

                case MotionEvent.ACTION_CANCEL:
                    //holder.mainView.setVisibility(View.VISIBLE);
                    return false;
            }

            return true;
        }


        public void swipeRemove() {
            getGroup(groupPosition).remove(childPosition, activity);
           // remove(getChild(groupPosition, childPosition));
            notifyDataSetChanged();
        }

    }
}