package com.scau.beyondboy.slidedeleteitemlibrary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import java.util.List;
/**
 * <p>
 * To simplify  implementation,it extends {@link ArrayAdapter} instead of {@link android.widget.BaseAdapter}.
 * To reuse {@link SlideItemWrapView#mContentView} returned by {@link android.widget.ListView}'s adapter,need
 * to extends this class and only overwrites {@link #createContentView(int, View, ViewGroup)} and {@link #createBackGroudView(int, View, ViewGroup)}
 * not {@link #getView(int, View, ViewGroup)}.
 * </p>
 */
public abstract class AbstractSlideDelItemAdapter<T> extends ArrayAdapter<T> {
    /**cache mBackGroudView to avoid recreate mBackGroudView object.*//*
    private SparseArrayCompat<View> mBgViewCache;*/
    private Interpolator mCloseInterpolator;
    private Interpolator mOpenInterpolator;
    private boolean mSwipeEnable=true;
    public AbstractSlideDelItemAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SlideItemWrapView swipeItemLayout;
        if(convertView==null){
            View contentView=createContentView(position,null,parent);
            View backGroudView=createBackGroudView(position,null,parent);
            swipeItemLayout=new SlideItemWrapView(contentView, backGroudView,mCloseInterpolator,mOpenInterpolator);
        }else{
            swipeItemLayout=(SlideItemWrapView)convertView;
            createContentView(position,swipeItemLayout.getContentView(),parent);
            createBackGroudView(position,swipeItemLayout.getBackGroundView(),parent);
        }
        swipeItemLayout.setSwipEnable(mSwipeEnable);
        swipeItemLayout.setPosition(position);
        swipeItemLayout.RollBackToClose();
        return swipeItemLayout;
    }

    /**
     *  its subclass must implement this method.
     */
    @UiThread
    @NonNull
    protected abstract View createContentView(int position, View convertView, ViewGroup parent);

    /**
     * its subclass must implement this method to create item'sBackGroundView.
     */
    @UiThread
    @NonNull
    protected  abstract View createBackGroudView(int position, View convertView, ViewGroup parent);

    public Interpolator getCloseInterpolator() {
        return mCloseInterpolator;
    }

    public void setCloseInterpolator(Interpolator closeInterpolator) {
        mCloseInterpolator = closeInterpolator;
    }

    public Interpolator getOpenInterpolator() {
        return mOpenInterpolator;
    }

    public void setOpenInterpolator(Interpolator openInterpolator) {
        mOpenInterpolator = openInterpolator;
    }

    public boolean isSwipeEnable() {
        return mSwipeEnable;
    }

    /**
     * determine whether item can be slided.
     * @param swipeEnable
     */
    public void setSwipeEnable(boolean swipeEnable) {
        mSwipeEnable = swipeEnable;
    }

}
