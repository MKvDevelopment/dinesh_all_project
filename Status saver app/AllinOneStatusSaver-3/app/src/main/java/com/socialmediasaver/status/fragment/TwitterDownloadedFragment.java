package com.socialmediasaver.status.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.activity.DownLoadMainActivity;
import com.socialmediasaver.status.activity.FullViewActivity;
import com.socialmediasaver.status.adapter.FileListAdapter;
import com.socialmediasaver.status.databinding.FragmentHistoryBinding;
import com.socialmediasaver.status.interfaces.FileListClickInterface;
import com.google.android.gms.ads.AdRequest;
import com.startapp.sdk.ads.banner.Banner;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import static androidx.databinding.DataBindingUtil.inflate;
import static com.socialmediasaver.status.util.Utils.RootDirectoryTwitterShow;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.bannerInit;

public class TwitterDownloadedFragment extends Fragment implements FileListClickInterface {
    private FragmentHistoryBinding binding;
    private FileListAdapter fileListAdapter;
    private ArrayList<File> fileArrayList;
    private DownLoadMainActivity activity;
    public static TwitterDownloadedFragment newInstance(String param1) {
        TwitterDownloadedFragment fragment = new TwitterDownloadedFragment();
        Bundle args = new Bundle();
        args.putString("m", param1);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NotNull Context _context) {
        super.onAttach(_context);
        activity = (DownLoadMainActivity) _context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("m");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        DownLoadMainActivity.toolbar.setTitle("Twitter Downloads");
        activity = (DownLoadMainActivity) getActivity();
        getAllFiles();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = inflate(inflater, R.layout.fragment_history, container, false);
        initViews();
        checksubscription();
        return binding.getRoot();
    }
    private void checksubscription() {
        if (Subscription.equals("NO")) {
            Banner banner = (Banner) binding.startAppfragmentHis;
            banner.setVisibility(View.VISIBLE);
            banner.showBanner();
        }

    }
    private void initViews(){
        binding.swiperefresh.setOnRefreshListener(() -> {
            getAllFiles();
            binding.swiperefresh.setRefreshing(false);
        });
    }

    private void getAllFiles(){
        fileArrayList = new ArrayList<>();
        File[] files = RootDirectoryTwitterShow.listFiles();
        if (files!=null) {
            for (File file : files) {
                fileArrayList.add(file);
            }
            fileListAdapter = new FileListAdapter(activity, fileArrayList, TwitterDownloadedFragment.this);
            binding.rvFileList.setAdapter(fileListAdapter);
        }
    }
    @Override
    public void getPosition(int position, File file) {
        Intent inNext = new Intent(activity, FullViewActivity.class);
        inNext.putExtra("ImageDataFile", fileArrayList);
        inNext.putExtra("Position", position);
        activity.startActivity(inNext);
    }
}