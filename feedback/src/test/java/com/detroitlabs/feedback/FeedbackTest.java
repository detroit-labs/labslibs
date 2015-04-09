package com.detroitlabs.feedback;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 18)

public class FeedbackTest {
    @Test(expected = IllegalStateException.class)
    public void feedbackCannotBeInitializedTwice() throws Exception {
        final Feedback.FeedbackInitializer feedbackInitializer = Feedback.newInitializer(RuntimeEnvironment.application);
        Feedback.initialize(feedbackInitializer);
        Feedback.initialize(feedbackInitializer);
    }

}