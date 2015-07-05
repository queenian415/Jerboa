#include <jni.h>
#include "com_jebora_jebora_ImageEditing.h"
#include <opencv2/opencv.hpp>
#include <stdio.h>
#include <stdlib.h>

using namespace cv;


JNIEXPORT jintArray JNICALL Java_com_jebora_jebora_ImageEditing_hello
  (JNIEnv * env, jobject obj, jintArray buf, int w, int h){
    jint *cbuf;
            cbuf = env->GetIntArrayElements(buf, false);
            if(cbuf == NULL)
            {
                return 0;
            }
            Mat myimg(h, w, CV_8UC4, (unsigned char*)cbuf);
            for(int j=0; j<myimg.rows/2; j++)
            {
                myimg.row(j).setTo(Scalar(0, 0, 0, 0));
            }
            int size=w*h;
            jintArray result = env->NewIntArray(size);
            env->SetIntArrayRegion(result, 0, size, cbuf);
            env->ReleaseIntArrayElements(buf, cbuf, 0);
            return result;
  }