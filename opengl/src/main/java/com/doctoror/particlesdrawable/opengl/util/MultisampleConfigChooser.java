/*
 * Copyright (C) 2018 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doctoror.particlesdrawable.opengl.util;

import android.opengl.GLSurfaceView;
import android.util.Log;

import com.doctoror.particlesdrawable.ConfigurableKeep;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Multisampling will probably slow down your app -- measure performance carefully and decide if the vastly
 * improved visual quality is worth the cost.
 */
@ConfigurableKeep
public class MultisampleConfigChooser implements GLSurfaceView.EGLConfigChooser {

    private static final String TAG = "ConfigChooser";

    @Nullable
    private final Callback callback;

    private final int samples;

    public MultisampleConfigChooser(@IntRange(from = 2, to = 4) final int samples) {
        this(samples, null);
    }

    public MultisampleConfigChooser(
            @IntRange(from = 2, to = 4) final int samples,
            @Nullable final Callback callback) {
        if (samples <= 1 || samples >= 5) {
            throw new IllegalArgumentException("Number of samples must be 2 or 4");
        }
        this.samples = samples;
        this.callback = callback;
    }

    @NonNull
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

    @Nullable
    private int[] chooseMultiSamplingConfig(
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
                if (callback != null) {
                    callback.onConfigChosen(i);
                }
                return configSpec;
            }
        }

        Log.w(TAG, "Multisampling eglChooseConfig failed");
        return null;
    }

    @Nullable
    private int[] chooseCoverageMultiSamplingConfig(
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
            if (callback != null) {
                callback.onConfigChosen(4);
            }
            return configSpec;
        }

        Log.w(TAG, "Coverage multisampling eglChooseConfig failed");
        return null;
    }

    @Nullable
    private int[] chooseAnyConfig(
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
            if (callback != null) {
                callback.onConfigChosen(0);
            }
            return configSpec;
        }

        Log.w(TAG, "Any eglChooseConfig failed");
        return null;
    }

    @ConfigurableKeep
    public interface Callback {

        /**
         * Notifies that the config has been chosen.
         *
         * @param samples the chosen EGL_SAMPLES or 0 if not a multisampling config was chosen.
         */
        void onConfigChosen(int samples);
    }
}
