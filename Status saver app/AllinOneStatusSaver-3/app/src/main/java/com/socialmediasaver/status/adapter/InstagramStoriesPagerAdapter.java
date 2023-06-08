package com.socialmediasaver.status.adapter;

import android.os.Parcelable;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.socialmediasaver.status.fragment.InstagramPagerFragment;
import com.socialmediasaver.status.model.story.ItemModel;

import java.util.ArrayList;


public class InstagramStoriesPagerAdapter extends FragmentStatePagerAdapter {
    public static int LOOPS_COUNT = 1000;
    private ArrayList<ItemModel> sliderListResponsesData;


    public InstagramStoriesPagerAdapter(FragmentManager manager, ArrayList<ItemModel> sliderListResponsesData) {
        super(manager);
        this.sliderListResponsesData = sliderListResponsesData;
    }


    @Override
    public Fragment getItem(int position) {
        if (sliderListResponsesData != null && sliderListResponsesData.size() > 0) {
           // position = position % sliderListResponsesData.size(); // use modulo for infinite cycling
            return InstagramPagerFragment.newInstance(sliderListResponsesData.get(position),sliderListResponsesData,position);
        } else {
            return InstagramPagerFragment.newInstance(null, sliderListResponsesData, position);
        }
    }


    @Override
    public int getCount() {
        return sliderListResponsesData.size(); // simulate infinite by big number of products
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //container.removeView((View) object);
    }
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }
} 