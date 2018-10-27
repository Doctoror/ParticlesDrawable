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

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class BaseConfigChooser implements GLSurfaceView.EGLConfigChooser {

    @NonNull
    private final String tag;

    @Nullable
    private final int[] configSpec;

    @Nullable
    private final EGLConfigChooserCallback callback;

    BaseConfigChooser(
            @NonNull final String tag,
            @Nullable final int[] configSpec,
            @Nullable final EGLConfigChooserCallback callback) {
        this.tag = tag;
        this.configSpec = configSpec;
        this.callback = callback;
    }

    @Nullable
    private int[] getConfigSpecCopy() {
        if (configSpec == null) {
            return null;
        }

        final int[] copy = new int[configSpec.length];
        System.arraycopy(configSpec, 0, copy, 0, copy.length);
        return copy;
    }

    @Override
    public final EGLConfig chooseConfig(@NonNull final EGL10 egl, final EGLDisplay display) {
        final int[] target = new int[1];

        final boolean result = egl.eglChooseConfig(
                display, configSpec, null, 0, target);

        if (!result || target[0] <= 0) {
            throw new IllegalArgumentException(tag + ": no config chosen");
        }

        final EGLConfig[] configs = new EGLConfig[1];
        if (!egl.eglChooseConfig(display, configSpec, configs, 1, target)
                || configs[0] == null) {
            throw new IllegalArgumentException(tag + ": failed fetching EGLConfig[]");
        }

        if (callback != null) {
            callback.onConfigChosen(getConfigSpecCopy());
        }

        return configs[0];
    }
}
