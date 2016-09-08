package com.scau.beyondboy.slidedeleteitemlibrary;

import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;
/**
 * <p>
 *     this Adapter is used to wrap listview's adapter.
 * </p>
 */
public class SlideDeleteItemAdapter implements WrapperListAdapter {
    private ListAdapter mAdapter;
    public SlideDeleteItemAdapter(@NonNull ListAdapter adapter){
        mAdapter=adapter;
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        mAdapter.registerDataSetObserver(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        mAdapter.unregisterDataSetObserver(dataSetObserver);
    }

    @Override
    public int getCount() {
        return mAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mAdapter.getView(position,convertView,parent);
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }
    @Override
    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return mAdapter.isEnabled(position);
    }

}
