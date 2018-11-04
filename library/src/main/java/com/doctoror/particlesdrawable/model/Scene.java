/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
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
package com.doctoror.particlesdrawable.model;

import com.doctoror.particlesdrawable.Defaults;
import com.doctoror.particlesdrawable.KeepAsApi;
import com.doctoror.particlesdrawable.contract.SceneConfiguration;

import java.nio.FloatBuffer;
import java.util.Locale;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

@KeepAsApi
public final class Scene implements SceneConfiguration {

    private static final int COORDINATES_PER_VERTEX = 2;

    private float particleRadiusMin = Defaults.PARTICLE_RADIUS_MIN;
    private float particleRadiusMax = Defaults.PARTICLE_RADIUS_MAX;

    private float mLineThickness = Defaults.LINE_THICKNESS;

    private float mLineDistance = Defaults.LINE_LENGTH;

    private int density = Defaults.DENSITY;

    @ColorInt
    private int particleColor = Defaults.PARTICLE_COLOR;

    @ColorInt
    private int mLineColor = Defaults.LINE_COLOR;

    private int mDelay = Defaults.FRAME_DELAY;

    private float mStepMultiplier = Defaults.SPEED_FACTOR;

    // The alpha value of the Drawable
    private int mAlpha = 255;

    private int mWidth;
    private int mHeight;

    private FloatBuffer mCoordinates;

    /**
     * First index is for cos, next index is for sin.
     * Next follows for next perticle in the same format.
     */
    private FloatBuffer mDirections;

    private FloatBuffer mRadiuses;
    private FloatBuffer mStepMultipliers;

    public Scene() {
        initBuffers(density);
    }

    @NonNull
    public FloatBuffer getCoordinates() {
        return mCoordinates;
    }

    @NonNull
    public FloatBuffer getRadiuses() {
        return mRadiuses;
    }

    public void setWidth(final int width) {
        mWidth = width;
    }

    public void setHeight(final int height) {
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setParticleData(
            final int position,
            final float x,
            final float y,
            final float dCos,
            final float dSin,
            final float radius,
            final float stepMultiplier) {
        setParticleX(position, x);
        setParticleY(position, y);

        setParticleDirectionCos(position, dCos);
        setParticleDirectionSin(position, dSin);

        mRadiuses.put(position, radius);
        mStepMultipliers.put(position, stepMultiplier);
    }

    public float getParticleX(final int position) {
        return mCoordinates.get(position * 2);
    }

    public float getParticleY(final int position) {
        return mCoordinates.get(position * 2 + 1);
    }

    public float getParticleDirectionCos(final int position) {
        return mDirections.get(position * 2);
    }

    public float getParticleDirectionSin(final int position) {
        return mDirections.get(position * 2 + 1);
    }

    public float getParticleStepMultiplier(final int position) {
        return mStepMultipliers.get(position);
    }

    public void setParticleX(final int position, final float x) {
        mCoordinates.put(position * 2, x);
    }

    public void setParticleY(final int position, final float y) {
        mCoordinates.put(position * 2 + 1, y);
    }

    private void setParticleDirectionCos(final int position, final float direction) {
        mDirections.put(position * 2, direction);
    }

    private void setParticleDirectionSin(final int position, final float direction) {
        mDirections.put(position * 2 + 1, direction);
    }

    public void setAlpha(final int alpha) {
        mAlpha = alpha;
    }

    @IntRange(from = 0, to = 255)
    public int getAlpha() {
        return mAlpha;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFrameDelay(@IntRange(from = 0) final int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("delay must not be nagative");
        }
        mDelay = delay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFrameDelay() {
        return mDelay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStepMultiplier(@FloatRange(from = 0) final float stepMultiplier) {
        if (stepMultiplier < 0) {
            throw new IllegalArgumentException("step multiplier must not be nagative");
        }
        if (Float.compare(stepMultiplier, Float.NaN) == 0) {
            throw new IllegalArgumentException("step multiplier must be a valid float");
        }
        mStepMultiplier = stepMultiplier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getStepMultiplier() {
        return mStepMultiplier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParticleRadiusRange(
            @FloatRange(from = 0.5f) final float minRadius,
            @FloatRange(from = 0.5f) final float maxRadius) {
        if (minRadius < 0.5f || maxRadius < 0.5f) {
            throw new IllegalArgumentException("Particle radius must not be less than 0.5");
        }
        if (Float.compare(minRadius, Float.NaN) == 0
                || Float.compare(maxRadius, Float.NaN) == 0) {
            throw new IllegalArgumentException("Particle radius must be a valid float");
        }
        if (minRadius > maxRadius) {
            throw new IllegalArgumentException(String.format(Locale.US,
                    "Min radius must not be greater than max, but min = %f, max = %f",
                    minRadius, maxRadius));
        }
        particleRadiusMin = minRadius;
        particleRadiusMax = maxRadius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getParticleRadiusMin() {
        return particleRadiusMin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getParticleRadiusMax() {
        return particleRadiusMax;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineThickness(@FloatRange(from = 1) final float lineThickness) {
        if (lineThickness < 1) {
            throw new IllegalArgumentException("Line thickness must not be less than 1");
        }
        if (Float.compare(lineThickness, Float.NaN) == 0) {
            throw new IllegalArgumentException("line thickness must be a valid float");
        }
        mLineThickness = lineThickness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLineThickness() {
        return mLineThickness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineDistance(@FloatRange(from = 0) final float lineDistance) {
        if (lineDistance < 0) {
            throw new IllegalArgumentException("line distance must not be negative");
        }
        if (Float.compare(lineDistance, Float.NaN) == 0) {
            throw new IllegalArgumentException("line distance must be a valid float");
        }
        mLineDistance = lineDistance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLineDistance() {
        return mLineDistance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDensity(@IntRange(from = 0) final int density) {
        if (density < 0) {
            throw new IllegalArgumentException("Density must not be negative");
        }
        if (this.density != density) {
            this.density = density;
            initBuffers(density);
        }
    }

    private void initBuffers(final int density) {
        initCoordinates(density);
        initDirections(density);
        initStepMultipliers(density);
        initRadiuses(density);
    }

    private void initCoordinates(final int density) {
        final int capacity = density * COORDINATES_PER_VERTEX;
        if (mCoordinates == null || mCoordinates.capacity() != capacity) {
            mCoordinates = FloatBuffer.allocate(capacity);
        }
    }

    private void initDirections(final int density) {
        final int capacity = density * 2;
        if (mDirections == null || mDirections.capacity() != capacity) {
            mDirections = FloatBuffer.allocate(capacity);
        }
    }

    private void initStepMultipliers(final int density) {
        if (mStepMultipliers == null || mStepMultipliers.capacity() != density) {
            mStepMultipliers = FloatBuffer.allocate(density);
        }
    }

    private void initRadiuses(final int density) {
        if (mRadiuses == null || mRadiuses.capacity() != density) {
            mRadiuses = FloatBuffer.allocate(density);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDensity() {
        return density;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParticleColor(@ColorInt final int color) {
        particleColor = color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getParticleColor() {
        return particleColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineColor(@ColorInt final int lineColor) {
        mLineColor = lineColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLineColor() {
        return mLineColor;
    }
}
