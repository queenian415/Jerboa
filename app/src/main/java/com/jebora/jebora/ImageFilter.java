package com.jebora.jebora;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;


/**
 * Created by mshzhb on 15/6/13.
 */
public class ImageFilter {
    public static Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap, int type) {
        if (firstBitmap == null || secondBitmap == null)
            return null;


        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(),
                firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Matrix m =new Matrix();

        canvas.drawBitmap(firstBitmap, m, null);


        switch (type) {
            case 0: //T-shirt
                //secondBitmap = RotateBitmap(secondBitmap,280);
                canvas.drawBitmap(secondBitmap, 690, 600, null); break;
            default:
        }
        return bitmap;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
