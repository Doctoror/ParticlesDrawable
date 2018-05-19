package com.doctoror.particlesdrawable.util;

public final class DistanceResolver {

    /**
     * Calculates the distance between two points
     *
     * @return distance between two points
     */
    static float distance(final float ax, final float ay,
                                  final float bx, final float by) {
        return (float) Math.sqrt(
                (ax - bx) * (ax - bx) +
                        (ay - by) * (ay - by)
        );
    }
}
