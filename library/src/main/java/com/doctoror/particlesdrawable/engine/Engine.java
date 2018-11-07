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

import android.graphics.drawable.Animatable;

import com.doctoror.particlesdrawable.KeepAsApi;
import com.doctoror.particlesdrawable.contract.SceneController;
import com.doctoror.particlesdrawable.contract.SceneRenderer;
import com.doctoror.particlesdrawable.contract.SceneScheduler;
import com.doctoror.particlesdrawable.model.Scene;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

@KeepAsApi
public final class Engine implements Animatable, Runnable, SceneController {

    private static final float STEP_PER_MS = 0.05f;

    private final FrameAdvancer frameAdvancer;
    private final ParticleGenerator particleGenerator;
    private final Scene scene;
    private final SceneScheduler scheduler;
    private final SceneRenderer renderer;
    private final TimeProvider timeProvider;

    private boolean particlesInited;

    private long lastFrameTime;
    private long lastDrawDuration;

    private volatile boolean animating;

    public Engine(
            @NonNull final Scene scene,
            @NonNull final SceneScheduler scheduler,
            @NonNull final SceneRenderer renderer) {
        this(
                new FrameAdvancer(new ParticleGenerator()),
                new ParticleGenerator(),
                scene,
                scheduler,
                renderer,
                new TimeProvider());
    }

    @VisibleForTesting
    Engine(
            @NonNull final FrameAdvancer frameAdvancer,
            @NonNull final ParticleGenerator particleGenerator,
            @NonNull final Scene scene,
            @NonNull final SceneScheduler scheduler,
            @NonNull final SceneRenderer renderer,
            @NonNull final TimeProvider timeProvider) {
        this.frameAdvancer = frameAdvancer;
        this.scene = scene;
        this.scheduler = scheduler;
        this.renderer = renderer;
        this.particleGenerator = particleGenerator;
        this.timeProvider = timeProvider;
    }

    private void resetLastFrameTime() {
        lastFrameTime = 0L;
    }

    private void gotoNextFrameAndSchedule() {
        nextFrame();
        scheduler.scheduleNextFrame(Math.max(scene.getFrameDelay() - lastDrawDuration, 0L));
    }

    public void setAlpha(final int alpha) {
        scene.setAlpha(alpha);
    }

    public int getAlpha() {
        return scene.getAlpha();
    }

    @Override
    public void start() {
        if (!animating) {
            animating = true;
            resetLastFrameTime();
            gotoNextFrameAndSchedule();
        }
    }

    @Override
    public void stop() {
        if (animating) {
            animating = false;
            resetLastFrameTime();
            scheduler.unscheduleNextFrame();
        }
    }

    @Override
    public boolean isRunning() {
        return animating;
    }

    @Override
    public void run() {
        if (animating) {
            gotoNextFrameAndSchedule();
        } else {
            resetLastFrameTime();
        }
    }

    public void draw() {
        final long startTime = timeProvider.uptimeMillis();
        renderer.drawScene(scene);
        lastDrawDuration = timeProvider.uptimeMillis() - startTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeFreshFrame() {
        final Scene scene = this.scene;
        if (scene.getWidth() != 0 && scene.getHeight() != 0) {
            resetLastFrameTime();
            initParticles();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeFreshFrameWithParticlesOffscreen() {
        final Scene model = scene;
        if (model.getWidth() != 0 && model.getHeight() != 0) {
            resetLastFrameTime();
            initParticlesOffScreen();
        }
    }

    public void setDimensions(final int width, final int height) {
        final Scene scene = this.scene;
        scene.setWidth(width);
        scene.setHeight(height);
        if (width > 0 && height > 0) {
            if (!particlesInited) {
                particlesInited = true;
                initParticles();
            }
        } else {
            if (particlesInited) {
                particlesInited = false;
            }
        }
    }

    private void initParticles() {
        initParticles(new ParticleCreationStrategy() {

            @Override
            public void addNewParticle(final int position) {
                if (position % 2 == 0) {
                    particleGenerator.applyFreshParticleOnScreen(scene, position);
                } else {
                    particleGenerator.applyFreshParticleOffScreen(scene, position);
                }
            }
        });
    }

    private void initParticlesOffScreen() {
        initParticles(new ParticleCreationStrategy() {

            @Override
            public void addNewParticle(final int position) {
                particleGenerator.applyFreshParticleOffScreen(scene, position);
            }
        });
    }

    private void initParticles(@NonNull final ParticleCreationStrategy strategy) {
        final Scene scene = this.scene;
        if (scene.getWidth() == 0 || scene.getHeight() == 0) {
            throw new IllegalStateException("Cannot init particles if width or height is 0");
        }
        for (int i = 0; i < scene.getDensity(); i++) {
            strategy.addNewParticle(i);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextFrame() {
        final float step = lastFrameTime == 0 ? 1f
                : (timeProvider.uptimeMillis() - lastFrameTime) * STEP_PER_MS;
        frameAdvancer.advanceToNextFrame(scene, step);
        lastFrameTime = timeProvider.uptimeMillis();
    }

    private interface ParticleCreationStrategy {

        void addNewParticle(int position);
    }
}
