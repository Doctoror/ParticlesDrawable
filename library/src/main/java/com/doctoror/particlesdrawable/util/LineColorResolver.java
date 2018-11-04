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

import com.doctoror.particlesdrawable.KeepAsApi;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;

@KeepAsApi
public final class LineColorResolver {

    /**
     * Resolves line alpha based on distance comparing to max distance.
     * Where alpha is close to 0 for maxDistance, and close to 1 to 0 distance.
     *
     * @param distance    line length
     * @param maxDistance max line length
     * @return line alpha
     */
    @IntRange(from = 0, to = 255)
    private static int resolveLineAlpha(
            @IntRange(from = 0, to = 255) final int sceneAlpha,
            final float maxDistance,
            final float distance) {
        final float alphaPercent = 1f - distance / maxDistance;
        final int alpha = (int) (255f * alphaPercent);
        return alpha * sceneAlpha / 255;
    }

    @ColorInt
    public static int resolveLineColorWithAlpha(
            @IntRange(from = 0, to = 255) final int sceneAlpha,
            @ColorInt final int lineColor,
            final float maxDistance,
            final float distance) {
        final int alpha = resolveLineAlpha(sceneAlpha, maxDistance, distance);
        return (lineColor & 0x00FFFFFF) | (alpha << 24);
    }
}
