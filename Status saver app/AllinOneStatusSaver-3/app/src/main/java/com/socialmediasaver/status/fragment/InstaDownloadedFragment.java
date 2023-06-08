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
import com.socialmediasaver.status.activity.InstagramFolderActivity;
import com.socialmediasaver.status.adapter.FileListAdapter;
import com.socialmediasaver.status.databinding.FragmentHistoryBinding;
import com.socialmediasaver.status.interfaces.FileListClickInterface;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.ArrayList;
import static androidx.databinding.DataBindingUtil.inflate;
import static com.socialmediasaver.status.util.Utils.RootDirectoryInstaShow;

public class InstaDownloadedFragment extends Fragment implements FileListClickInterface {
    private FragmentHistoryBinding binding;
    private FileListAdapter fileListAdapter;
    private ArrayList<File> fileArrayList;
    private DownLoadMainActivity activity;
    public static InstaDownloadedFragment newInstance(String param1) {
        InstaDownloadedFragment fragment = new InstaDownloadedFragment();
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
        activity = (DownLoadMainActivity) getActivity();
        //getAllFiles();
       Intent intent = new Intent(getActivity(), InstagramFolderActivity.class);
        startActivity(intent);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // binding = inflate(inflater, R.layout.fragment_history, container, false);
        View rootView = inflater.inflate(R.layout.fragment_history, null);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InstagramFolderActivity.class);
        startActivity(intent);
            }
        });

        //initViews();
        return rootView;

       // return binding.getRoot();
    }
    private void initViews(){
        binding.swiperefresh.setOnRefreshListener(() -> {
           // getAllFiles();

            binding.swiperefresh.setRefreshing(false);
        });
    }

    private void getAllFiles(){
        fileArrayList = new ArrayList<>();
        File[] files = RootDirectoryInstaShow.listFiles();
        Intent intent = new Intent(getActivity(), InstagramFolderActivity.class);
        startActivity(intent);
        if (files!=null) {
            for (File file : files) {
                fileArrayList.add(file);
            }
            fileListAdapter = new FileListAdapter(activity, fileArrayList, InstaDownloadedFragment.this);
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