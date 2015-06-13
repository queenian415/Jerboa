package com.jebora.jebora;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by Tiffanie on 15-06-12.
 */
public class ImageProcess {

    public static Bitmap createGrayScale(Bitmap src) {
        // sepia-toning effect with no intensity on any channel
        // will result in black-and-white
        return createSepiaToningEffect(src, 0, 0, 0, 0);
    }

    public static Bitmap createSepiaToningEffect(Bitmap src, int depth, double red, double green, double blue) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        // constant grayscale, will result in black-and-white
        final double GS_RED = 0.3;
        final double GS_GREEN = 0.59;
        final double GS_BLUE = 0.11;
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // get pixel color
                pixel = src.getPixel(x, y);
                // get color on each channel
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // apply grayscale sample
                B = G = R = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);

                // apply intensity level on each channel
                R += (depth * red);
                if (R > 255) { R = 255; }

                G += (depth * green);
                if (G > 255) { G = 255; }

                B += (depth * blue);
                if (B > 255) { B = 255; }

                // set new pixel to output image
                result.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return result;
    }
}
