package com.detroitlabs.feedback.core;

import android.content.Context;

/**
 * To create a custom Plugin clients must implement this interface and register them during initialization.
 * <p/>
 * <pre>
 * <code>
 * Feedback.initialize(
 *      newInitializer(this).
 *      addPlugin(new MyCustomPlugin()).
 *      setSender(new DefaultSender("andrew.giang@detroitlabs.com", "Feedback"))
 * );
 * </code>
 * </pre>
 */
public interface Plugin {

    /**
     * The AttachmentManger provides a hook into the library to attach files that will be
     * used by the {@link Sender}.
     */
    void execute(Context context, AttachmentManger manger);

}
