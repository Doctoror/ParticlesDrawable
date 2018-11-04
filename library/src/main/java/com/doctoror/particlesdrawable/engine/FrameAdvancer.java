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

import com.doctoror.particlesdrawable.model.Scene;

import androidx.annotation.NonNull;

final class FrameAdvancer {

    @NonNull
    private final ParticleGenerator particleGenerator;

    FrameAdvancer(@NonNull final ParticleGenerator particleGenerator) {
        this.particleGenerator = particleGenerator;
    }

    void advanceToNextFrame(
            @NonNull final Scene scene,
            final float step
    ) {
        final int particlesCount = scene.getNumDots();
        for (int i = 0; i < particlesCount; i++) {
            float x = scene.getParticleX(i);
            float y = scene.getParticleY(i);

            final float stepMultiplier = scene.getParticleStepMultiplier(i);
            final float dCos = scene.getParticleDirectionCos(i);
            final float dSin = scene.getParticleDirectionSin(i);

            x += step * scene.getStepMultiplier() * stepMultiplier * dCos;
            y += step * scene.getStepMultiplier() * stepMultiplier * dSin;

            if (pointOutOfBounds(scene, x, y)) {
                particleGenerator.applyFreshParticleOffScreen(scene, i);
            } else {
                scene.setParticleX(i, x);
                scene.setParticleY(i, y);
            }
        }
    }

    /**
     * Used for checking if the point is off-screen and farther than line distance
     *
     * @param x the point x
     * @param y the point y
     * @return true if the point is off-screen and guaranteed not to be used to draw a line to the
     * closest point on-screen
     */
    private boolean pointOutOfBounds(
            @NonNull final Scene scene,
            final float x,
            final float y) {
        final float offset = scene.getMinDotRadius() + scene.getLineDistance();
        return x + offset < 0 || x - offset > scene.getWidth()
                || y + offset < 0 || y - offset > scene.getHeight();
    }
}
