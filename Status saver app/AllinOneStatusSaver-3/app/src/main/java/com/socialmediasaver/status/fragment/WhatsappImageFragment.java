package com.socialmediasaver.status.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.activity.FullImageActivity;
import com.socialmediasaver.status.activity.VideoActivity;
import com.socialmediasaver.status.adapter.WhatsappStatusAdapter;
import com.socialmediasaver.status.databinding.FragmentWhatsappImageBinding;
import com.socialmediasaver.status.model.WhatsappStatusModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import static androidx.databinding.DataBindingUtil.inflate;

public class WhatsappImageFragment extends Fragment {
    FragmentWhatsappImageBinding binding;

    private File[] allfiles;
    private DocumentFile[] allfiles1;

   public static ArrayList<WhatsappStatusModel> statusModelArrayList;
    private WhatsappStatusAdapter whatsappStatusAdapter;
    //boolean whatsapp ,whatappBusiness=false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = inflate(inflater, R.layout.fragment_whatsapp_image, container, false);
        initViews();
        return binding.getRoot();
    }

    private void initViews() {
        statusModelArrayList = new ArrayList<>();
        checkAndroidVersion();
        binding.swiperefresh.setOnRefreshListener(() -> {
            statusModelArrayList = new ArrayList<>();
            checkAndroidVersion();
            binding.swiperefresh.setRefreshing(false);
        });

    }

    private void checkAndroidVersion() {
        //String targetPath="";
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (whatsapp==true){
//                 targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
//
//            }else if (whatappBusiness==true){
//                 targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/com.whatsapp.w4b/WhatsAppBusiness/Media/.Statuses";
//
//            }
            //File folder = new File(Environment.getExternalStorageDirectory(), "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses");
            //String path=folder.getAbsolutePath();

          //  String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
           // String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Internal storage/Android/media/com.whatsapp/WhatsApp/Media/.Statuses/";
           // String targetPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+File.separator+"/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";

           // String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
            //String targetPath = Environment.getStorageDirectory().getPath() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
            //getData(targetPath);
            getStatusForAndroid();
        } else {

//            if (whatsapp==true){
//                // targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/.Statuses";
//                targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsAppBusiness/Media/.Statuses";
//
//
//            }else if (whatappBusiness==true){
//                 targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsAppBusiness/Media/.Statuses";
//
//            }
            String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/.Statuses";
            //String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsAppBusiness/Media/.Statuses";
            getData(targetPath);
        }

        String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/.Statuses";
        //String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsAppBusiness/Media/.Statuses";
        getData(targetPath);

    }

    public void getStatusForAndroid(){
        WhatsappStatusModel whatsappStatusModel;
        Uri myUri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses");
        DocumentFile documentFile = DocumentFile.fromTreeUri(getActivity(), myUri);
        allfiles1 = documentFile.listFiles();
        for (DocumentFile file : documentFile.listFiles()) {
            if(file.isDirectory()){ // if it is sub directory
                // Do stuff with sub directory
                Log.d("yo",file.getUri() + "\n");
            } else {
                // Do stuff with normal file
            }

            Log.d("Uri",file.getUri() + "\n");

        }

        for (int i = 0; i < allfiles1.length; i++) {
            DocumentFile file = allfiles1[i];
            if (file.getUri().getPath().endsWith(".png") || file.getUri().getPath().endsWith(".jpg")) {
                whatsappStatusModel = new WhatsappStatusModel("WhatsStatus: " + (i + 1),
                        file.getUri(),
                        allfiles1[i].getUri().getEncodedPath(),
                        file.getName());
                statusModelArrayList.add(whatsappStatusModel);
            }
        }

        if (statusModelArrayList.size()!=0) {
            binding.tvNoResult.setVisibility(View.GONE);
        } else {
            binding.tvNoResult.setVisibility(View.VISIBLE);
        }
        whatsappStatusAdapter = new WhatsappStatusAdapter(getActivity(), statusModelArrayList);
        binding.rvFileList.setAdapter(whatsappStatusAdapter);

        whatsappStatusAdapter.setListner(position -> {
            //  Toast.makeText(getContext(), "ddd", Toast.LENGTH_SHORT).show();
            WhatsappStatusModel fileItem = statusModelArrayList.get(position);
            if (fileItem.getUri().toString().endsWith(".mp4")){
                Intent intent=new Intent(getContext(), VideoActivity.class);
                intent.putExtra("url",fileItem.getPath());
                intent.putExtra("type",String.valueOf(position));
                getContext().startActivity(intent);
            }else {
                Intent intent=new Intent(getContext(), FullImageActivity.class);
                intent.putExtra("url",fileItem.getPath());
                intent.putExtra("type",String.valueOf(position));
                //intent.putParcelableArrayListExtra("list",  statusModelArrayList);
                intent.putExtra("list",statusModelArrayList);
                getContext().startActivity(intent);
            }
        });
    }

    private void getData(String targetPath) {
        String targetPathBusiness="";
        WhatsappStatusModel whatsappStatusModel;
        File targetDirector = new File(targetPath);
        allfiles = targetDirector.listFiles();
        try {
            Arrays.sort(allfiles, (Comparator) (o1, o2) -> {
                if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    return -1;
                } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            });

            for (int i = 0; i < allfiles.length; i++) {
                File file = allfiles[i];
                if (Uri.fromFile(file).toString().endsWith(".png") || Uri.fromFile(file).toString().endsWith(".jpg")) {
                    whatsappStatusModel = new WhatsappStatusModel("WhatsStatus: " + (i + 1),
                            Uri.fromFile(file),
                            allfiles[i].getAbsolutePath(),
                            file.getName());
                    statusModelArrayList.add(whatsappStatusModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            Arrays.sort(allfilesBusiness, (Comparator) (o1, o2) -> {
//                if (((File) o1).lastModified() > ((File) o2).lastModified()) {
//                    return -1;
//                } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
//                    return +1;
//                } else {
//                    return 0;
//                }
//            });
//
//            for (int i = 0; i < allfilesBusiness.length; i++) {
//                File file = allfilesBusiness[i];
//                if (Uri.fromFile(file).toString().endsWith(".png") || Uri.fromFile(file).toString().endsWith(".jpg")) {
//                    whatsappStatusModel = new WhatsappStatusModel("WhatsStatusB: " + (i + 1),
//                            Uri.fromFile(file),
//                            allfilesBusiness[i].getAbsolutePath(),
//                            file.getName());
//                    statusModelArrayList.add(whatsappStatusModel);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (statusModelArrayList.size()!=0) {
            binding.tvNoResult.setVisibility(View.GONE);
        } else {
            binding.tvNoResult.setVisibility(View.VISIBLE);
        }
        whatsappStatusAdapter = new WhatsappStatusAdapter(getActivity(), statusModelArrayList);
        binding.rvFileList.setAdapter(whatsappStatusAdapter);

        whatsappStatusAdapter.setListner(position -> {
          //  Toast.makeText(getContext(), "ddd", Toast.LENGTH_SHORT).show();
            WhatsappStatusModel fileItem = statusModelArrayList.get(position);
            if (fileItem.getUri().toString().endsWith(".mp4")){
                Intent intent=new Intent(getContext(), VideoActivity.class);
                intent.putExtra("url",fileItem.getPath());
                intent.putExtra("type",String.valueOf(position));
                getContext().startActivity(intent);
            }else {
                Intent intent=new Intent(getContext(), FullImageActivity.class);
                intent.putExtra("url",fileItem.getPath());
                intent.putExtra("type",String.valueOf(position));
                //intent.putParcelableArrayListExtra("list",  statusModelArrayList);
                intent.putExtra("list",statusModelArrayList);
                getContext().startActivity(intent);
            }
        });
    }

}
