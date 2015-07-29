package com.jebora.jebora.Utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

/**
 * Created by danchen on 15-07-25.
 */
public class ConvolutionMatrix
{
    public static final int SIZE = 3;

    public static Bitmap convolute(Bitmap src, Matrix mat, double factor, double offset){
        int width = src.getWidth();
        int height = src.getHeight();
        int R, G, B, sumR, sumG, sumB;
        int pix, idx;
        float mat_value;
        float [] mat_values = new float[SIZE * SIZE];
        mat.getValues(mat_values);
        int[] rtPxs = new int[width * height];
        int[] srcPxs = new int[width * height];
        src.getPixels(srcPxs, 0, width, 0, 0, width, height);

        for (int x = 0, w = width - SIZE + 1; x < w; ++x){
            for (int y = 0, h = height - SIZE + 1; y < h; ++y){
                idx = (x + 1) + (y + 1)*width;
                sumR = sumB = sumG = 0;
                for(int mx = 0; mx < SIZE; ++mx){
                    for (int my = 0; my < SIZE; ++my){
                        pix = srcPxs[(x + mx) + (y + my)*width];
                        mat_value = mat_values[mx + my*SIZE];
                        sumR += (Color.red(pix) * mat_value);
                        sumG += (Color.green(pix) * mat_value);
                        sumB += (Color.blue(pix) * mat_value);
                    }
                }

                R = ImgProc.boundColor((int)(sumR/factor + offset));
                G = ImgProc.boundColor((int)(sumG/factor + offset));
                B = ImgProc.boundColor((int)(sumB/factor + offset));

                rtPxs[idx] = Color.argb(Color.alpha(srcPxs[idx]), R, G, B);
            }
        }
        return Bitmap.createBitmap(rtPxs, width, height, src.getConfig());
    }
}
