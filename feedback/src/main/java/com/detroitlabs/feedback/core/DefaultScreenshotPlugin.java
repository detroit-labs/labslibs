package com.detroitlabs.feedback.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.detroitlabs.feedback.util.FileHelper;


/**
 * Created by andrewgiang on 3/18/15.
 */
public class DefaultScreenshotPlugin implements Plugin {

    @Override
    public void execute(Context context, AttachmentManger manager) {
        manager.add(FileHelper.bitmapToFile(context, "screenshot.jpg", getScreenshot(context)));
    }

    private Bitmap getScreenshot(Context context) {
        if (context instanceof Activity) {
            final View rootView = ((Activity) context).getWindow().getDecorView().getRootView();
            return getScreenBitmap(rootView);
        }
        throw new IllegalArgumentException("Context must be an instance of an Activity");
    }

    private Bitmap getScreenBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
