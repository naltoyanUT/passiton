package com.mycompany.passiton;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * http://www.jayrambhia.com/blog/swipe-listview/
 */
public class MyListObject extends ArrayAdapter<MyListObject.ItemObject> {

    private int layoutResource;
    private LayoutInflater inflater;
    private float density = 2f;
    private View listView;

    public MyListObject(Context con, int resource) {
        super(con, resource);
        layoutResource = resource;
        inflater = (LayoutInflater)con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View workingView = null;

        if (convertView == null) {
            workingView = inflater.inflate(layoutResource, null);
        } else {
            workingView = convertView;
        }

        final ObjectHolder holder = getObjectHolder(workingView);
//        final ItemObject entry = getItem(position);

        /* set values here */

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mainView.getLayoutParams();
        params.rightMargin = 0;
        params.leftMargin = 0;
        holder.mainView.setLayoutParams(params);
        workingView.setOnTouchListener(new SwipeDetector(holder, position));

        return workingView;
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

        /* other views here */
    }

    public void setListView(View view) {
        listView = view;
    }

    // swipe detector class here


    public static class ItemObject {
        public Object getItem(int i){return null;}

    }

    @Override
    public int getCount()
    {
       return 2;
    }


    public class SwipeDetector implements View.OnTouchListener {

        private static final int MIN_DISTANCE = 300;
        private static final int MIN_LOCK_DISTANCE = 30; // disallow motion intercept
        private boolean motionInterceptDisallowed = false;
        private float downX, upX;
        private MyListObject.ObjectHolder holder;
        private int position;

        public SwipeDetector(MyListObject.ObjectHolder h, int pos) {
            holder = h;
            position = pos;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    return true; // allow other events like Click to be processed
                }

                case MotionEvent.ACTION_MOVE: {
                    upX = event.getX();
                    float deltaX = downX - upX;

                    if (Math.abs(deltaX) > MIN_LOCK_DISTANCE && listView != null && !motionInterceptDisallowed) {
                       // listView.requestDisallowInterceptTouchEvent(true);
                        motionInterceptDisallowed = true;
                    }

                    if (deltaX > 0) {
                        holder.deleteView.setVisibility(View.GONE);
                    } else {
                        // if first swiped left and then swiped right
                        holder.deleteView.setVisibility(View.VISIBLE);
                    }

                    swipe(-(int) deltaX);
                    return true;
                }

                case MotionEvent.ACTION_UP:
                    upX = event.getX();
                    float deltaX = upX - downX;
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        // left or right
                        swipeRemove();
                    } else {
                        swipe(0);
                    }

                    if (listView != null) {
                       // listView.requestDisallowInterceptTouchEvent(false);
                        motionInterceptDisallowed = false;
                    }

                    holder.deleteView.setVisibility(View.VISIBLE);
                    return true;

                case MotionEvent.ACTION_CANCEL:
                    holder.deleteView.setVisibility(View.VISIBLE);
                    return false;
            }

            return true;
        }

        private void swipe(int distance) {
            View animationView = holder.mainView;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) animationView.getLayoutParams();
            params.rightMargin = -distance;
            params.leftMargin = distance;
            animationView.setLayoutParams(params);
        }

        private void swipeRemove() {
            remove(getItem(position));
            notifyDataSetChanged();
        }
    }
}
