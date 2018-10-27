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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Chooses a multisampling config and falling back to less samples or no multisampling at all.
 * Tries configs in the following order:
 * <p>
 * <p>
 * - RGB 888 with exact multisampling value;<p>
 * - RGB 888 with multisampling value divided by 2, until reaches 2;<p>
 * - RGB 888 with exact coverage multisampling value;<p>
 * - RGB 888 no multisampling;<p>
 * - any config.<p>
 * <p>
 * If no config choosen, throws {@link IllegalArgumentException}.
 */
final class FailsafeMultisamplingConfigChooser extends CompositeConfigChooser {

    FailsafeMultisamplingConfigChooser(
            final int samples,
            @Nullable final EGLConfigChooserCallback callback) {
        super(makeChoosers(samples, callback));
    }

    @NonNull
    private static EGLConfigChooser[] makeChoosers(
            final int samples,
            @Nullable final EGLConfigChooserCallback callback
    ) {
        final int fixedConfigChoosersCount = 3;
        final int dynamicConfigChoosersCount = (int) Math.ceil(Math.log(samples) / Math.log(2));

        final EGLConfigChooser[] choosers = new EGLConfigChooser[
                dynamicConfigChoosersCount + fixedConfigChoosersCount];

        int chooserPosition = 0;
        for (int i = samples; i >= 2; i /= 2) {
            choosers[chooserPosition++] = new MultisamplingConfigChooser(i, callback);
        }
        choosers[chooserPosition++] = new CoverageMultisamplingConfigChooser(samples, callback);
        choosers[chooserPosition++] = new RGB888ConfigChooser(callback);
        choosers[chooserPosition] = new AnyConfigChooser(callback);
        return choosers;
    }
}
