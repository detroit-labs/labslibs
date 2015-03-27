package com.detroitlabs.mapmodule;

import android.view.View;
import android.view.ViewGroup;

public class LayoutTraverser {
    public interface Processor {
        boolean process(View view);
    }

    private final Processor processor;

    private LayoutTraverser(Processor processor) {
        this.processor = processor;
    }

    public static LayoutTraverser build(Processor processor) {
        return new LayoutTraverser(processor);
    }

    public void traverse(ViewGroup root) {
        final int childCount = root.getChildCount();

        for (int i = 0; i < childCount; ++i) {
            final View child = root.getChildAt(i);
            if(processor.process(child)) {
                if (child instanceof ViewGroup) {
                    traverse((ViewGroup) child);
                }
            }else {
                break;
            }
        }
    }

}