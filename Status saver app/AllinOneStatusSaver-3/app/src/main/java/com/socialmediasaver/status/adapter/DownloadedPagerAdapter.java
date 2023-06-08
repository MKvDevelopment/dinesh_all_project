package com.socialmediasaver.status.adapter;

import android.os.Parcelable;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.socialmediasaver.status.util.DownloadedVideoPagerFragment;

import java.io.File;
import java.util.ArrayList;


public class DownloadedPagerAdapter extends FragmentStatePagerAdapter {
    public static int LOOPS_COUNT = 1000;
    private ArrayList<File> sliderListResponsesData;


    public DownloadedPagerAdapter(FragmentManager manager, ArrayList<File> sliderListResponsesData) {
        super(manager);
        this.sliderListResponsesData = sliderListResponsesData;
    }


    @Override
    public Fragment getItem(int position) {
        if (sliderListResponsesData != null && sliderListResponsesData.size() > 0) {
           // position = position % sliderListResponsesData.size(); // use modulo for infinite cycling
            return DownloadedVideoPagerFragment.newInstance(sliderListResponsesData.get(position),sliderListResponsesData,position);
        } else {
            return DownloadedVideoPagerFragment.newInstance(null, sliderListResponsesData, position);
        }
    }


    @Override
    public int getCount() {
        return sliderListResponsesData.size(); // simulate infinite by big number of products
      /*  if (sliderListResponsesData != null && sliderListResponsesData.size() > 0) {
            return sliderListResponsesData.size() * LOOPS_COUNT; // simulate infinite by big number of products
        } else {
            return 10;
        }*/
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //container.removeView((View) object);
    }
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }
} 