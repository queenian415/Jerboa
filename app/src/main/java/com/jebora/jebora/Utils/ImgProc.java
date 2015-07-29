package com.jebora.jebora.Utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import java.util.Random;

/**
 * Created by danchen on 15-07-24.
 */
public class ImgProc {
    static final double GS_RED = 0.3;
    static final double GS_GREEN = 0.59;
    static final double GS_BLUE =  0.11;
    static final int MAX_COLOR = 255;
    static final double MAX_COLOR_D = 255.0;

    public static void doGreyscale(int[] pixels, int width, int height){
        //int width = src.getWidth();
        //int height = src.getHeight();
        //int[] pixels = new int[width * height];
        //src.getPixels(pixels, 0, width, 0, 0, width, height);
        int A, R, B, G, idx;

        for(int x = 0; x<width; ++x){
            for(int y = 0; y < height; ++y){
                idx = y*width + x;
                A = Color.alpha(pixels[idx]);
                R = Color.red(pixels[idx]);
                B = Color.blue(pixels[idx]);
                G = Color.green(pixels[idx]);
                R = G = B = (int)(GS_RED*R + GS_GREEN*G + GS_BLUE*B);
                pixels[idx] = Color.argb(A, R, G, B);
            }
        }
        //Bitmap bm = Bitmap.createBitmap(width, height, src.getConfig());
        //bm.setPixels(pixels, 0, width, 0, 0, width, height);
        //return bm;
    }

    public static void doSepiaTone(int[] pixels, int depth, double red, double green, double blue,
                                     int width, int height){
        //int width = src.getWidth();
        //int height = src.getHeight();
        //int[] pixels = new int[width * height];
        //src.getPixels(pixels, 0, width, 0, 0, width, height);
        int idx, A, R, G, B;

        for(int x = 0; x < width; ++x){
            for (int y = 0; y < height; ++y){
                idx = y*width + x;
                A = Color.alpha(pixels[idx]);
                R = Color.red(pixels[idx]);
                G = Color.green(pixels[idx]);
                B = Color.blue(pixels[idx]);
                R = G = B = (int)(GS_RED*R + GS_GREEN*G + GS_BLUE*B);

                R = boundColor(R + (int) (depth * red));
                G = boundColor(G + (int) (depth * green));
                B = boundColor(B + (int) (depth * blue));

                pixels[idx] = Color.argb(A, R, G, B);
            }
        }
        //Bitmap bm = Bitmap.createBitmap(width, height, src.getConfig());
        //bm.setPixels(pixels, 0, width, 0, 0, width, height);
        //return bm;
    }

    public static Bitmap doContrast(Bitmap src, double contrastLevel){
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        int idx, A, R, G, B;

        double contrast = Math.pow((100+contrastLevel)/100, 2);

        for (int x = 0; x < width; ++x){
            for (int y = 0; y < height; ++y){
                idx = y*width + x;
                A = Color.alpha(pixels[idx]);
                R = boundColor((int) (((((Color.red(pixels[idx]) / MAX_COLOR_D) - 0.5) * contrast)+0.5)*MAX_COLOR_D));
                G = boundColor((int) (((((Color.green(pixels[idx])/MAX_COLOR_D)-0.5)*contrast)+0.5)*MAX_COLOR_D));
                B = boundColor((int) (((((Color.blue(pixels[idx])/MAX_COLOR_D)-0.5)*contrast)+0.5)*MAX_COLOR_D));
                pixels[idx] = Color.argb(A, R, G, B);
            }
        }
        Bitmap bm = Bitmap.createBitmap(width, height, src.getConfig());
        bm.setPixels(pixels, 0, width, 0, 0, width, height);
        return bm;
    }

    public static Bitmap doBrightness(Bitmap src, int brightnessLevel){
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        int idx, A, R, G, B;

        for (int x = 0; x < width; ++x){
            for (int y = 0; y < height; ++y){
                idx = y*width + x;
                int pix = pixels[idx];
                A = Color.alpha(pix);
                R = boundColor(Color.red(pix) + brightnessLevel);
                G = boundColor(Color.green(pix) + brightnessLevel);
                B = boundColor(Color.blue(pix) + brightnessLevel);
                pixels[idx] = Color.argb(A, R, G, B);
            }
        }
        Bitmap bm = Bitmap.createBitmap(width, height, src.getConfig());
        bm.setPixels(pixels, 0, width, 0, 0, width, height);
        return bm;
    }

    public static Bitmap doTransparent(Bitmap src, int transparency){
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        int idx;

        for (int x = 0; x < width; ++x){
            for (int y = 0; y < height; ++y){
                idx = y*width + x;
                pixels[idx] = Color.argb(
                        boundColor(Color.alpha(pixels[idx]) - transparency),
                        Color.red(pixels[idx]),
                        Color.green(pixels[idx]),
                        Color.blue(pixels[idx]));
            }
        }
        Bitmap bm = Bitmap.createBitmap(width, height, src.getConfig());
        bm.setPixels(pixels, 0, width, 0, 0, width, height);
        return bm;
    }

    public static Bitmap doSnow(Bitmap src){
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        int idx;

        Random rand = new Random();

        for (int x = 0; x < width; ++x){
            for (int y = 0; y < height; ++y){
                idx = y*width + x;
                int randomColor = Color.rgb(rand.nextInt(MAX_COLOR), rand.nextInt(MAX_COLOR),
                        rand.nextInt(MAX_COLOR));
                pixels[idx] |= randomColor;
            }
        }
        Bitmap bm = Bitmap.createBitmap(width, height, src.getConfig());
        bm.setPixels(pixels, 0, width, 0, 0, width, height);
        return bm;
    }


    public static Bitmap doBlur(Bitmap src){
        float[] BlurKernel = new float[]{
                0, 2, 0,
                2, 2, 2,
                0, 2, 0};
        double BlurFactor = 10.0;
        double BlurOffset = 0.0;
        Matrix BlurConfig = new Matrix();
        BlurConfig.setValues(BlurKernel);
        return ConvolutionMatrix.convolute(src, BlurConfig, BlurFactor, BlurOffset);
    }

    public static Bitmap doGaussianBlur(Bitmap src){
        float[] GaussianBlurKernel = new float[]{
                1, 2, 1,
                2, 4, 2,
                1, 2, 1};
        double GBFactor = 16.0;
        double GBOffset = 0.0;
        Matrix GBConfig = new Matrix();
        GBConfig.setValues(GaussianBlurKernel);
        return ConvolutionMatrix.convolute(src, GBConfig, GBFactor, GBOffset);
    }

    public static Bitmap doEmboss(Bitmap src){
        float[] EmbossKernel = new float[]{
                -1, 0, -1,
                0, 4, 0,
                -1, 0, -1};
        double EmbossFactor = 1.0;
        double EmbossOffset = 127.0;
        Matrix EmbossConfig = new Matrix();
        EmbossConfig.setValues(EmbossKernel);
        return ConvolutionMatrix.convolute(src, EmbossConfig, EmbossFactor, EmbossOffset);
    }

    public static Bitmap doSharpen(Bitmap src, float weight){
        float[] ShaprKernel = new float[]{
                0, -2, 0,
                -2, weight, -2,
                0, -2, 0};
        double SharpFactor = weight - 8;
        double SharpOffset = 0.0;
        Matrix SharpConfig = new Matrix();
        SharpConfig.setValues(ShaprKernel);
        return ConvolutionMatrix.convolute(src, SharpConfig, SharpFactor, SharpOffset);
    }

    public static int boundColor(int color){
        if (color > MAX_COLOR) return MAX_COLOR;
        else if(color < 0 ) return 0;
        else return color;
    }
}
