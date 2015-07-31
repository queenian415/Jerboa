package com.jebora.jebora;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jebora.jebora.Utils.FileInfo;
import com.jebora.jebora.Utils.UserMainCheck;
import com.jebora.jebora.adapters.CircularAdapter;
import com.jebora.jebora.provider.ExtraArgumentKeys;
import com.jebora.jebora.provider.ImagesUrls;
import com.jebora.jebora.DetailActivity;
import com.jpardogo.listbuddies.lib.provider.ScrollConfigOptions;
import com.jpardogo.listbuddies.lib.views.ListBuddiesLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ListBuddiesFragment extends Fragment implements ListBuddiesLayout.OnBuddyItemClickListener {
    private static final String TAG = ListBuddiesFragment.class.getSimpleName();
    int mMarginDefault;
    int[] mScrollConfig;
    private boolean isOpenActivities;
    private CircularAdapter mAdapterLeft;
    private CircularAdapter mAdapterRight;
    @InjectView(R.id.listbuddies)
    ListBuddiesLayout mListBuddies;
    private List<String> mImagesLeft = new ArrayList<String>();
    private List<String> mImagesRight = new ArrayList<String>();
    private static Context mContext;
    private String ImageFullName;
    private String ImageName;
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;

    public static ListBuddiesFragment newInstance(boolean isOpenActivitiesActivated) {
        ListBuddiesFragment fragment = new ListBuddiesFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ExtraArgumentKeys.OPEN_ACTIVITES.toString(), isOpenActivitiesActivated);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOpenActivities = getArguments().getBoolean(ExtraArgumentKeys.OPEN_ACTIVITES.toString(), false);
        mMarginDefault = getResources().getDimensionPixelSize(com.jpardogo.listbuddies.lib.R.dimen.default_margin_between_lists);
        //mScrollConfig = getResources().getIntArray(R.attr.scrollFaster);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_listbuddies, container, false);
        setCameraAndGalleryButton(rootView);
        ButterKnife.inject(this, rootView);
        mContext = getActivity().getApplicationContext();
        //If we do this we need to uncomment the container on the xml layout
        //createListBuddiesLayoutDinamically(rootView);
        mAdapterLeft = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_small), mImagesLeft);
        mAdapterRight = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_tall), mImagesRight);
        if(ImagesUrls.allImages!=null)
            ImagesUrls.allImages.clear();
        if(UserMainCheck.GetKidSelected()!=null&&UserMainCheck.GetKidSelected().equals("全部照片"))
            ImagesUrls.allImages = loadLocalImages(false);
        else
            ImagesUrls.allImages = loadLocalImages(true);

        for (int i=0; i<ImagesUrls.allImages.size(); i++){
            if(i%7==0){
                mImagesLeft.add(ImagesUrls.allImages.get(i).toString().trim());
            }
            else if(i%7%2==0){
                mImagesLeft.add(ImagesUrls.allImages.get(i).toString().trim());
            }
            else{
                mImagesRight.add(ImagesUrls.allImages.get(i).toString().trim());
            }
        }
        mListBuddies.setAdapters(mAdapterLeft, mAdapterRight);
        mListBuddies.setOnItemClickListener(this);
        UserMainCheck.reqUpdOptMenu("ListBuddies");
        setHasOptionsMenu(true);
        setSpeed(0);
        return rootView;
    }


    @Override
    public void onResume(){
        mAdapterLeft.notifyDataSetChanged();
        mAdapterRight.notifyDataSetChanged();
        super.onResume();
    }
    /*private void createListBuddiesLayoutDinamically(View rootView) {
        mListBuddies = new ListBuddiesLayout(getActivity());
        resetLayout();
        //Once the container is created we can add the ListViewLayout into it
        //((FrameLayout)rootView.findViewById(R.id.<container_id>)).addView(mListBuddies);
    }*/

    @Override
    public void onBuddyItemClicked(AdapterView<?> parent, View view, int buddy, int position, long id) {
        if (isOpenActivities) {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("localImagePath", getClickedImage(buddy, position));
            startActivity(intent);
        } else {
            Resources resources = getResources();
            Toast.makeText(getActivity(), "List" + ": " + buddy + " " + "Position" + ": " + position, Toast.LENGTH_SHORT).show();
        }
    }

    private String getImage(int buddy, int position) {
        return buddy == 0 ? ImagesUrls.imageUrls_left[position] : ImagesUrls.imageUrls_right[position];
    }

    private String getClickedImage(int buddy, int position) {
        if(buddy == 0 && position < mImagesLeft.size()){
            return mImagesLeft.get(position);
        }
        else if (buddy == 1 && position < mImagesRight.size()){
            return mImagesRight.get(position);
        }
        else { return null; }

    }

    public void setGap(int value) {
        mListBuddies.setGap(value);
    }

    public void setSpeed(int value) {
        mListBuddies.setSpeed(value);
    }

    public void setDividerHeight(int value) {
        mListBuddies.setDividerHeight(value);
    }

    public void setGapColor(int color) {
        mListBuddies.setGapColor(color);
    }

    public void setAutoScrollFaster(int option) {
        mListBuddies.setAutoScrollFaster(option);
    }

    public void setScrollFaster(int option) {
        mListBuddies.setManualScrollFaster(option);
    }

    public void setDivider(Drawable drawable) {
        mListBuddies.setDivider(drawable);
    }

    public void setOpenActivities(Boolean openActivities) {
        this.isOpenActivities = openActivities;
    }

    public List<String> loadLocalImages(boolean loadUserOnly) {
        List<String> imagesList = new ArrayList<>();

        List<String> imagesPath = FileInfo.loadLocalCompressedImagesPath(mContext, loadUserOnly);
        for (String path : imagesPath) {
            imagesList.add("file://" + path);
        }

        return imagesList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != -1) return;

        if(requestCode == CAMERA_REQUEST){
            saveBitmapToServer(ImageFullName, ImageName);
            saveCompressedToLocal(ImageFullName, ImageName);
        }
        else if(requestCode == GALLERY_REQUEST){
            Uri origUri = data.getData();
            File kidDirectory = FileInfo.getUserKidDirectory(mContext);
            File picked_photo = new File(FileInfo.getRealPathFromURI(origUri, mContext));
            Date photoAddedTime = new Date();
            long unsignedHashCode = photoAddedTime.hashCode() & 0x00000000ffffffffL;
            String fileName = unsignedHashCode + ".jpg";
            String dstPath = kidDirectory.getAbsolutePath() + File.separator +
                    fileName;
            try{
                File dstFile = FileInfo.newFile(dstPath);
                FileInfo.copyFile(picked_photo, dstFile);
                saveBitmapToServer(dstPath, fileName);
                saveCompressedToLocal(dstPath, fileName);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void saveBitmapToServer(final String path, final String filename) {
        if (isNetworkConnected()) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    ServerCommunication.saveImageInBackground(mContext, path, filename, UserRecorder.getPreferredKidId());
                }
            };
            new Thread(task, "serverThread").start();
        }
    }

    public void saveCompressedToLocal(String path, String fileName) {
        File file = new File(path);
        Bitmap bmp = FileInfo.compressImage(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        try {
            OutputStream outputStream = new FileOutputStream(
                    FileInfo.getUserKidCompressedDirectory(mContext).getAbsolutePath() + File.separator + fileName);
            bos.writeTo(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnected());
    }

    public void setCameraAndGalleryButton(View rootView){
        ImageButton cameraButton = (ImageButton) rootView.findViewById(R.id.camera_button);
        ImageButton galleryButton = (ImageButton) rootView.findViewById(R.id.gallery_button);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Date photoTakenTime = new Date();
                int hashCode = photoTakenTime.hashCode();
                // Avoid negative hashcode in filename
                long unsignedHashCode = hashCode & 0x00000000ffffffffL;
                String filename = unsignedHashCode + ".jpg";
                String filePath = FileInfo.getUserKidDirectory(mContext).toString() + File.separator + filename;
                Uri imageUri = Uri.fromFile(new File(filePath));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                ImageFullName = filePath;
                ImageName = filename;
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");
                    startActivityForResult(i, GALLERY_REQUEST);
                } else {
                    i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_REQUEST);
                }
            }
        });
    }

    public void resetLayout() {
        mListBuddies.setGap(mMarginDefault)
                .setSpeed(ListBuddiesLayout.DEFAULT_SPEED)
                .setDividerHeight(mMarginDefault)
                .setGapColor(getResources().getColor(R.color.frame))
                .setAutoScrollFaster(mScrollConfig[ScrollConfigOptions.RIGHT.getConfigValue()])
                .setManualScrollFaster(mScrollConfig[ScrollConfigOptions.LEFT.getConfigValue()])
                .setDivider(getResources().getDrawable(R.drawable.divider));
    }

}