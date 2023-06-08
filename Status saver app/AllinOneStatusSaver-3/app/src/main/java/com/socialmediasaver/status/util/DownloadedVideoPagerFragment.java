package com.socialmediasaver.status.util;

import static com.socialmediasaver.status.util.Utils.RootDirectoryWhatsappShow;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.model.WhatsappStatusModel;

import java.io.File;
import java.util.ArrayList;

public class DownloadedVideoPagerFragment extends Fragment {
    String  image;
    public String SaveFilePath = RootDirectoryWhatsappShow + "/";
    public WhatsappStatusModel fileItem;
    ArrayList<WhatsappStatusModel> list;
    int position;
    private MediaController mediaController;
    VideoView simpleVideoView;
    boolean isVisible = false;
    String type;
    int pauSePosition;
    ImageView im_vpPlay;
    public boolean Pause=false;


    public static DownloadedVideoPagerFragment newInstance(File banner, ArrayList<File> sliderListResponsesData, int position) {

        Bundle args = new Bundle();

        args.putString("image", banner.getPath());
        args.putString("type", banner.getName().substring(banner.getName().lastIndexOf(".")));
      //  args.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) sliderListResponsesData);
        args.putInt("position",position);

        DownloadedVideoPagerFragment fragment = new DownloadedVideoPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        image = getArguments().getString("image");
        list = getArguments().getParcelableArrayList("list");
        position = getArguments().getInt("position");
        type = getArguments().getString("type");

      //  fileItem = list.get(position);

        int layout = R.layout.slidingimages_layout;
        //int layout = R.layout.activity_full_image2;
        View root = inflater.inflate(layout, container, false);
        // root.setTag(position);
        ImageView imageView = root.findViewById(R.id.im_fullViewImage);
         im_vpPlay = root.findViewById(R.id.im_vpPlay);
         simpleVideoView = root.findViewById(R.id.videoview2);

        //ImageView image_one = (ImageView) root.findViewById(R.id.fullImag);
       // FloatingActionButton floatingActionButton = root.findViewById(R.id.floatingActionButton);
        if (type.equals(".mp4")){
            im_vpPlay.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);

            mediaController = new MediaController(getActivity());
            Uri uri = Uri.parse(image);
//        if(simpleVideoView!=null)
//            simpleVideoView.stopPlayback();
            // Toast.makeText(getActivity(), isVisible+"", Toast.LENGTH_SHORT).show();
            simpleVideoView.setVideoURI(uri);
            mediaController.setAnchorView(simpleVideoView);
            simpleVideoView.setMediaController(null);
            simpleVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (simpleVideoView.isPlaying()) {
                        im_vpPlay.setVisibility(View.VISIBLE);
                        //simpleVideoView.setVisibility(View.GONE);
                        simpleVideoView.pause();
                    }else {
                        im_vpPlay.setVisibility(View.GONE);
                       // simpleVideoView.setVisibility(View.VISIBLE);
                        simpleVideoView.start();
                    }

                }
            });
            simpleVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    im_vpPlay.setVisibility(View.VISIBLE);

                }
            });
        } else {
            imageView.setVisibility(View.VISIBLE);

            simpleVideoView.setVisibility(View.GONE);
            Glide.with(getActivity()).load(image).into(imageView);
            im_vpPlay.setVisibility(View.GONE);
        }




       // simpleVideoView.start();
      //  if (isVisible)
        //simpleVideoView.start();
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                download();
//            }
//        });


        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {


//        isVisible = isVisibleToUser;
//      //  Toast.makeText(getActivity(), isVisible+"", Toast.LENGTH_SHORT).show();
//        if(simpleVideoView!=null)
//            simpleVideoView.pause();
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible())
        {
            if (!isVisibleToUser)   // If we are becoming invisible, then...
            {
                //pause or stop video
                if (type.equals(".mp4")) {
                    simpleVideoView.pause();
                    im_vpPlay.setVisibility(View.VISIBLE);
                }
               // Toast.makeText(getActivity(), position+"", Toast.LENGTH_SHORT).show();
            }else {
                //Toast.makeText(getActivity(), position+"", Toast.LENGTH_SHORT).show();
                //simpleVideoView.start();
            }

            if (isVisibleToUser) // If we are becoming visible, then...
            {
                //play your video
                if (type.equals(".mp4")) {
                    im_vpPlay.setVisibility(View.GONE);
                    simpleVideoView.start();
                }
               // Toast.makeText(getActivity(), position+"", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        boolean t =getUserVisibleHint();
        setUserVisibleHint(t);
        super.onResume();

    }



    @Override
    public void onPause() {
        setUserVisibleHint(false);
        super.onPause();

        pauSePosition = simpleVideoView.getCurrentPosition();
    }

    @Override
    public void onStart() {
        super.onStart();

        simpleVideoView.seekTo(pauSePosition);
    }

//    private void download() {
//        simpleVideoView.pause();
//
//        createFileFolder();
//        final String path = fileItem.getPath();
//        String filename = path.substring(path.lastIndexOf("/") + 1);
//        final File file = new File(path);
//        File destFile = new File(SaveFilePath);
//        try {
//            FileUtils.copyFileToDirectory(file, destFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String fileNameChange = filename.substring(12);
//        File newFile = new File(SaveFilePath + fileNameChange);
//        String ContentType = "image/*";
//        if (fileItem.getUri().toString().endsWith(".mp4")) {
//            ContentType = "video/*";
//        } else {
//            ContentType = "image/*";
//        }
//        MediaScannerConnection.scanFile(getActivity().getApplicationContext(), new String[]{newFile.getAbsolutePath()}, new String[]{ContentType},
//                new MediaScannerConnection.MediaScannerConnectionClient() {
//                    public void onMediaScannerConnected() {
//                    }
//
//                    public void onScanCompleted(String path, Uri uri) {
//                    }
//                });
//
//        File from = new File(SaveFilePath, filename);
//        File to = new File(SaveFilePath, fileNameChange);
//        from.renameTo(to);
//
//        Toast.makeText(getActivity().getApplicationContext(), "Saved to My Downloads", Toast.LENGTH_LONG).show();
//
//    }


}
