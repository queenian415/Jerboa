package com.jebora.jebora.Utils;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.jebora.jebora.R;

/**
 * Created by mshzhb on 15/6/21.
 */
public class PreviewProduct extends ActionBarActivity {
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview_product);
        imageView = (ImageView) findViewById(R.id.logo);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            // 设置开始点
            private PointF startPoint = new PointF();
            // 设置图片位置的变换矩阵
            private Matrix matrix = new Matrix();
            // 设置图片当前位置的变换矩阵
            private Matrix currentMatrix = new Matrix();
            // 初始化模式参数
            private int mode = 0;


            // 拖拉模式
            private static final int DRAG = 1;
            // 缩放模式
            private static final int ZOOM = 2;
            // 开启缩放效果的门槛
            private static final float ZOOM_THRESHOLD = 10.0f;
            // 开始距离
            private float startDistance;
            // 中心点
            private PointF middlePoint;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mode = DRAG;
                        // 记录图片当前的移动位置
                        currentMatrix.set(imageView.getImageMatrix());
                        // 记录开始坐标
                        startPoint.set(event.getX(), event.getY());
                        break;

                    // 当屏幕上已经有触点（手指），再有手指按下时触发该事件
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mode = ZOOM;
                        //得到两点间的距离
                        startDistance = getDistance(event);
                        //如果大于
                        if(startDistance > ZOOM_THRESHOLD) {
                            //得到两点的中间点
                            middlePoint = getMiddlePoint(event);
                            // 记录图片当前的缩放比例
                            currentMatrix.set(imageView.getImageMatrix());
                        }
                        break;


                    case MotionEvent.ACTION_MOVE:
                        if(mode == DRAG) {
                            // 获取X轴移动距离
                            float distanceX = event.getX() - startPoint.x;
                            // 获取Y轴移动距离
                            float distanceY = event.getY() - startPoint.y;
                            // 在上次移动停止位置的基础上再进行移动
                            matrix.set(currentMatrix);
                            // 实现图片位置移动
                            matrix.postTranslate(distanceX, distanceY);
                        }else if(mode == ZOOM) {
                            // 结束距离
                            float endDistance = getDistance(event);
                            if(endDistance > ZOOM_THRESHOLD) {
                                // 缩放比例
                                float scale = endDistance / startDistance;
                                matrix.set(currentMatrix);
                                matrix.postScale(scale, scale, middlePoint.x, middlePoint.y);
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        // 当手指离开屏幕，但屏幕上仍有其他触点（手指）时触发该事件
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = 0;
                        break;
                }
                imageView.setImageMatrix(matrix);
                return true;
            }

        });
    }


    /**
     * 计算两点之间的距离
     */
    public static float getDistance(MotionEvent event) {

        float disX = event.getX(1) - event.getX(0);
        float disY = event.getY(1) - event.getY(0);
        return FloatMath.sqrt(disX * disX + disY * disY);
    }

    /**
     * 计算两点之间的中间点
     */
    public static PointF getMiddlePoint(MotionEvent event) {
        float midX = (event.getX(0) + event.getX(1)) /2;
        float midY = (event.getY(0) + event.getY(1)) /2;
        return new PointF(midX, midY);
    }}
