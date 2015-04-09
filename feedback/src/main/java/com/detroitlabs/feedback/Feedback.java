package com.detroitlabs.feedback;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.DrawableRes;

import com.detroitlabs.feedback.core.Sender;
import com.detroitlabs.feedback.core.Plugin;
import com.detroitlabs.feedback.core.SimpleActivityLifeCycle;

import java.util.ArrayList;


public class Feedback {

    private static Feedback instance;

    protected static final String ACTION_FEEDBACK = "ACTION_FEEDBACK";

    private final boolean showFeedbackNotification;
    private final BroadcastReceiver broadcastReceiver;
    private final IntentFilter feedbackIntentFilter = new IntentFilter(ACTION_FEEDBACK);
    private final NotificationController notificationController;
    private final Application.ActivityLifecycleCallbacks activityLifecycleCallbacks= new SimpleActivityLifeCycle(){
        @Override
        public void onActivityResumed(Activity activity) {
            if(showFeedbackNotification) {
                notificationController.startNotification(activity);
            }
            activity.registerReceiver(broadcastReceiver, feedbackIntentFilter);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            activity.unregisterReceiver(broadcastReceiver);
            if(showFeedbackNotification) {
                notificationController.stopNotification();
            }
        }
    };

    private Feedback(FeedbackInitializer initializer){
        this.showFeedbackNotification = initializer.showNotification;
        this.notificationController = new NotificationController(initializer.application, initializer.notificationTitle, initializer.notificationContentText, initializer.notificationIcon);
        this.broadcastReceiver = new FeedbackReceiver(initializer.plugins, initializer.sender);
        initializer.application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    /**
     * Can only be called after activity has been resumed.
     */
    public static void sendFeedbackNow(Activity activity){
        Intent intent = new Intent(ACTION_FEEDBACK);
        activity.sendBroadcast(intent);
    }
    public static void initialize(FeedbackInitializer builder){
        if(instance == null) {
            instance = builder.build();
        }else{
            throw new IllegalStateException("initialize should only be called once");
        }
    }

    public static FeedbackInitializer newInitializer(Application application){
        return new FeedbackInitializer(application);
    }

    public static class FeedbackInitializer {

        private final Application application;
        private final ArrayList<Plugin> plugins;

        private String notificationTitle = "Feedback Reporter";
        private String notificationContentText = "Click to send feedback";
        private int notificationIcon = R.drawable.ic_bug_report;
        private Sender sender;
        private boolean showNotification;

        public FeedbackInitializer(Application application){
            this.application = application;
            plugins = new ArrayList<>();
        }

        public FeedbackInitializer addPlugin(Plugin plugin){
            plugins.add(plugin);
            return this;
        }

        public FeedbackInitializer showNotification(boolean showNotification){
            this.showNotification = showNotification;
            return this;
        }
        public FeedbackInitializer setNotificationTitle(String title){
            this.notificationTitle = title;
            return this;
        }

        public FeedbackInitializer setNotificationContentText(String notificationContentText){
            this.notificationContentText = notificationContentText;
            return this;
        }

        public FeedbackInitializer setNotificationIcon(@DrawableRes int notificationIcon){
            this.notificationIcon = notificationIcon;
            return this;
        }

        public FeedbackInitializer setSender(Sender sender){
            this.sender = sender;
            return this;
        }
        public Feedback build(){
            return new Feedback(this);
        }
    }

}
