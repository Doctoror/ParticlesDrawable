package com.doctoror.particlesdrawable.util;

import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;

public final class LineColorResolver {

    /**
     * Resolves line alpha based on distance comparing to max distance.
     * Where alpha is close to 0 for maxDistance, and close to 1 to 0 distance.
     *
     * @param distance    line distance
     * @param maxDistance max line distance
     * @return line alpha
     */
    @IntRange(from = 0, to = 255)
    static int resolveLineAlpha(
            @IntRange(from = 0, to = 255)
            final int sceneAlpha,
            final float maxDistance,
            final float distance) {
        final float alphaPercent = 1f - distance / maxDistance;
        final int alpha = (int) (255f * alphaPercent);
        return alpha * sceneAlpha / 255;
    }

    @ColorInt
    static int resolveLineColorWithAlpha(
            @IntRange(from = 0, to = 255)
            final int sceneAlpha,
            @ColorInt final int lineColor,
            final float maxDistance,
            final float distance) {
        final int alpha = resolveLineAlpha(sceneAlpha, maxDistance, distance);
        return (lineColor & 0x00FFFFFF) | (alpha << 24);
    }
}
