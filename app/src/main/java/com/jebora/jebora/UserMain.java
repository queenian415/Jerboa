package com.jebora.jebora;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.jebora.jebora.adapters.CircularAdapter;
import com.jebora.jebora.provider.ImagesUrls;
import com.jpardogo.listbuddies.lib.provider.ScrollConfigOptions;
import com.jpardogo.listbuddies.lib.views.ListBuddiesLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // 设置抽屉
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
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
            ft.hide(lastFragment);
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.user_main, menu);
            restoreActionBar();
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
        private ImageView showImage;
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
        private List<String> mImagesLeft = new ArrayList<String>();
        private List<String> mImagesRight = new ArrayList<String>();
        //end

        public static ContentFragment newInstance(String title) {
            ContentFragment fragment = new ContentFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_TITLE, title);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(getArguments().getString(ARG_SECTION_TITLE).equals("Jebora")){
                View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
                showImage = (ImageView) rootView.findViewById(R.id.selected_image);
                ImageButton cameraButton = (ImageButton) rootView.findViewById(R.id.camera_button);
                ImageButton galleryButton = (ImageButton) rootView.findViewById(R.id.gallery_button);

                cameraButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                return rootView;
            }
            else if(getArguments().getString(ARG_SECTION_TITLE).equals("关于我们")){
                View rootView = inflater.inflate(R.layout.fragment_user_main_list, container, false);
                ButterKnife.inject(this, rootView);

                //If we do this we need to uncomment the container on the xml layout
                //createListBuddiesLayoutDinamically(rootView);
                mImagesLeft.addAll(Arrays.asList(ImagesUrls.imageUrls_left));
                mImagesRight.addAll(Arrays.asList(ImagesUrls.imageUrls_right));
                mAdapterLeft = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_small), mImagesLeft);
                mAdapterRight = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_tall), mImagesRight);
                mListBuddies.setAdapters(mAdapterLeft, mAdapterRight);
                //mListBuddies.setOnItemClickListener(this);
                return rootView;
            }
            else{
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
                Bundle extras = data.getExtras();
                Bitmap photo = (Bitmap) extras.get("data");
                showImage.setImageBitmap(photo);
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
                showImage.setImageBitmap(bitmap);
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
    }
}