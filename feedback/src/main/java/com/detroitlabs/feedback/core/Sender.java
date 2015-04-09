package com.detroitlabs.feedback.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;

public interface Sender {

    /**
     * Handles sending the attached files from the Plugins.
     *
     * @param context the context from {@link com.detroitlabs.feedback.FeedbackReceiver#onReceive(Context, Intent)}
     * @param attachments a List of files that the Plugins have provided
     */
    void send(Context context, ArrayList<Uri> attachments);
}
