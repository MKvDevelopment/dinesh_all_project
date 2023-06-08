package com.socialmediasaver.status.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.activity.FullViewActivity;
import com.socialmediasaver.status.activity.VideoPlayActivity;

import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.ArrayList;
import static com.socialmediasaver.status.util.Utils.shareImage;
import static com.socialmediasaver.status.util.Utils.shareVideo;


public class ShowImagesAdapter extends PagerAdapter {
    private Context context;
    public static ArrayList<File> imageList;
    private LayoutInflater inflater;
    FullViewActivity fullViewActivity;
    VideoView videoview2;
    MediaController mediaController;


    public ShowImagesAdapter(Context context, ArrayList<File> imageList, FullViewActivity fullViewActivity) {
        this.context = context;
        this.imageList = imageList;
        this.fullViewActivity=fullViewActivity;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        container.removeView((View) object);
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);
        videoview2 = imageLayout.findViewById(R.id.videoview2);
        assert imageLayout != null;
        final ImageView imageView = imageLayout.findViewById(R.id.im_fullViewImage);
        final ImageView im_vpPlay = imageLayout.findViewById(R.id.im_vpPlay);
        final ImageView im_share = imageLayout.findViewById(R.id.im_share);
        final ImageView im_delete = imageLayout.findViewById(R.id.im_delete);




        view.addView(imageLayout, 0);
        String extension = imageList.get(position).getName().substring(imageList.get(position).getName().lastIndexOf("."));
        if (extension.equals(".mp4")){
            im_vpPlay.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);

                videoview2.setVisibility(View.VISIBLE);
                mediaController = new MediaController(context);
                Uri uri = Uri.parse(imageList.get(position).getPath());
                videoview2.setVideoURI(uri);
                mediaController.setAnchorView(videoview2);
                videoview2.setMediaController(null);
                videoview2.start();
        }else {
            imageView.setVisibility(View.VISIBLE);

            videoview2.setVisibility(View.GONE);
            Glide.with(context).load(imageList.get(position).getPath()).into(imageView);
            im_vpPlay.setVisibility(View.GONE);
        }


        im_vpPlay.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), VideoPlayActivity.class);
            intent.putExtra("url",imageList.get(position).getPath());
            context.startActivity(intent);
        });

        im_delete.setOnClickListener(v -> {
            boolean b=imageList.get(position).delete();
            if (b){
                fullViewActivity.deleteFileAA(position);
            }
        });

        im_share.setOnClickListener(v -> {
            String extension1 = imageList.get(position).getName().substring(imageList.get(position).getName().lastIndexOf("."));
            if (extension1.equals(".mp4")){
                shareVideo(context,imageList.get(position).getPath());
            }else {
                shareImage(context,imageList.get(position).getPath());
            }
        });


        return imageLayout;
    }




    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, @NotNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}