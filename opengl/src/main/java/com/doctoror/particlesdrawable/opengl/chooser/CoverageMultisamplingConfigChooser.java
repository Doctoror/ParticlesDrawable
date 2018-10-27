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

import javax.microedition.khronos.egl.EGL10;

import androidx.annotation.Nullable;

final class CoverageMultisamplingConfigChooser extends BaseConfigChooser {

    private static final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
    private static final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;

    CoverageMultisamplingConfigChooser(
            final int samples,
            @Nullable final EGLConfigChooserCallback callback) {
        super("CoverageMultisamplingConfigChooser", new int[]{
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL_COVERAGE_BUFFERS_NV, 1,
                EGL_COVERAGE_SAMPLES_NV, samples,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL10.EGL_NONE
        }, callback);
    }
}
