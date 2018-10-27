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
package com.doctoror.particlesdrawable.opengl.chooser;

import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

/**
 * Iterates from config choosers and chooses first config in order that did not throw a
 * {@link RuntimeException} and returned a non-null {@link EGLConfig}.
 * <p>
 * Throws {@link IllegalArgumentException} when all configs threw.
 */
class CompositeConfigChooser implements EGLConfigChooser {

    private static final String TAG = "CompositeConfigChooser";

    @VisibleForTesting
    static boolean log = true;

    @NonNull
    @VisibleForTesting
    final EGLConfigChooser[] choosers;

    CompositeConfigChooser(@NonNull final EGLConfigChooser... choosers) {
        if (choosers == null || choosers.length == 0) {
            throw new IllegalArgumentException("Must pass at least one chooser");
        }
        this.choosers = choosers;
    }

    @Override
    public final EGLConfig chooseConfig(@NonNull final EGL10 egl, final EGLDisplay display) {
        for (final EGLConfigChooser chooser : choosers) {
            try {
                final EGLConfig config = chooser.chooseConfig(egl, display);
                if (config == null) {
                    throw new IllegalArgumentException("Returned config is null");
                }
                return config;
            } catch (@NonNull final RuntimeException e) {
                if (log) {
                    Log.w(TAG, "Chooser failed", e);
                }
            }
        }

        throw new IllegalArgumentException("All config choosers failed");
    }
}
