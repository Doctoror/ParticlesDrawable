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
package com.doctoror.particlesdrawable;

import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.doctoror.particlesdrawable.contract.SceneConfiguration;

import java.nio.FloatBuffer;
import java.util.Locale;

/**
 * The Particles scene
 */
public final class ParticlesScene implements SceneConfiguration {

    private static final int COORDINATES_PER_VERTEX = 2;

    private float mMinDotRadius = Defaults.DEFAULT_MIN_DOT_RADIUS;
    private float mMaxDotRadius = Defaults.DEFAULT_MAX_DOT_RADIUS;

    private float mLineThickness = Defaults.DEFAULT_LINE_THICKNESS;

    private float mLineDistance = Defaults.DEFAULT_LINE_DISTANCE;

    private int mNumDots = Defaults.DEFAULT_DOT_NUMBER;

    @ColorInt
    private int mDotColor = Defaults.DEFAULT_DOT_COLOR;

    @ColorInt
    private int mLineColor = Defaults.DEFAULT_LINE_COLOR;

    private int mDelay = Defaults.DEFAULT_DELAY;

    private float mStepMultiplier = Defaults.DEFAULT_STEP_MULTIPLIER;

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

    public ParticlesScene() {
        initBuffers(mNumDots);
    }

    @NonNull
    public FloatBuffer getCoordinates() {
        return mCoordinates;
    }

    @NonNull
    public FloatBuffer getRadiuses() {
        return mRadiuses;
    }

    void setWidth(final int width) {
        mWidth = width;
    }

    void setHeight(final int height) {
        mHeight = height;
    }

    int getWidth() {
        return mWidth;
    }

    int getHeight() {
        return mHeight;
    }

    void setParticleData(
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

    void setParticleX(final int position, final float x) {
        mCoordinates.put(position * 2, x);
    }

    void setParticleY(final int position, final float y) {
        mCoordinates.put(position * 2 + 1, y);
    }

    private void setParticleDirectionCos(final int position, final float direction) {
        mDirections.put(position * 2, direction);
    }

    private void setParticleDirectionSin(final int position, final float direction) {
        mDirections.put(position * 2 + 1, direction);
    }

    void setAlpha(final int alpha) {
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
    public void setDotRadiusRange(
            @FloatRange(from = 0.5f) final float minRadius,
            @FloatRange(from = 0.5f) final float maxRadius) {
        if (minRadius < 0.5f || maxRadius < 0.5f) {
            throw new IllegalArgumentException("Dot radius must not be less than 0.5");
        }
        if (Float.compare(minRadius, Float.NaN) == 0
                || Float.compare(maxRadius, Float.NaN) == 0) {
            throw new IllegalArgumentException("Dot radius must be a valid float");
        }
        if (minRadius > maxRadius) {
            throw new IllegalArgumentException(String.format(Locale.US,
                    "Min radius must not be greater than max, but min = %f, max = %f",
                    minRadius, maxRadius));
        }
        mMinDotRadius = minRadius;
        mMaxDotRadius = maxRadius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getMinDotRadius() {
        return mMinDotRadius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getMaxDotRadius() {
        return mMaxDotRadius;
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
    public void setNumDots(@IntRange(from = 0) final int newNum) {
        if (newNum < 0) {
            throw new IllegalArgumentException("numPoints must not be negative");
        }
        if (mNumDots != newNum) {
            mNumDots = newNum;
            initBuffers(newNum);
        }
    }

    private void initBuffers(final int dotCount) {
        initCoordinates(dotCount);
        initDirections(dotCount);
        initStepMultipliers(dotCount);
        initRadiuses(dotCount);
    }

    private void initCoordinates(final int dotCount) {
        final int capacity = dotCount * COORDINATES_PER_VERTEX;
        if (mCoordinates == null || mCoordinates.capacity() != capacity) {
            mCoordinates = FloatBuffer.allocate(capacity);
        }
    }

    private void initDirections(final int dotCount) {
        final int capacity = dotCount * 2;
        if (mDirections == null || mDirections.capacity() != capacity) {
            mDirections = FloatBuffer.allocate(capacity);
        }
    }

    private void initStepMultipliers(final int dotCount) {
        if (mStepMultipliers == null || mStepMultipliers.capacity() != dotCount) {
            mStepMultipliers = FloatBuffer.allocate(dotCount);
        }
    }

    private void initRadiuses(final int dotCount) {
        if (mRadiuses == null || mRadiuses.capacity() != dotCount) {
            mRadiuses = FloatBuffer.allocate(dotCount);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumDots() {
        return mNumDots;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDotColor(@ColorInt final int dotColor) {
        mDotColor = dotColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDotColor() {
        return mDotColor;
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
