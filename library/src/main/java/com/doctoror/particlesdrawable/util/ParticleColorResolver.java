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
package com.doctoror.particlesdrawable.util;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;

public final class ParticleColorResolver {

    private ParticleColorResolver() {

    }

    @ColorInt
    public static int resolveParticleColorWithSceneAlpha(
            @ColorInt final int particleColor,
            @IntRange(from = 0, to = 255) final int sceneAlpha
    ) {
        final int alpha = Color.alpha(particleColor) * sceneAlpha / 255;
        return (particleColor & 0x00FFFFFF) | (alpha << 24);
    }
}
