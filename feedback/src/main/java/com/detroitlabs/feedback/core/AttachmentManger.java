package com.detroitlabs.feedback.core;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by andrewgiang on 3/19/15.
 */
public final class AttachmentManger {
    private ArrayList<Uri> attachments = new ArrayList<>();

    public ArrayList<Uri> getAttachments() {
        return attachments;
    }

    public void add(File file){
        if(file != null) {
            attachments.add(Uri.fromFile(file));
        }
    }


}
