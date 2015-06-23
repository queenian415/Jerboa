package com.jebora.jebora;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mshzhb on 15/6/23.
 */
public class LoadModels extends Activity {
    /** Called when the activity is first created. */
    private GLSurfaceView glView;

    private MyRenderer mr;

    public static float dx = 0;
    public static float dy = 0;

    public static float xpos = -1;
    public static float ypos = -1;
    private static android.content.Context Context = null;

    public static Context getContext() {
        return Context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        // 加载文件

        new LoadAssets(getResources());

        glView = new GLSurfaceView(this);

        mr = new MyRenderer();

        glView.setRenderer(mr);

        setContentView(glView);

    }

    public boolean onTouchEvent(MotionEvent m) {

        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            xpos = m.getX();
            ypos = m.getY();
            return true;
        }

        if (m.getAction() == MotionEvent.ACTION_UP) {
            xpos = -1;
            ypos = -1;
            dx = 0;
            dy = 0;
            return true;
        }

        if (m.getAction() == MotionEvent.ACTION_MOVE) {
            float xd = m.getX() - xpos;
            float yd = m.getY() - ypos;

            xpos = m.getX();
            ypos = m.getY();

            dx = xd / -150f;
            dy = yd / -150f;
            return true;
        }

        try {
            Thread.sleep(10);
        } catch (Exception e) {

        }

        return super.onTouchEvent(m);
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}


// 载入Assets文件夹下的文件

class LoadAssets {

    public static Resources res;

    public LoadAssets(Resources resources) {

        res = resources;

    }

    public static InputStream loadf(String fileName) {

        AssetManager am = LoadAssets.res.getAssets();

        try {

            return am.open(fileName, AssetManager.ACCESS_UNKNOWN);

        } catch (IOException e) {

            return null;

        }

    }

}