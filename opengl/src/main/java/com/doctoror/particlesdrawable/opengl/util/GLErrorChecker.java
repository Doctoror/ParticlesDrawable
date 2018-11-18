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

import android.opengl.GLES20;
import android.util.Log;

import com.doctoror.particlesdrawable.KeepAsApi;
import com.doctoror.particlesdrawable.opengl.GlException;

import androidx.annotation.NonNull;

@KeepAsApi
public final class GLErrorChecker {

    private static boolean shouldThrowOnGlError = true;
    private static boolean shouldCheckGlError = false;

    public static void setShouldCheckGlError(final boolean shouldCheckGlError) {
        GLErrorChecker.shouldCheckGlError = shouldCheckGlError;
    }

    public static void setShouldThrowOnGlError(final boolean shouldThrowOnGlError) {
        GLErrorChecker.shouldThrowOnGlError = shouldThrowOnGlError;
    }

    public static void checkGlError(@NonNull final String tag) {
        if (shouldCheckGlError) {
            final int error = GLES20.glGetError();
            if (error != GLES20.GL_NO_ERROR) {
                if (shouldThrowOnGlError) {
                    throw new GlException(error, tag);
                } else {
                    Log.e("GLErrorChecker", "GLError" + Integer.toString(error) + ", tag = " + tag);
                }
            }
        }
    }
}
