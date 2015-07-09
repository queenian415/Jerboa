package com.jebora.jebora;

import android.accounts.NetworkErrorException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jebora.jebora.Utils.FileInfo;
import com.jebora.jebora.adapters.CircularAdapter;
import com.jebora.jebora.provider.ImagesUrls;
import com.jpardogo.listbuddies.lib.provider.ScrollConfigOptions;
import com.jpardogo.listbuddies.lib.views.ListBuddiesLayout;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class UserMain extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * 左侧划出抽屉内部fragment
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * 存放上次显示在action bar中的title
     * {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private Fragment currentFragment;
    private Fragment lastFragment;

    final static List<String> listNames = new ArrayList<String>();
    final static List<String> listIds = new ArrayList<String>();
    private static int kidsnumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        Map <String, String> kids = UserRecorder.getKidList();
        for (String key : kids.keySet()) {
            System.out.println(key);
            System.out.println(kids.get(key));//will print value associated with key
            listNames.add(kids.get(key));
            listIds.add(key);
            kidsnumber++;
        }
        listNames.add("全部照片");
        listNames.add("+");
        listIds.add("全部照片");
        listIds.add("+");

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getBaseContext(),
                android.R.layout.simple_spinner_dropdown_item, listNames
                );
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setElevation(0);
        ActionBar.OnNavigationListener navigationListener = new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                String[] temp = new String[listIds.size()];
                temp = listIds.toArray(temp);
                if(temp[itemPosition].equals("+")){
                    kidsnumber++;
                    startActivity(new Intent(UserMain.this, SignUp_2.class));
                }
                else
                    onNavigationDrawerItemSelected(temp[itemPosition]);
                return false;
            }
        };
        // 设置抽屉
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        getSupportActionBar().setListNavigationCallbacks(adapter, navigationListener);

    }

    @Override
    public void onNavigationDrawerItemSelected(String title) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        currentFragment = fragmentManager.findFragmentByTag(title);
        if(currentFragment == null) {
            currentFragment = ContentFragment.newInstance(title);
            ft.add(R.id.container, currentFragment, title);
        }
        if(lastFragment != null) {
            ft.detach(lastFragment);
        }
        if(currentFragment.isDetached()){
            ft.attach(currentFragment);
        }
        ft.show(currentFragment);
        lastFragment = currentFragment;
        ft.commit();
        onSectionAttached(title);
    }

    public void onSectionAttached(String title) {
        mTitle = title;
    }

    public void restoreActionBar() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(!mNavigationDrawerFragment.isDrawerOpen()){
            //getMenuInflater().inflate(R.menu.user_main, menu);
            String[] temp = new String[listNames.size()];
            temp = listNames.toArray(temp);
            HashMap<String, String> kidlist = UserRecorder.getKidList();
            String CurrentKid = kidlist.get(mTitle);
            ActionBar actionBar = getSupportActionBar();
            for(int i=0; i<kidsnumber; i++){
                if(CurrentKid != null) {
                    if (CurrentKid.equals(temp[i]))
                        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                }
                else if(mTitle.equals("全部照片")||mTitle.equals("Jebora")||mTitle.equals("UserMain"))
                    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                else{
                    getMenuInflater().inflate(R.menu.user_main, menu);
                    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                }
            }

            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 内容fragment
     */
    public static class ContentFragment extends Fragment {

        private static final int CAMERA_REQUEST = 1;
        private static final int GALLERY_REQUEST = 2;
        private static final String ARG_SECTION_TITLE = "section_title";
        private static Context mContext;
        /**
         * 返回根据title参数创建的fragment
         */
        //parameters for listbuddies
        private static final String TAG = UserMainList.class.getSimpleName();
        int mMarginDefault;
        int[] mScrollConfig;
        private boolean isOpenActivities;
        @InjectView(R.id.listbuddies)
        ListBuddiesLayout mListBuddies;
        private String ImageFullName;
        private String ImageName;

        //end

        public static ContentFragment newInstance(String title) {
            ContentFragment fragment = new ContentFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_TITLE, title);
            fragment.setArguments(args);

            if(title.equals("我的照片"))
                fragment.setHasOptionsMenu(true);

            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mContext = getActivity().getApplicationContext();
            String[] temp = new String[listIds.size()];
            temp = listIds.toArray(temp);

            if(getArguments().getString(ARG_SECTION_TITLE).equals("我的照片")){
                View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
                return rootView;
            }
            else if(getArguments().getString(ARG_SECTION_TITLE).equals("Jebora")){
                return setUpMainPage(inflater, container);
            }
            else if (getArguments().getString(ARG_SECTION_TITLE).equals("注销")) {
                ParseUser.logOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                return null;
            }
            else if(getArguments().getString(ARG_SECTION_TITLE).equals("关注我们")){
                View rootView = inflater.inflate(R.layout.preview_temp_layout, container, false);
                Button preview = (Button) rootView.findViewById(R.id.preview);
                preview.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ProductSelect.class);
                        startActivity(intent);
                    }
                });
                return rootView;
            }
            else{
                for (int i=0; i<=kidsnumber; i++) {
                    if(getArguments().getString(ARG_SECTION_TITLE).equals(temp[i])){
                        return setUpMainPage(inflater, container);
                    }
                }
                View rootView = inflater.inflate(R.layout.fragment_main, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getArguments().getString(ARG_SECTION_TITLE));
                return rootView;
            }

        }

        private View setUpMainPage(LayoutInflater inflater, ViewGroup container) {
            View rootView = inflater.inflate(R.layout.fragment_user_main_list, container, false);

            setCameraAndGalleryButton(rootView);

            ButterKnife.inject(this, rootView);
            setHasOptionsMenu(true);

            //List<String> mImagesLeft = ServerCommunication.loadImages(getActivity().getApplicationContext());
            List<String> mImagesRight = new ArrayList<>();
            List<String> mImagesLeft = loadLocalImages();
            //If we do this we need to uncomment the container on the xml layout
            //createListBuddiesLayoutDinamically(rootView);
            mImagesRight.addAll(Arrays.asList(ImagesUrls.imageUrls_right));
            CircularAdapter mAdapterLeft = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_small), mImagesLeft);
            CircularAdapter mAdapterRight = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_tall), mImagesRight);
            mListBuddies.setAdapters(mAdapterLeft, mAdapterRight);
            mListBuddies.setSpeed(0);
            //mListBuddies.setOnItemClickListener(this);
            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(resultCode != RESULT_OK) return;

            if(requestCode == CAMERA_REQUEST){
                saveBitmapToServer(ImageFullName, ImageName);
            }
            else if(requestCode == GALLERY_REQUEST){
                Uri origUri = data.getData();
                File kidDirectory = FileInfo.getUserKidDirectory(mContext);
                File picked_photo = new File(FileInfo.getRealPathFromURI(origUri, mContext));
                Date photoAddedTime = new Date();
                String fileName = Integer.toString(photoAddedTime.hashCode());
                String dstPath = kidDirectory.toString() + File.separator +
                        fileName + ".jpg";
                try{
                    File dstFile = FileInfo.newFile(dstPath);
                    FileInfo.copyFile(picked_photo, dstFile);
                    saveBitmapToServer(dstFile.toString(), fileName);
                } catch (Exception e){
                    e.printStackTrace();
                }
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

        public void resetLayout() {
            mListBuddies.setGap(mMarginDefault)
                    .setSpeed(ListBuddiesLayout.DEFAULT_SPEED)
                    .setDividerHeight(mMarginDefault)
                    .setGapColor(getResources().getColor(R.color.frame))
                    .setAutoScrollFaster(mScrollConfig[ScrollConfigOptions.RIGHT.getConfigValue()])
                    .setManualScrollFaster(mScrollConfig[ScrollConfigOptions.LEFT.getConfigValue()])
                    .setDivider(getResources().getDrawable(R.drawable.divider));
        }

        public void saveBitmapToServer(final String src, final String filename) {
            if (isNetworkConnected()) {
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        ServerCommunication.saveImageInBackground(mContext, src, filename);
                    }
                };
                new Thread(task, "serverThread").start();
            }
        }

        public List<String> loadLocalImages() {
            List<String> imagesList = new ArrayList<>();

            File dir = new File(FileInfo.getUserKidDirectory(mContext).toString());
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
                    String filename = photoTakenTime.hashCode() + ".jpg";
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
    }
}