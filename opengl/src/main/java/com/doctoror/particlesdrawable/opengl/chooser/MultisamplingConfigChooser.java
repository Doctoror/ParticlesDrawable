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

import com.doctoror.particlesdrawable.opengl.util.PotCalculator;

import javax.microedition.khronos.egl.EGL10;

import androidx.annotation.Nullable;

final class MultisamplingConfigChooser extends BaseConfigChooser {

    MultisamplingConfigChooser(
            final int samples,
            @Nullable final EGLConfigChooserCallback callback) {
        super("MultisamplingConfigChooser", makeConfigSpec(samples), callback);
    }

    private static int[] makeConfigSpec(final int samples) {
        if (samples <= 1) {
            throw new IllegalArgumentException("samples must be > 1");
        }

        if (!PotCalculator.isPowerOfTwo(samples)) {
            throw new IllegalArgumentException("samples must be a power of two");
        }

        return new int[]{
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_SAMPLE_BUFFERS, 1,
                EGL10.EGL_SAMPLES, samples,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL10.EGL_NONE
        };
    }
}
