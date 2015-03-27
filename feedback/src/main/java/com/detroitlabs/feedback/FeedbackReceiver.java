package com.detroitlabs.feedback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.detroitlabs.feedback.core.AttachmentManger;
import com.detroitlabs.feedback.core.Sender;
import com.detroitlabs.feedback.core.Plugin;

import java.util.List;

class FeedbackReceiver extends BroadcastReceiver {

    private final List<Plugin> plugins;
    private final Sender sender;

    public FeedbackReceiver(List<Plugin> plugins, Sender sender){
        this.plugins = plugins;
        this.sender = sender;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AttachmentManger attachmentManager = new AttachmentManger();
        for(Plugin plugin : plugins){
            plugin.execute(context, attachmentManager);
        }
        if(sender != null) {
            sender.send(context, attachmentManager.getAttachments());
        }

    }

}
