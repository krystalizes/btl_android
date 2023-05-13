package com.example.th2android.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.th2android.fragment.FragmentHistory;
import com.example.th2android.fragment.FragmentHome;
import com.example.th2android.fragment.FragmentSearch;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:  return new FragmentHome();
            case 1:  return new FragmentHistory();
            case 2:  return new FragmentSearch();
        }
        return null;
    }
    @Override
    public int getCount() {
        return 3;
    }
}