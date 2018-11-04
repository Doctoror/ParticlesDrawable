package com.doctoror.particlesdrawable.engine;

import com.doctoror.particlesdrawable.model.Scene;

import androidx.annotation.NonNull;

final class FrameAdvancer {

    @NonNull
    private final ParticleGenerator offscreenParticleGenerator;

    FrameAdvancer(@NonNull final ParticleGenerator offscreenParticleGenerator) {
        this.offscreenParticleGenerator = offscreenParticleGenerator;
    }

    public void advanceToNextFrame(
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
                offscreenParticleGenerator.applyFreshParticleOffScreen(scene, i);
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
