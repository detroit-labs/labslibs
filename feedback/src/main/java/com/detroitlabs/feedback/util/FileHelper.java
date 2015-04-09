package com.detroitlabs.feedback.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by andrewgiang on 3/19/15.
 */
public class FileHelper {

    /**
     * Store a bitmap as a JPEG in the Pictures directory of the application.
     *
     * @param context used to get the external file directory
     * @param filename of the jpeg being saved
     * @param bitmap the actual bitmap that will be saved to file
     * @return {@link File} of the saved jpeg or null if IOException is thrown while saving the bitmap.
     */
    @Nullable
    public static File bitmapToFile(Context context, String filename, Bitmap bitmap) {
        try {
            File returnFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);
            final FileOutputStream fileOutputStream = new FileOutputStream(returnFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return returnFile;
        } catch (IOException e) {
            return null;
        }
    }
}
