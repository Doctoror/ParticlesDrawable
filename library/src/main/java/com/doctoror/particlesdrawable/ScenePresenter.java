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
package com.doctoror.particlesdrawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.doctoror.particlesdrawable.contract.SceneController;
import com.doctoror.particlesdrawable.contract.SceneRenderer;
import com.doctoror.particlesdrawable.contract.SceneScheduler;

import java.util.Random;

import androidx.annotation.NonNull;

/**
 * Particles Scene Controller
 */
@KeepAsApi
public final class ScenePresenter implements Animatable, Runnable, SceneController {

    /**
     * Path calculation padding.
     *
     * @see #applyFreshParticleOffScreen(int)
     */
    private static final float PCC = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 18f, Resources.getSystem().getDisplayMetrics());

    private static final float STEP_PER_MS = 0.05f;

    private final Random random = new Random();

    private final ParticlesScene scene;
    private final SceneScheduler scheduler;
    private final SceneRenderer renderer;

    private boolean particlesInited;

    private long lastFrameTime;
    private long lastDrawDuration;

    private volatile boolean animating;

    public ScenePresenter(
            @NonNull final ParticlesScene scene,
            @NonNull final SceneScheduler scheduler,
            @NonNull final SceneRenderer renderer) {
        this.scene = scene;
        this.scheduler = scheduler;
        this.renderer = renderer;
    }

    @NonNull
    private SceneScheduler getViewScheduler() {
        return scheduler;
    }

    void inflate(@NonNull final Resources r, @NonNull final AttributeSet attrs) {
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

            } else if (attr == R.styleable.ParticlesView_numDots) {
                scene.setNumDots(a.getInteger(attr, Defaults.DENSITY));

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
        getViewScheduler()
                .scheduleNextFrame(Math.max(scene.getFrameDelay() - lastDrawDuration, 0L));
    }

    void setAlpha(final int alpha) {
        scene.setAlpha(alpha);
    }

    int getAlpha() {
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
            getViewScheduler().unscheduleNextFrame();
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
        final long startTime = SystemClock.uptimeMillis();
        renderer.drawScene(scene);
        lastDrawDuration = SystemClock.uptimeMillis() - startTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrame() {
        final ParticlesScene model = scene;
        if (model.getWidth() != 0 && model.getHeight() != 0) {
            resetLastFrameTime();
            initParticles();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrameWithPointsOffscreen() {
        final ParticlesScene model = scene;
        if (model.getWidth() != 0 && model.getHeight() != 0) {
            resetLastFrameTime();
            initParticlesOffScreen();
        }
    }

    public void setDimensions(final int width, final int height) {
        final ParticlesScene model = scene;
        model.setWidth(width);
        model.setHeight(height);
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
                ScenePresenter.this.addNewParticle(position, position % 2 == 0);
            }
        });
    }

    private void initParticlesOffScreen() {
        initParticles(new ParticleCreationStrategy() {

            @Override
            public void addNewParticle(final int position) {
                ScenePresenter.this.addNewParticle(position, false);
            }
        });
    }

    private void initParticles(@NonNull final ParticleCreationStrategy strategy) {
        final ParticlesScene model = scene;
        if (model.getWidth() == 0 || model.getHeight() == 0) {
            throw new IllegalStateException("Cannot init points if width or height is 0");
        }
        for (int i = 0; i < model.getNumDots(); i++) {
            strategy.addNewParticle(i);
        }
    }

    @SuppressWarnings("WeakerAccess")
        // exposed to avoid synthethic accessor
    void addNewParticle(
            final int position,
            final boolean onScreen) {
        final ParticlesScene model = scene;
        if (model.getWidth() == 0 || model.getHeight() == 0) {
            throw new IllegalStateException("Cannot make new point if width or height is 0");
        }

        if (onScreen) {
            applyFreshParticleOnScreen(position);
        } else {
            applyFreshParticleOffScreen(position);
        }
    }

    /**
     * Set new point coordinates somewhere on screen and apply new direction
     *
     * @param position the point position to apply new values to
     */
    private void applyFreshParticleOnScreen(final int position) {
        final ParticlesScene model = scene;
        final int w = model.getWidth();
        final int h = model.getHeight();
        if (w == 0 || h == 0) {
            throw new IllegalStateException("Cannot apply points if width or height is 0");
        }

        final double direction = Math.toRadians(random.nextInt(360));
        final float dCos = (float) Math.cos(direction);
        final float dSin = (float) Math.sin(direction);
        final float x = random.nextInt(w);
        final float y = random.nextInt(h);
        final float stepMultiplier = newRandomIndividualDotStepMultiplier();
        final float radius = newRandomIndividualDotRadius();

        model.setParticleData(
                position,
                x,
                y,
                dCos,
                dSin,
                radius,
                stepMultiplier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextFrame() {
        final ParticlesScene model = scene;
        final float step = lastFrameTime == 0 ? 1f
                : (SystemClock.uptimeMillis() - lastFrameTime) * STEP_PER_MS;

        final int particlesCount = model.getNumDots();
        for (int i = 0; i < particlesCount; i++) {
            float x = model.getParticleX(i);
            float y = model.getParticleY(i);

            final float stepMultiplier = model.getParticleStepMultiplier(i);
            final float dCos = model.getParticleDirectionCos(i);
            final float dSin = model.getParticleDirectionSin(i);

            x += step * model.getStepMultiplier() * stepMultiplier * dCos;
            y += step * model.getStepMultiplier() * stepMultiplier * dSin;

            if (pointOutOfBounds(x, y)) {
                applyFreshParticleOffScreen(i);
            } else {
                model.setParticleX(i, x);
                model.setParticleY(i, y);
            }
        }

        lastFrameTime = SystemClock.uptimeMillis();
    }

    /**
     * Generates new step multiplier for individual dot.
     * The value is in [0.5:1.5] range
     *
     * @return new step multiplier for individual dot
     */
    private float newRandomIndividualDotStepMultiplier() {
        return 1f + 0.1f * (random.nextInt(11) - 5);
    }

    /**
     * Generates new individual dot radius based on min and max radius setting
     *
     * @return new dot radius
     */
    private float newRandomIndividualDotRadius() {
        final ParticlesScene model = scene;
        return model.getMinDotRadius() == model.getMaxDotRadius() ?
                model.getMinDotRadius() : model.getMinDotRadius()
                + (random.nextInt(
                (int) ((model.getMaxDotRadius() - model.getMinDotRadius()) * 100f))) / 100f;
    }

    /**
     * Set new point coordinates somewhere off screen and apply new direction towards the screen
     *
     * @param position the particle position to apply new values to
     */
    private void applyFreshParticleOffScreen(final int position) {
        final ParticlesScene model = scene;
        final int w = model.getWidth();
        final int h = model.getHeight();
        if (w == 0 || h == 0) {
            throw new IllegalStateException("Cannot apply points if width or height is 0");
        }

        float x = random.nextInt(w);
        float y = random.nextInt(h);

        // The offset to make when creating point of out bounds
        final short offset = (short) (model.getMinDotRadius() + model.getLineDistance());

        // Point angle range
        final float startAngle;
        float endAngle;

        // Make random offset and calulate angles so that the direction of travel will always be
        // towards our View

        switch (random.nextInt(4)) {
            case 0:
                // offset to left
                x = (short) -offset;
                startAngle = angleDeg(PCC, PCC, x, y);
                endAngle = angleDeg(PCC, h - PCC, x, y);
                break;

            case 1:
                // offset to top
                y = (short) -offset;
                startAngle = angleDeg(w - PCC, PCC, x, y);
                endAngle = angleDeg(PCC, PCC, x, y);
                break;

            case 2:
                // offset to right
                x = (short) (w + offset);
                startAngle = angleDeg(w - PCC, h - PCC, x, y);
                endAngle = angleDeg(w - PCC, PCC, x, y);
                break;

            case 3:
                // offset to bottom
                y = (short) (h + offset);
                startAngle = angleDeg(PCC, h - PCC, x, y);
                endAngle = angleDeg(w - PCC, h - PCC, x, y);
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
        final float stepMultiplier = newRandomIndividualDotStepMultiplier();
        final float radius = newRandomIndividualDotRadius();

        model.setParticleData(
                position,
                x,
                y,
                dCos,
                dSin,
                radius,
                stepMultiplier);
    }

    /**
     * Used for checking if the point is off-screen and farther than line distance
     *
     * @param x the point x
     * @param y the point y
     * @return true if the point is off-screen and guaranteed not to be used to draw a line to the
     * closest point on-screen
     */
    private boolean pointOutOfBounds(final float x, final float y) {
        final ParticlesScene model = scene;
        final float offset = model.getMinDotRadius() + model.getLineDistance();
        return x + offset < 0 || x - offset > model.getWidth()
                || y + offset < 0 || y - offset > model.getHeight();
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

    private interface ParticleCreationStrategy {

        void addNewParticle(int position);
    }
}
