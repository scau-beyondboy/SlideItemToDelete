package com.scau.beyondboy.slidedeleteitemview;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.scau.beyondboy.slidedeleteitemlibrary.AbstractSlideDelItemAdapter;
import com.scau.beyondboy.slidedeleteitemlibrary.SlideDeleteItemListView;
import com.scau.beyondboy.slidedeleteitemlibrary.SlideItemWrapView;
import java.util.ArrayList;
import java.util.List;
/**
 * demonstrate that slide item to delete .
 */
public class SlideItemToDelete extends AppCompatActivity implements SlideItemWrapView.onSlideStopCallBack{
    private SlideAdapter mSlideAdapter;
    private ArrayList<String> itemsContent;
    @Override
    public void openStop(SlideItemWrapView view, int position) {
        //slide item to delete
        itemsContent.remove(position);
        mSlideAdapter.notifyDataSetChanged();
    }

    @Override
    public void closeStop(SlideItemWrapView view, int position) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slditemtodel);
        SlideDeleteItemListView deleteItemListView=(SlideDeleteItemListView)findViewById(R.id.swipe_delete_list);
        itemsContent=new ArrayList<>();
        itemsContent.add("apple");
        itemsContent.add("baidu");
        itemsContent.add("alibaba");
        itemsContent.add("baidu");
        itemsContent.add("tencent");
        itemsContent.add("google");
        itemsContent.add("google");
        itemsContent.add("alibaba");
        itemsContent.add("baidu");
        itemsContent.add("tencent");
        itemsContent.add("google");
        itemsContent.add("apple");
        itemsContent.add("alibaba");
        itemsContent.add("baidu");
        itemsContent.add("tencent");
        itemsContent.add("tencent");
        itemsContent.add("tencent");
        itemsContent.add("tencent");
        itemsContent.add("tencent");
        mSlideAdapter=new SlideAdapter(this,0,itemsContent);
        deleteItemListView.setAdapter(mSlideAdapter);
        //set listener to implement function of sliding item to delete
        deleteItemListView.setOnSlideStopCallBack(this);
        //disable automatically roll back
        deleteItemListView.setAutoSpringBack(false);
        //enable swiping item.
        mSlideAdapter.setSwipeEnable(true);
    }

    class SlideAdapter extends AbstractSlideDelItemAdapter<String>{

        public SlideAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }
        @NonNull
        @Override
        protected View createContentView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout1,null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mTextView.setText(getItem(position));
            return convertView;
        }

        @NonNull
        @Override
        protected View createBackGroudView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=new View(getContext());
                convertView.setBackgroundColor(Color.RED);
                convertView.setLayoutParams(new FrameLayout.LayoutParams(540, FrameLayout.LayoutParams.WRAP_CONTENT));
            }
            return convertView;
        }
    }

    class  ViewHolder{
        TextView mTextView;
        public ViewHolder(View view){
            mTextView=(TextView)view.findViewById(android.R.id.text1);
            mTextView.setTextColor(Color.BLACK);
            view.setTag(this);
        }
    }
}
