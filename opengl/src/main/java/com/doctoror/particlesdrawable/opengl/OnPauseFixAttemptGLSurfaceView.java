package com.doctoror.particlesdrawable.opengl;

import android.content.Context;
import android.util.AttributeSet;

import com.doctoror.particlesdrawable.KeepAsApi;

/**
 * Source: <a href="https://issuetracker.google.com/issues/36909769#comment4">issuetracker.google.com/issues/36909769#comment4</a>
 */
@KeepAsApi
public class OnPauseFixAttemptGLSurfaceView extends MultipleInstanceSafeGLSurfaceView {

    private final NotifyPause notifyPause = new NotifyPause();
    final boolean[] pausing = new boolean[1];

    public OnPauseFixAttemptGLSurfaceView(Context context) {
        super(context);
    }

    public OnPauseFixAttemptGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onPause() {
        synchronized (pausing) {
            pausing[0] = true;
            queueEvent(notifyPause);
            try {
                while (pausing[0]) {
                    pausing.wait();
                }
            } catch (final java.lang.InterruptedException e) {
                // empty
            }
            super.onPause();
        }
    }

    private final class NotifyPause implements Runnable {

        @Override
        public void run() {
            synchronized (pausing) {
                pausing[0] = false;
                pausing.notifyAll();
            }
        }
    }
}
