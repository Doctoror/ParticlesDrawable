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

import androidx.annotation.Nullable;

/**
 * First chooses RGB 888 config. If failed fallbacks to any config.
 * <p>
 * If no config choosen, throws {@link IllegalArgumentException}.
 */
final class FailsafeRGB888ConfigChooser extends CompositeConfigChooser {

    FailsafeRGB888ConfigChooser(@Nullable final EGLConfigChooserCallback callback) {
        super(new RGB888ConfigChooser(callback), new AnyConfigChooser(callback));
    }
}
