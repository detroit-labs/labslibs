package com.detroitlabs.feedback.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Starts an E-mail intent that attaches the files provided by the {@link Plugin}
 */
public class DefaultSender implements Sender {

    public static final String TYPE_TEXT_PLAIN = "text/plain";

    private final String emailTo;
    private final String subject;

    public DefaultSender(String emailTo, String subject) {
        this.emailTo = emailTo;
        this.subject = subject;
    }

    @Override
    public void send(Context context, ArrayList<Uri> attachments) {
        context.startActivity(Intent.createChooser(getFeedbackEmailIntent(attachments, context), "Choose an email provider:"));
    }

    private Intent getFeedbackEmailIntent(ArrayList<Uri> attachments, Context context) {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType(TYPE_TEXT_PLAIN);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachments);
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            emailIntent.putExtra(Intent.EXTRA_TEXT, getAppInfo(pInfo.versionCode, pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return emailIntent;
    }



    public static String getAppInfo(int versionCode, String versionName) {
        //noinspection StringBufferReplaceableByString
        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append("\n\n\n");
        messageBuilder.append("---------------------\n");

        messageBuilder.append("App Version: ");
        messageBuilder.append(versionName);
        messageBuilder.append(" - ");
        messageBuilder.append(versionCode);
        messageBuilder.append("\n");

        messageBuilder.append("Android OS Version: ");
        messageBuilder.append(Build.VERSION.RELEASE);
        messageBuilder.append(" - ");
        messageBuilder.append(Build.VERSION.SDK_INT);

        messageBuilder.append("\n");
        messageBuilder.append("Date: ");

        messageBuilder.append(SimpleDateFormat.getDateInstance().format(new Date()));
        return messageBuilder.toString();
    }
}
