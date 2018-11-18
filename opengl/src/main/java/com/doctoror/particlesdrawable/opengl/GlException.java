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
package com.doctoror.particlesdrawable.opengl;

import com.doctoror.particlesdrawable.KeepAsApi;

import androidx.annotation.NonNull;

@KeepAsApi
public final class GlException extends RuntimeException {

    private final int glError;

    /**
     * @param glError the error read from glGetError.
     * @param tag     custom tag to include in exception message.
     */
    public GlException(final int glError, @NonNull final String tag) {
        super("GLError: " + glError + ", tag: " + tag);
        this.glError = glError;
    }

    /**
     * @return the error read from glGetError.
     */
    @SuppressWarnings("unused")
    public int getGlError() {
        return glError;
    }
}
