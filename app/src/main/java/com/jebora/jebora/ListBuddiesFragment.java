package com.jebora.jebora;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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

import java.io.File;
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
        mImagesLeft.addAll(Arrays.asList(ImagesUrls.imageUrls_left));// = loadLocalImages();
        mImagesRight.addAll(Arrays.asList(ImagesUrls.imageUrls_right));
        mAdapterLeft = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_small), mImagesLeft);
        mAdapterRight = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_tall), mImagesRight);
        mListBuddies.setAdapters(mAdapterLeft, mAdapterRight);
        mListBuddies.setOnItemClickListener(this);
        UserMainCheck.reqUpdOptMenu("ListBuddies");
        setHasOptionsMenu(true);
        setSpeed(0);
        return rootView;
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
            intent.putExtra(DetailActivity.EXTRA_URL, getImage(buddy, position));
            startActivity(intent);
        } else {
            Resources resources = getResources();
            Toast.makeText(getActivity(), "List" + ": " + buddy + " " + "Position" + ": " + position, Toast.LENGTH_SHORT).show();
        }
    }

    private String getImage(int buddy, int position) {
        return buddy == 0 ? ImagesUrls.imageUrls_left[position] : ImagesUrls.imageUrls_right[position];
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

    public List<String> loadLocalImages() {
        List<String> imagesList = new ArrayList<>();

        File dir = new File(FileInfo.getUserKidDirectory(getActivity().getApplicationContext()).toString());
        File file[] = dir.listFiles();

        for (int i = 0; i < file.length; i ++) {
            if (file[i].isFile()) {
                String filename = file[i].getName();
                // Make sure it's a JPEG image
                String ext = filename.substring(filename.lastIndexOf('.') + 1);
                if (ext.equals("jpg")) {
                    imagesList.add("file://" + file[i].getAbsolutePath());
                }
            }
        }
        return imagesList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != -1) return;

        if(requestCode == CAMERA_REQUEST){
            saveBitmapToServer(ImageFullName, ImageName);
        }
        else if(requestCode == GALLERY_REQUEST){
            Uri origUri = data.getData();
            File kidDirectory = FileInfo.getUserKidDirectory(mContext);
            File picked_photo = new File(FileInfo.getRealPathFromURI(origUri, mContext));
            Date photoAddedTime = new Date();
            String fileName = Integer.toString(photoAddedTime.hashCode()) + ".jpg";
            String dstPath = kidDirectory.toString() + File.separator +
                    fileName;
            try{
                File dstFile = FileInfo.newFile(dstPath);
                FileInfo.copyFile(picked_photo, dstFile);
                saveBitmapToServer(dstFile.toString(), fileName);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void saveBitmapToServer(final String src, final String filename) {
        if (isNetworkConnected()) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    ServerCommunication.saveImageInBackground(mContext, src, filename, UserRecorder.getPreferredKidId());
                }
            };
            new Thread(task, "serverThread").start();
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