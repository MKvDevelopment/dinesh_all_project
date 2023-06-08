package com.socialmediasaver.status.util;

import static com.socialmediasaver.status.util.Utils.RootDirectoryWhatsappBusinessImageShow;
import static com.socialmediasaver.status.util.Utils.RootDirectoryWhatsappBusinessVideoShow;
import static com.socialmediasaver.status.util.Utils.RootDirectoryWhatsappImageShow;
import static com.socialmediasaver.status.util.Utils.RootDirectoryWhatsappVideoShow;
import static com.socialmediasaver.status.util.Utils.createFileFolder;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.model.WhatsappStatusModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jsibbold.zoomage.ZoomageView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ViewPagerFragment extends Fragment {
    String image;
    // public String SaveFilePath = RootDirectoryWhatsappShow + "/";
    public String SaveFilePathImage = RootDirectoryWhatsappImageShow + "/";
    public String SaveFilePathImageWhatsappBusiness = RootDirectoryWhatsappBusinessImageShow + "/";
    public String SaveFilePathVideo = RootDirectoryWhatsappVideoShow + "/";
    public String SaveFilePathVideoWhatsappBusiness = RootDirectoryWhatsappBusinessVideoShow + "/";
    public WhatsappStatusModel fileItem;
    ArrayList<WhatsappStatusModel> list;
    int position;
    String filename;

    public static ViewPagerFragment newInstance(WhatsappStatusModel banner, ArrayList<WhatsappStatusModel> sliderListResponsesData, int position) {

        Bundle args = new Bundle();

        args.putString("image", banner.getUri().toString());
        args.putParcelableArrayList("list", sliderListResponsesData);
        args.putInt("position", position);

        ViewPagerFragment fragment = new ViewPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        image = getArguments().getString("image");
        list = getArguments().getParcelableArrayList("list");
        position = getArguments().getInt("position");
        fileItem = list.get(position);

        int layout = R.layout.fragment_viewpager_data;
        //int layout = R.layout.activity_full_image2;
        View root = inflater.inflate(layout, container, false);
        // root.setTag(position);
        //ImageView image_one = root.findViewById(R.id.image_one);
        ZoomageView image_one = root.findViewById(R.id.image_one);
        //ImageView image_one = (ImageView) root.findViewById(R.id.fullImag);
        FloatingActionButton floatingActionButton = root.findViewById(R.id.floatingActionButton1);


//        Bitmap myBitmap = BitmapFactory.decodeFile(list.get(position).getUri().getEncodedPath());
//
//        image_one.setImageBitmap(myBitmap);

//        Glide.with(getActivity().getApplicationContext())
//                   .load(list.get(position).getUri())
//                   .into(image_one);

        // Uri.fromFile(new File(image));

        try {
            Glide.with(getActivity().getApplicationContext())
                    .load(list.get(position).getUri())
                    .into(image_one);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString() + "", Toast.LENGTH_SHORT).show();
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //download();
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    downloadFile(list.get(position).getUri());
                } else {
                    download2();
                }
            }
        });


        return root;
    }

    public void downloadFile(Uri uri) {
        String mimeType = getActivity().getContentResolver().getType(uri);
        if (mimeType == null) {
            String path = getPath(getActivity(), uri);
            if (path == null) {
                filename = FilenameUtils.getName(uri.toString());
            } else {
                File file = new File(path);
                filename = file.getName();
            }
        } else {
            // Uri returnUri = data.getData();
            Uri returnUri = uri;
            Cursor returnCursor = getActivity().getContentResolver().query(returnUri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            filename = returnCursor.getString(nameIndex);
            String size = Long.toString(returnCursor.getLong(sizeIndex));
        }
        File fileSave = getActivity().getExternalFilesDir(null);
//        if (uri.toString().contains("com.whatsapp.w4b")){
//            downloadWhatsappBusiness(fileSave.getAbsolutePath());
//        }else {
            download(fileSave.getAbsolutePath());
      //  }

        String sourcePath = getActivity().getExternalFilesDir(null).toString();
        try {
            copyFileStream(new File(sourcePath + "/" + filename), uri, getActivity());

        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getActivity().getApplicationContext(), "Saved to My Downloads", Toast.LENGTH_LONG).show();

    }


    private void download(String uri) {
        File destFile;
        createFileFolder();
        final String path = uri.toString();

        //final String path = path_;
        String filename = path.substring(path.lastIndexOf("/") + 1);

        final File file = new File(uri);
        //final File file = new File(getPath(this,uri));
        //  final File file = new File(uri);


        File[] files = file.listFiles();

        for (int i = 0; i < files.length; ++i) {
            File file1 = files[i];
            if (file1.isDirectory()) {
                // traverse(file1);
            } else {
                if (file1.toString().contains(".mp4")) {
                    destFile = new File(SaveFilePathVideo);

                } else {

                    destFile = new File(SaveFilePathImage);
                }
                //getRealPathFromURI(getActivity(),fileItem.getUri());
                try {
                    FileUtils.copyFileToDirectory(file1, destFile);
                    //FileUtils.copyDirectoryToDirectory(file, destFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                // do something here with the file
            }
        }
    }
    private void downloadWhatsappBusiness(String uri) {
        File destFile;
        createFileFolder();
        final String path = uri.toString();

        //final String path = path_;
        String filename = path.substring(path.lastIndexOf("/") + 1);

        final File file = new File(uri);
        //final File file = new File(getPath(this,uri));
        //  final File file = new File(uri);


        File[] files = file.listFiles();

        for (int i = 0; i < files.length; ++i) {
            File file1 = files[i];
            if (file1.isDirectory()) {
                // traverse(file1);
            } else {
                if (file1.toString().contains(".mp4")) {
                    destFile = new File(SaveFilePathVideoWhatsappBusiness);

                } else {

                    destFile = new File(SaveFilePathImageWhatsappBusiness);
                }
                //getRealPathFromURI(getActivity(),fileItem.getUri());
                try {
                    FileUtils.copyFileToDirectory(file1, destFile);
                    //FileUtils.copyDirectoryToDirectory(file, destFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                // do something here with the file
            }
        }
    }

    private void copyFileStream(File dest, Uri uri, Context context)
            throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }

    public static String getPath(Context context, Uri uri) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // DocumentProvider
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    private void download2() {
        File destFile;
        createFileFolder();
        final String path = fileItem.getPath();

        //final String path = path_;
        String filename = path.substring(path.lastIndexOf("/") + 1);
       // getPath(getActivity(),fileItem.getUri());
       final File file = new File(path);
      //  final File file = new File(getPath(getActivity(),fileItem.getUri()));
       // DownloadImageFromPath(fileItem.getUri().toString());

        if (fileItem.getUri().toString().endsWith(".mp4")) {
             destFile = new File(SaveFilePathVideo);
        } else {

            destFile = new File(SaveFilePathImage);
        }
        //getRealPathFromURI(getActivity(),fileItem.getUri());
        try {
            FileUtils.copyFileToDirectory(file, destFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileNameChange = filename.substring(12);
        File newFile;
        if (fileItem.getUri().toString().endsWith(".mp4")) {
             newFile = new File(SaveFilePathVideo + fileNameChange);
        }
        else {
            newFile = new File(SaveFilePathImage + fileNameChange);

        }
      //  File newFile = new File(SaveFilePath + fileNameChange);
        String ContentType = "image/*";
        if (fileItem.getUri().toString().endsWith(".mp4")) {
            ContentType = "video/*";
        } else {
            ContentType = "image/*";
        }
        MediaScannerConnection.scanFile(getActivity().getApplicationContext(), new String[]{newFile.getAbsolutePath()}, new String[]{ContentType},
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    public void onMediaScannerConnected() {
                    }

                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
        File from,to;
        if (fileItem.getUri().toString().endsWith(".mp4")) {
             from = new File(SaveFilePathVideo, filename);

        }else {
            from = new File(SaveFilePathImage, filename);

        }
        if (fileItem.getUri().toString().endsWith(".mp4")) {
             to = new File(SaveFilePathVideo, fileNameChange);
        }else {
            to = new File(SaveFilePathImage, fileNameChange);
        }

       // File from = new File(SaveFilePath, filename);
       // File to = new File(SaveFilePath, fileNameChange);
        from.renameTo(to);

        Toast.makeText(getActivity().getApplicationContext(), "Saved to My Downloads", Toast.LENGTH_LONG).show();

    }




}



