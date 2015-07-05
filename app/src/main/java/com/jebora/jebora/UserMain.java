package com.jebora.jebora;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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

    private static List<String> mImagesLeft = new ArrayList<String>();
    private static List<String> mImagesRight = new ArrayList<String>();
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
        /**
         * 返回根据title参数创建的fragment
         */
        //parameters for listbuddies
        private static final String TAG = UserMainList.class.getSimpleName();
        int mMarginDefault;
        int[] mScrollConfig;
        private boolean isOpenActivities;
        private CircularAdapter mAdapterLeft;
        private CircularAdapter mAdapterRight;
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

            String[] temp = new String[listIds.size()];
            temp = listIds.toArray(temp);

            if(getArguments().getString(ARG_SECTION_TITLE).equals("我的照片")){
                View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
                return rootView;
            }
            else if(getArguments().getString(ARG_SECTION_TITLE).equals("Jebora")){
                View rootView = inflater.inflate(R.layout.fragment_user_main_list, container, false);

                setCameraAndGalleryButton(rootView);

                ButterKnife.inject(this, rootView);
                setHasOptionsMenu(true);

                mImagesLeft = ServerCommunication.loadImages(getActivity().getApplicationContext());
                // mImagesLeft = loadLocalImages();
                //If we do this we need to uncomment the container on the xml layout
                //createListBuddiesLayoutDinamically(rootView);
                mImagesRight.addAll(Arrays.asList(ImagesUrls.imageUrls_right));
                mAdapterLeft = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_small), mImagesLeft);
                mAdapterRight = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_tall), mImagesRight);
                mListBuddies.setAdapters(mAdapterLeft, mAdapterRight);
                mListBuddies.setSpeed(0);
                //mListBuddies.setOnItemClickListener(this);
                return rootView;
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
                        View rootView = inflater.inflate(R.layout.fragment_user_main_list, container, false);
                        setCameraAndGalleryButton(rootView);
                        ButterKnife.inject(this, rootView);
                        setHasOptionsMenu(true);
                        mImagesLeft = ServerCommunication.loadImages(getActivity().getApplicationContext());
                        // mImagesLeft = loadLocalImages();
                        mImagesRight.addAll(Arrays.asList(ImagesUrls.imageUrls_right));
                        mAdapterLeft = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_small), mImagesLeft);
                        mAdapterRight = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_tall), mImagesRight);
                        mListBuddies.setAdapters(mAdapterLeft, mAdapterRight);
                        mListBuddies.setSpeed(0);
                        //mListBuddies.setOnItemClickListener(this);
                        return rootView;
                    }
                }
                View rootView = inflater.inflate(R.layout.fragment_main, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getArguments().getString(ARG_SECTION_TITLE));
                return rootView;
            }

        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(resultCode != RESULT_OK) return;

            if(requestCode == CAMERA_REQUEST){
                saveBitmapToServer(ImageFullName, ImageName);
            }
            else if(requestCode == GALLERY_REQUEST){
                Uri origUri = data.getData();
                Bitmap bitmap = null;
                try{
                    ContentResolver cr = getActivity().getApplicationContext().getContentResolver();
                    bitmap= MediaStore.Images.Media.getBitmap(cr, origUri);
                } catch (Exception e){
                    e.printStackTrace();
                }
                File appExtDir = getAppDir();
                Date photoAddedTime = new Date();
                saveBitmapToPath(bitmap, appExtDir.toString(), Integer.toString(photoAddedTime.hashCode()));
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

        public File getAppDir(){
            // get user & kid id. store image in the corresponding directory
            String userId = ParseUser.getCurrentUser().getObjectId();
            SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(App.PREFIX + "KIDID", 0);
            String kidId = sharedPreferences.getString("kidid", null);

            File extFile = getActivity().getApplicationContext().getExternalFilesDir(null);
            if(!extFile.exists()){
                if(!extFile.mkdir()){
                    Log.e("IO Error", "Error cannot make jerboa dir");
                }
            }

            // user directory
            String userPath = extFile.getAbsolutePath() + File.separator + userId;
            File userFile = new File(userPath);
            if(!userFile.exists()){
                if(!userFile.mkdir()){
                    Log.e("IO Error", "Error cannot make jerboa dir");
                }
            }

            if (kidId == null) {
                return userFile;
            } else {
            // user's kid directory
                String kidPath = userPath + File.separator + kidId;
                File kidFile = new File(kidPath);
                if (!kidFile.exists()) {
                    if (!kidFile.mkdir()) {
                        Log.e("IO Error", "Error cannot make jerboa dir");
                    }
                }
                return kidFile;
            }
        }

        public boolean saveBitmapToPath(Bitmap bm, String path, String filename){
            boolean result = false;
            FileOutputStream fOut = null;

            File f = new File(path + File.separator + filename + ".png");
            boolean fExists = f.exists();
            try{
                if(!fExists){
                    f.createNewFile();
                }
                else{
                    f.delete();
                    f.createNewFile();
                }
                fOut = new FileOutputStream(f);
                bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.close();
                result = true;
            }catch (Exception e){
                result = false;
                e.printStackTrace();
            }

            // save to server
            saveBitmapToServer(path + File.separator + filename + ".png", filename);
            return result;
        }

        public void saveBitmapToServer(final String src, final String filename) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    ServerCommunication.saveImageInBackground(getActivity().getApplicationContext(), src, filename);
                }
            };
            new Thread(task, "serverThread").start();
        }

        public List<String> loadLocalImages() {
            List<String> imagesList = new ArrayList<>();

            File dir = new File(getAppDir().toString());
            File file[] = dir.listFiles();

            for (int i = 0; i < file.length; i ++) {
                if (file[i].isFile()) {
                    imagesList.add("file://" + file[i].getAbsolutePath());
                }
            }
            return imagesList;
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
                    String filePath = getAppDir().toString() + File.separator + filename;
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