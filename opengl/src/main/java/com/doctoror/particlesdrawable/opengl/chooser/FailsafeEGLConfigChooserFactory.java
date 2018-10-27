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

import com.doctoror.particlesdrawable.KeepAsApi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@KeepAsApi
public final class FailsafeEGLConfigChooserFactory {

    private FailsafeEGLConfigChooserFactory() {

    }

    /**
     * Creates an {@link EGLConfigChooser} for given multisampling value.
     * Will fallback to lower multisampling mode or no multisampling mode at all if unsupported.
     *
     * @param samples  multisampling mode. Must be a positive number and a power of two. May be 0 for
     *                 no multisampling.
     * @param callback the {@link EGLConfigChooserCallback} to notify with the chosen config
     * @return new {@link EGLConfigChooser}  for the given parameters.
     */
    @NonNull
    public static EGLConfigChooser newFailsafeEGLConfigChooser(
            final int samples,
            @Nullable final EGLConfigChooserCallback callback) {
        if (samples < 0) {
            throw new IllegalArgumentException("samples must not be negative");
        }

        if (samples == 0) {
            return new FailsafeRGB888ConfigChooser(callback);
        } else {
            return new FailsafeMultisamplingConfigChooser(samples, callback);
        }
    }
}
