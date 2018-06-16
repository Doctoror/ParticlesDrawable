package com.doctoror.particlesdrawable;

import android.opengl.GLSurfaceView;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Multisampling will probably slow down your app -- measure performance carefully and decide if the vastly
 * improved visual quality is worth the cost.
 */
public class MultisampleConfigChooser implements GLSurfaceView.EGLConfigChooser {

    private static final String TAG = "ConfigChooser";

    private final int samples;

    MultisampleConfigChooser(@IntRange(from = 2, to = 4) final int samples) {
        if (samples <= 1 || samples >= 5) {
            throw new IllegalArgumentException("Must be 2 or 4");
        }
        this.samples = samples;
    }

    @Override
    public EGLConfig chooseConfig(@NonNull final EGL10 egl, @NonNull final EGLDisplay display) {
        final int[] target = new int[1];

        int[] configSpec = chooseMultiSamplingConfig(egl, display, target, samples);

        if (configSpec == null || target[0] <= 0) {
            configSpec = chooseCoverageMultiSamplingConfig(egl, display, target);
        }

        if (configSpec == null || target[0] <= 0) {
            configSpec = chooseAnyConfig(egl, display, target);
        }

        if (configSpec == null || target[0] <= 0) {
            throw new IllegalArgumentException("Cannot choose any config");
        }

        final int numConfigs = target[0];

        // Get all matching configurations.
        final EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!egl.eglChooseConfig(display, configSpec, configs, numConfigs, target)) {
            throw new IllegalArgumentException("eglChooseConfig: failed fetching EGLConfig[]");
        }

        return configs[0];
    }

    private static int[] chooseMultiSamplingConfig(
            @NonNull final EGL10 egl,
            @NonNull final EGLDisplay display,
            @NonNull final int[] target,
            final int samples) {
        for (int i = samples; i >= 2; i /= 2) {
            final int[] configSpec = {
                    EGL10.EGL_RED_SIZE, EGL10.EGL_DONT_CARE,
                    EGL10.EGL_GREEN_SIZE, EGL10.EGL_DONT_CARE,
                    EGL10.EGL_BLUE_SIZE, EGL10.EGL_DONT_CARE,
                    EGL10.EGL_DEPTH_SIZE, EGL10.EGL_DONT_CARE,
                    EGL10.EGL_RENDERABLE_TYPE, EGL10.EGL_DONT_CARE,
                    EGL10.EGL_SAMPLE_BUFFERS, 1,
                    EGL10.EGL_SAMPLES, samples,
                    EGL10.EGL_NONE
            };

            if (egl.eglChooseConfig(display, configSpec, null, 0, target)) {
                return configSpec;
            }
        }

        Log.w(TAG, "Multisampling eglChooseConfig failed");
        return null;
    }

    private static int[] chooseCoverageMultiSamplingConfig(
            @NonNull final EGL10 egl,
            @NonNull final EGLDisplay display,
            @NonNull final int[] target) {
        final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
        final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;

        final int[] configSpec = new int[]{
                EGL10.EGL_RED_SIZE, EGL10.EGL_DONT_CARE,
                EGL10.EGL_GREEN_SIZE, EGL10.EGL_DONT_CARE,
                EGL10.EGL_BLUE_SIZE, EGL10.EGL_DONT_CARE,
                EGL10.EGL_DEPTH_SIZE, EGL10.EGL_DONT_CARE,
                EGL10.EGL_RENDERABLE_TYPE, EGL10.EGL_DONT_CARE,
                EGL_COVERAGE_BUFFERS_NV, 1,
                EGL_COVERAGE_SAMPLES_NV, 4,
                EGL10.EGL_NONE
        };

        if (egl.eglChooseConfig(display, configSpec, null, 0, target)) {
            return configSpec;
        }

        Log.w(TAG, "Coverage multisampling eglChooseConfig failed");
        return null;
    }

    private static int[] chooseAnyConfig(
            @NonNull final EGL10 egl,
            @NonNull final EGLDisplay display,
            @NonNull final int[] target) {
        final int[] configSpec = new int[]{
                EGL10.EGL_RED_SIZE, EGL10.EGL_DONT_CARE,
                EGL10.EGL_GREEN_SIZE, EGL10.EGL_DONT_CARE,
                EGL10.EGL_BLUE_SIZE, EGL10.EGL_DONT_CARE,
                EGL10.EGL_DEPTH_SIZE, EGL10.EGL_DONT_CARE,
                EGL10.EGL_RENDERABLE_TYPE, EGL10.EGL_DONT_CARE,
                EGL10.EGL_NONE
        };

        if (egl.eglChooseConfig(display, configSpec, null, 0, target)) {
            return configSpec;
        }

        Log.w(TAG, "Any eglChooseConfig failed");
        return null;
    }
}
