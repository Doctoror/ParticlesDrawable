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
package com.doctoror.particlesdrawable.engine;

import android.content.res.Resources;
import android.util.TypedValue;

import com.doctoror.particlesdrawable.model.Scene;

import java.util.Random;

import androidx.annotation.NonNull;

final class ParticleGenerator {

    /**
     * Path calculation padding.
     *
     * @see #applyFreshParticleOffScreen(Scene, int)
     */
    private final float pcc = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 18f, Resources.getSystem().getDisplayMetrics());

    private final Random random = new Random();

    /**
     * Set new point coordinates somewhere on screen and apply new direction
     *
     * @param position the point position to apply new values to
     */
    void applyFreshParticleOnScreen(
            @NonNull final Scene scene,
            final int position
    ) {
        final int w = scene.getWidth();
        final int h = scene.getHeight();
        if (w == 0 || h == 0) {
            throw new IllegalStateException(
                    "Cannot generate particles if scene width or height is 0");
        }

        final double direction = Math.toRadians(random.nextInt(360));
        final float dCos = (float) Math.cos(direction);
        final float dSin = (float) Math.sin(direction);
        final float x = random.nextInt(w);
        final float y = random.nextInt(h);
        final float stepMultiplier = newRandomIndividualParticleStepMultiplier();
        final float radius = newRandomIndividualParticleRadius(scene);

        scene.setParticleData(
                position,
                x,
                y,
                dCos,
                dSin,
                radius,
                stepMultiplier);
    }

    /**
     * Set new particle coordinates somewhere off screen and apply new direction towards the screen
     *
     * @param position the particle position to apply new values to
     */
    void applyFreshParticleOffScreen(
            @NonNull final Scene scene,
            final int position) {
        final int w = scene.getWidth();
        final int h = scene.getHeight();
        if (w == 0 || h == 0) {
            throw new IllegalStateException(
                    "Cannot generate particles if scene width or height is 0");
        }

        float x = random.nextInt(w);
        float y = random.nextInt(h);

        // The offset to make when creating point of out bounds
        final short offset = (short) (scene.getParticleRadiusMin() + scene.getLineDistance());

        // Point angle range
        final float startAngle;
        float endAngle;

        // Make random offset and calulate angles so that the direction of travel will always be
        // towards our View

        switch (random.nextInt(4)) {
            case 0:
                // offset to left
                x = (short) -offset;
                startAngle = angleDeg(pcc, pcc, x, y);
                endAngle = angleDeg(pcc, h - pcc, x, y);
                break;

            case 1:
                // offset to top
                y = (short) -offset;
                startAngle = angleDeg(w - pcc, pcc, x, y);
                endAngle = angleDeg(pcc, pcc, x, y);
                break;

            case 2:
                // offset to right
                x = (short) (w + offset);
                startAngle = angleDeg(w - pcc, h - pcc, x, y);
                endAngle = angleDeg(w - pcc, pcc, x, y);
                break;

            case 3:
                // offset to bottom
                y = (short) (h + offset);
                startAngle = angleDeg(pcc, h - pcc, x, y);
                endAngle = angleDeg(w - pcc, h - pcc, x, y);
                break;

            default:
                throw new IllegalArgumentException("Supplied value out of range");
        }

        if (endAngle < startAngle) {
            endAngle += 360;
        }

        // Get random angle from angle range
        final float randomAngleInRange = startAngle + (random
                .nextInt((int) Math.abs(endAngle - startAngle)));
        final double direction = Math.toRadians(randomAngleInRange);

        final float dCos = (float) Math.cos(direction);
        final float dSin = (float) Math.sin(direction);
        final float stepMultiplier = newRandomIndividualParticleStepMultiplier();
        final float radius = newRandomIndividualParticleRadius(scene);

        scene.setParticleData(
                position,
                x,
                y,
                dCos,
                dSin,
                radius,
                stepMultiplier);
    }

    /**
     * Returns angle in degrees between two points
     *
     * @param ax x of the point 1
     * @param ay y of the point 1
     * @param bx x of the point 2
     * @param by y of the point 2
     * @return angle in degrees between two points
     */
    private static float angleDeg(final float ax, final float ay,
                                  final float bx, final float by) {
        final double angleRad = Math.atan2(ay - by, ax - bx);
        double angle = Math.toDegrees(angleRad);
        if (angleRad < 0) {
            angle += 360;
        }
        return (float) angle;
    }

    /**
     * Generates new step multiplier for individual particle.
     * The value is in [0.5:1.5] range
     *
     * @return new step multiplier for individual particle.
     */
    private float newRandomIndividualParticleStepMultiplier() {
        return 1f + 0.1f * (random.nextInt(11) - 5);
    }

    /**
     * Generates new individual particle radius based on min and max radius setting.
     *
     * @return new particle radius
     */
    private float newRandomIndividualParticleRadius(@NonNull final Scene scene) {
        return scene.getParticleRadiusMin() == scene.getParticleRadiusMax() ?
                scene.getParticleRadiusMin() : scene.getParticleRadiusMin() + (random.nextInt(
                (int) ((scene.getParticleRadiusMax() - scene.getParticleRadiusMin()) * 100f))) / 100f;
    }
}
