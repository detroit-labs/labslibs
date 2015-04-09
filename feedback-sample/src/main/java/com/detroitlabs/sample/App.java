package com.detroitlabs.sample;

import android.app.Application;

import com.detroitlabs.feedback.Feedback;
import com.detroitlabs.feedback.core.DefaultSender;
import com.detroitlabs.feedback.core.DefaultScreenshotPlugin;

import static com.detroitlabs.feedback.Feedback.newInitializer;

/**
 * Created by andrewgiang on 2/9/15.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Feedback.initialize(
                newInitializer(this).
                        addPlugin(new DefaultScreenshotPlugin()).
                        setSender(new DefaultSender("andrew.giang@detroitlabs.com", "Feedback"))
        );
    }
}
