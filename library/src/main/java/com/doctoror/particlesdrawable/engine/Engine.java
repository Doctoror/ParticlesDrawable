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
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;

import com.doctoror.particlesdrawable.Defaults;
import com.doctoror.particlesdrawable.KeepAsApi;
import com.doctoror.particlesdrawable.model.Scene;
import com.doctoror.particlesdrawable.R;
import com.doctoror.particlesdrawable.contract.SceneController;
import com.doctoror.particlesdrawable.contract.SceneRenderer;
import com.doctoror.particlesdrawable.contract.SceneScheduler;

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

    public void inflate(@NonNull final Resources r, @NonNull final AttributeSet attrs) {
        final TypedArray a = r.obtainAttributes(attrs, R.styleable.ParticlesView);
        try {
            handleAttrs(a);
        } finally {
            a.recycle();
        }
    }

    public void handleAttrs(@NonNull final TypedArray a) {
        final int count = a.getIndexCount();
        float minDotRadius = Defaults.PARTICLE_RADIUS_MIN;
        float maxDotRadius = Defaults.PARTICLE_RADIUS_MAX;
        for (int i = 0; i < count; i++) {
            final int attr = a.getIndex(i);
            if (attr == R.styleable.ParticlesView_minDotRadius) {
                minDotRadius = a.getDimension(attr, Defaults.PARTICLE_RADIUS_MIN);

            } else if (attr == R.styleable.ParticlesView_maxDotRadius) {
                maxDotRadius = a.getDimension(attr, Defaults.PARTICLE_RADIUS_MAX);

            } else if (attr == R.styleable.ParticlesView_lineThickness) {
                scene.setLineThickness(a.getDimension(attr, Defaults.LINE_THICKNESS));

            } else if (attr == R.styleable.ParticlesView_lineDistance) {
                scene.setLineDistance(a.getDimension(attr, Defaults.LINE_LENGTH));

            } else if (attr == R.styleable.ParticlesView_density) {
                scene.setDensity(a.getInteger(attr, Defaults.DENSITY));

            } else if (attr == R.styleable.ParticlesView_dotColor) {
                scene.setDotColor(a.getColor(attr, Defaults.PARTICLE_COLOR));

            } else if (attr == R.styleable.ParticlesView_lineColor) {
                scene.setLineColor(a.getColor(attr, Defaults.LINE_COLOR));

            } else if (attr == R.styleable.ParticlesView_frameDelayMillis) {
                scene.setFrameDelay(a.getInteger(attr, Defaults.FRAME_DELAY));

            } else if (attr == R.styleable.ParticlesView_stepMultiplier) {
                scene.setStepMultiplier(a.getFloat(attr, Defaults.SPEED_FACTOR));
            }
        }
        scene.setDotRadiusRange(minDotRadius, maxDotRadius);
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
    public void makeBrandNewFrame() {
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
    public void makeBrandNewFrameWithParticlesOffscreen() {
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
