package edu.illinois.financemanager.object;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class OCRScanner {

    private static final String TAG = "OCR";

    /**
     * Static method for finding the total amount on a receipt.
     *
     * @param context      The context
     * @param picFile      The image file
     * @param bitmapOption Normally we use 4 here (must be power of 2). If set to a value > 1, requests the decoder to subsample the original image, returning a smaller image to save memory.
     * @return The total amount, -1 means no total amount was found
     */
    public static float Scan(Context context, File picFile, int bitmapOption) {
        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir != null) {
            final String DATA_PATH = externalFilesDir.toString();
            final String lang = "eng";

            File mFileTrain = new File(DATA_PATH + "/tessdata", lang + ".traineddata");

            String[] paths = new String[]{DATA_PATH, DATA_PATH + "/tessdata"};

            for (String path : paths) {
                File dir = new File(path);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        return -1;
                    }
                }
            }

            if (!mFileTrain.exists()) {
                try {
                    AssetManager assetManager = context.getAssets();
                    InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                    OutputStream out = new FileOutputStream(mFileTrain);

                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    Log.v(TAG, "Copied " + lang + " traineddata");
                } catch (IOException e) {
                    Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
                }
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = bitmapOption;

            Bitmap bitmap = BitmapFactory.decodeFile(picFile.toString(), options);
            try {
                ExifInterface exif = new ExifInterface(picFile.toString());
                int exifOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                Log.v(TAG, "Orient: " + exifOrientation);

                int rotate = 0;

                switch (exifOrientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                }

                Log.v(TAG, "Rotation: " + rotate);

                if (rotate != 0) {

                    // Getting width & height of the given image.
                    int w = bitmap.getWidth();
                    int h = bitmap.getHeight();

                    // Setting pre rotate
                    Matrix mtx = new Matrix();
                    mtx.preRotate(rotate);

                    // Rotating Bitmap
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
                }

                // Convert to ARGB_8888, required by tess
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            } catch (IOException e) {
                Log.e(TAG, "Couldn't correct orientation: " + e.toString());
            }

            TessBaseAPI baseApi = new TessBaseAPI();
            baseApi.setDebug(true);
            baseApi.init(DATA_PATH, lang);
            baseApi.setImage(bitmap);
            String recognizedText = baseApi.getUTF8Text();
            baseApi.end();

            int totalStart = -5, start, end;
            String result;
            recognizedText = recognizedText.toLowerCase() + "\n";

            do {
                start = totalStart;
                totalStart = recognizedText.indexOf("tot", totalStart + 5);
            } while (totalStart > 0);

            if (start < 0) {
                Log.d(TAG, "no total");
                return -1;
            } else {
                end = recognizedText.indexOf("\n", start);
                result = recognizedText.substring(start, end);
                Log.d(TAG, "OCR Result: " + result);
                result = result.replaceAll("[a-zA-Z]", "");
                result = result.replaceAll(" ", "");
                result = result.replaceAll("[^0-9]", ".");
                Log.d(TAG, result);

                String newResult = "";
                boolean flag = true;
                int decIndex = 0;
                for (int i = 0; i < result.length(); i++) {
                    if (i > decIndex + 2 && !flag) {
                        break;
                    }
                    String temp = result.substring(i, i + 1);
                    if (newResult.length() == 0) {
                        if (!temp.equals("."))
                            newResult += temp;
                    } else {
                        if (temp.equals(".")) {
                            if (flag) {
                                decIndex = i;
                                newResult += temp;
                                flag = false;
                            }
                        } else {
                            newResult += temp;
                        }
                    }
                }

                return Float.parseFloat(newResult);
            }
        } else {
            return -1F;
        }
    }
}
