#include <jni.h>
#include "com_jebora_jebora_ImageEditing.h"
#include <opencv2/opencv.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

using namespace cv;

namespace {
    const int COLOR_MAX = 255;
    const double COLOR_MAX_D = 255.0;
    const int MAT_SIZE_S = 3;
    const int MAT_SIZE_M = 5;
    const double GS_GREEN = 0.59;
    const double GS_RED = 0.3;
    const double GS_BLUE=0.11;

    int getAlpha(int pixel){
        return (pixel >> 24);
    }

    int getRed(int pixel){
        return (pixel >> 16) & 0XFF;
    }

    int getGreen(int pixel){
        return (pixel >> 8) & 0xFF;
    }

    int getBlue(int pixel){
        return pixel & 0XFF;
    }

    int setARGB(int alpha, int red, int green, int blue){
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    int bound(int color){
        if(color > COLOR_MAX) return COLOR_MAX;
        else if(color < 0) return 0;
        else return color;
    }

    void convolute(jint* pixels, int width, int height,
        float * mat, int mat_size, double factor, double offset){

        jint* pixs_cpy = new jint[width * height];
        // memcpy assumes src and dst has data type of
        // unsigned char, therefore need to mulitply by 4
        memcpy(pixs_cpy, pixels, 4*width*height);
        int dist2mid = mat_size / 2;
        int idx, pix, R, G, B, sumR, sumG, sumB;
        float mat_val;

        for(int x=0, w = width - mat_size + 1; x < w; ++x){
            for(int y=0, h = height - mat_size + 1; y < h; ++y){
                idx = (x+1) + (y+1)*width;
                sumR = sumG = sumB = 0;
                for(int mx=0; mx<mat_size; ++mx){
                    for(int my=0; my<mat_size; ++my){
                        pix = pixs_cpy[(x+mx) + (y+my)*width];
                        mat_val = mat[mx + my*mat_size];
                        sumR += (getRed(pix) * mat_val);
                        sumB += (getBlue(pix) * mat_val);
                        sumG += (getGreen(pix) * mat_val);
                    }
                }
                R = bound((int)(sumR/factor + offset));
                G = bound((int)(sumG/factor + offset));
                B = bound((int)(sumB/factor + offset));
                pixels[idx] = setARGB(getAlpha(pixels[idx]), R, G, B);
            }
        }
        delete[] pixs_cpy;
    }
}

JNIEXPORT void JNICALL Java_com_jebora_jebora_ImageEditing_doGreyscale
  (JNIEnv * env, jobject obj, jintArray buf, int w, int h){
    jint *pixels;
    pixels = env->GetIntArrayElements(buf, NULL);
    if(pixels == NULL) return;

    int idx, A, R, G, B;

    for(int x = 0; x<w; ++x){
        for(int y = 0; y < h; ++y){
            idx = y*w + x;
            R = G = B = bound((int)(GS_RED*getRed(pixels[idx]) +
                                    GS_GREEN*getGreen(pixels[idx]) +
                                    GS_BLUE*getBlue(pixels[idx])));
            pixels[idx] = setARGB(getAlpha(pixels[idx]), R, G, B);
        }
    }
    env->ReleaseIntArrayElements(buf, pixels, 0);
}

JNIEXPORT void JNICALL Java_com_jebora_jebora_ImageEditing_doSepia
  (JNIEnv * env, jobject obj, jintArray buf, int w, int h, int depth, double r, double g, double b){
    jint *pixels;
    pixels = env->GetIntArrayElements(buf, NULL);
    if(pixels == NULL) return;

    int idx, R, G, B;

    for(int x = 0; x<w; ++x){
        for(int y = 0; y < h; ++y){
            idx = y*w + x;
            R = G = B = bound((int)(GS_RED*getRed(pixels[idx]) +
                                    GS_GREEN*getGreen(pixels[idx]) +
                                    GS_BLUE*getBlue(pixels[idx])));
            R = bound(R + depth*r);
            G = bound(G + depth*g);
            B = bound(B + depth*b);
            pixels[idx] = setARGB(getAlpha(pixels[idx]), R, G, B);
        }
    }
    env->ReleaseIntArrayElements(buf, pixels, 0);
}

JNIEXPORT void JNICALL Java_com_jebora_jebora_ImageEditing_doContrast
  (JNIEnv * env, jobject obj, jintArray buf, int w, int h, double contrastLvl){
    jint *pixels;
    pixels = env->GetIntArrayElements(buf, NULL);
    if(pixels == NULL) return;

    int idx, A, R, G, B;
    double contrast = pow((100+contrastLvl)/100, 2);
    for(int x = 0; x<w; ++x){
        for(int y = 0; y < h; ++y){
            idx = y*w + x;
            R = bound((int) (((((getRed(pixels[idx]) / COLOR_MAX_D) - 0.5) * contrast)+0.5)*COLOR_MAX_D));
            G = bound((int) (((((getGreen(pixels[idx]) / COLOR_MAX_D) - 0.5) * contrast)+0.5)*COLOR_MAX_D));
            B = bound((int) (((((getBlue(pixels[idx]) / COLOR_MAX_D) - 0.5) * contrast)+0.5)*COLOR_MAX_D));
            pixels[idx] = setARGB(getAlpha(pixels[idx]), R, G, B);
        }
    }
    env->ReleaseIntArrayElements(buf, pixels, 0);
}

JNIEXPORT void JNICALL Java_com_jebora_jebora_ImageEditing_doBrightness
  (JNIEnv * env, jobject obj, jintArray buf, int w, int h, int brightnessLvl){
    jint *pixels;
    pixels = env->GetIntArrayElements(buf, NULL);
    if(pixels == NULL) return;

    int idx, A, R, G, B;

    for(int x = 0; x<w; ++x){
        for(int y = 0; y < h; ++y){
            idx = y*w + x;
            pixels[idx] = setARGB(getAlpha(pixels[idx]),
                                bound(getRed(pixels[idx]) + brightnessLvl),
                                bound(getGreen(pixels[idx]) + brightnessLvl),
                                bound(getBlue(pixels[idx]) + brightnessLvl));

        }
    }
    env->ReleaseIntArrayElements(buf, pixels, 0);
}

JNIEXPORT void JNICALL Java_com_jebora_jebora_ImageEditing_doSnow
  (JNIEnv * env, jobject obj, jintArray buf, int w, int h){
    jint *pixels;
    pixels = env->GetIntArrayElements(buf, NULL);
    if(pixels == NULL) return;

    int idx, A, R, G, B;

    for(int x = 0; x<w; ++x){
        for(int y = 0; y < h; ++y){
            idx = y*w + x;
            int rand_color = setARGB(getAlpha(pixels[idx]),
                                    rand()%COLOR_MAX, rand()%COLOR_MAX, rand()%COLOR_MAX);
            pixels[idx] |= rand_color;
        }
    }
    env->ReleaseIntArrayElements(buf, pixels, 0);
}

JNIEXPORT void JNICALL Java_com_jebora_jebora_ImageEditing_doEmboss
    (JNIEnv * env, jobject obj, jintArray buf, int w, int h){
        jint *pixels;
        pixels = env->GetIntArrayElements(buf, NULL);
        if(pixels == NULL) return;
        float embossConfig[] = {
                -1, 0, -1,
                0, 4, 0,
                -1, 0, -1};
        double embossFactor = 1.0;
        double embossOffset = 127.0;
        convolute(pixels, w, h, embossConfig, MAT_SIZE_S, embossFactor, embossOffset);
        env->ReleaseIntArrayElements(buf, pixels, 0);
}

JNIEXPORT void JNICALL Java_com_jebora_jebora_ImageEditing_doBlur
    (JNIEnv * env, jobject obj, jintArray buf, int w, int h){
        jint *pixels;
        pixels = env->GetIntArrayElements(buf, NULL);
        if(pixels == NULL) return;
        float blurConfig[] = {
                0, 0 , 1, 0, 0,
                0, 1 , 1, 1, 0,
                1, 1 , 1, 1, 1,
                0, 1 , 1, 1, 0,
                0, 0 , 1, 0, 0,};
        double blurFactor = 13.0;
        double blurOffset = 0.0;
        convolute(pixels, w, h, blurConfig, MAT_SIZE_M, blurFactor, blurOffset);
        env->ReleaseIntArrayElements(buf, pixels, 0);
}

JNIEXPORT void JNICALL Java_com_jebora_jebora_ImageEditing_doSharpen
    (JNIEnv * env, jobject obj, jintArray buf, int w, int h, double weight){
        jint *pixels;
        pixels = env->GetIntArrayElements(buf, NULL);
        if(pixels == NULL) return;
        float sharpConfig[] = {
                0, -2, 0,
                -2, weight, -2,
                0, -2, 0};
        double sharpFactor = weight - 8.0;
        double sharpOffset = 0.0;
        convolute(pixels, w, h, sharpConfig, MAT_SIZE_S, sharpFactor, sharpOffset);
        env->ReleaseIntArrayElements(buf, pixels, 0);
}

JNIEXPORT void JNICALL Java_com_jebora_jebora_ImageEditing_doEmbossTwo
    (JNIEnv * env, jobject obj, jintArray buf, int w, int h){
        jint *pixels;
        pixels = env->GetIntArrayElements(buf, NULL);
        if(pixels == NULL) return;
        float eb2Config[] = {
                -2, -1, 0,
                -1, 1, 1,
                0, 1, 2};
        double eb2Factor = 1.0;
        double eb2Offset = 0.0;
        convolute(pixels, w, h, eb2Config, MAT_SIZE_S, eb2Factor, eb2Offset);
        env->ReleaseIntArrayElements(buf, pixels, 0);
}

JNIEXPORT void JNICALL Java_com_jebora_jebora_ImageEditing_doEdgeDetect
    (JNIEnv * env, jobject obj, jintArray buf, int w, int h){
        jint *pixels;
        pixels = env->GetIntArrayElements(buf, NULL);
        if(pixels == NULL) return;
        float EDConfig[] = {
                0, 1, 0,
                1, -4, 1,
                0, 1, 0};
        double EDFactor = 1.0;
        double EDOffset = 0.0;
        convolute(pixels, w, h, EDConfig, MAT_SIZE_S, EDFactor, EDOffset);
        env->ReleaseIntArrayElements(buf, pixels, 0);
}



//    Mat myimg(h, w, CV_8UC4, (unsigned char*)cbuf);
//    for (int y=0 ; y < myimg.rows ; y++){
//        for(int x=0 ; x < myimg.cols ; x++){
//            int factor = (int)(GS_RED * myimg.at<Vec3b>(y,x)[0] +
//                            GS_GREEN * myimg.at<Vec3b>(y,x)[1] +
//                            GS_BLUE * myimg.at<Vec3b>(y,x)[2]);
//            for(int c=0; c < 3; c++){
//                //myimg.at<Vec3b>(y,x)[c] = saturate_cast<uchar> ( alpha*( myimg.at<Vec3b>(y,x)[c] ) + beta);
//                myimg.at<Vec3b>(y,x) = factor;
//            }
//        }
//    }

//    int size=w*h;
//    jintArray result = env->NewIntArray(size);
//    env->SetIntArrayRegion(result, 0, size, cbuf);