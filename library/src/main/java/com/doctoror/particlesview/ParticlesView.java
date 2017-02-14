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
package com.doctoror.particlesview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * The Particles View
 */
public class ParticlesView extends View {

    // We don't use this Drawable as background so that the user might be able to set it's own
    private final ParticlesDrawable mDrawable = new ParticlesDrawable();

    public ParticlesView(final Context context) {
        super(context);
        init(context, null);
    }

    public ParticlesView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ParticlesView(final Context context, final AttributeSet attrs,
            final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ParticlesView(final Context context, final AttributeSet attrs,
            final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_HARDWARE, mDrawable.getPaint());
        }
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ParticlesView);
            final int count = a.getIndexCount();
            float minDotRadius = ParticlesDrawable.DEFAULT_MIN_DOT_RADIUS;
            float maxDotRadius = ParticlesDrawable.DEFAULT_MAX_DOT_RADIUS;
            for (int i = 0; i < count; i++) {
                final int attr = a.getIndex(i);
                if (attr == R.styleable.ParticlesView_minDotRadius) {
                    minDotRadius = a.getDimension(attr, ParticlesDrawable.DEFAULT_MIN_DOT_RADIUS);

                } else if (attr == R.styleable.ParticlesView_maxDotRadius) {
                    maxDotRadius = a.getDimension(attr, ParticlesDrawable.DEFAULT_MAX_DOT_RADIUS);

                } else if (attr == R.styleable.ParticlesView_lineThickness) {
                    setLineThickness(
                            a.getDimension(attr, ParticlesDrawable.DEFAULT_LINE_THICKNESS));

                } else if (attr == R.styleable.ParticlesView_lineDistance) {
                    setLineDistance(a.getDimension(attr, ParticlesDrawable.DEFAULT_LINE_DISTANCE));

                } else if (attr == R.styleable.ParticlesView_numDots) {
                    setNumDots(a.getInteger(attr, ParticlesDrawable.DEFAULT_DOT_NUMBER));

                } else if (attr == R.styleable.ParticlesView_dotColor) {
                    setDotColor(a.getColor(attr, ParticlesDrawable.DEFAULT_DOT_COLOR));

                } else if (attr == R.styleable.ParticlesView_lineColor) {
                    setLineColor(a.getColor(attr, ParticlesDrawable.DEFAULT_LINE_COLOR));

                } else if (attr == R.styleable.ParticlesView_frameDelayMillis) {
                    setFrameDelay(a.getInteger(attr, ParticlesDrawable.DEFAULT_DELAY));

                } else if (attr == R.styleable.ParticlesView_stepMultiplier) {
                    setStepMultiplier(a.getFloat(attr, ParticlesDrawable.DEFAULT_STEP_MULTIPLIER));

                }
            }
            setDotRadiusRange(minDotRadius, maxDotRadius);
            a.recycle();
        }
    }

    /**
     * Set a delay per frame in milliseconds.
     *
     * @param delay delay between frames
     * @throws IllegalArgumentException if delay is a negative number
     */
    public void setFrameDelay(@IntRange(from = 0) final int delay) {
        mDrawable.setFrameDelay(delay);
    }

    /**
     * Sets step multiplier. Use this to control speed.
     *
     * @param stepMultiplier step multiplier
     */
    public void setStepMultiplier(@FloatRange(from = 0) final float stepMultiplier) {
        mDrawable.setStepMultiplier(stepMultiplier);
    }

    /**
     * Set dot radius range
     *
     * @param minRadius smallest dot radius
     * @param maxRadius largest dot radius
     */
    public void setDotRadiusRange(
            @FloatRange(from = 0.5f) final float minRadius,
            @FloatRange(from = 0.5f) final float maxRadius) {
        mDrawable.setDotRadiusRange(minRadius, maxRadius);
    }

    /**
     * Set a line thickness
     *
     * @param lineThickness line thickness
     */
    public void setLineThickness(@FloatRange(from = 1) final float lineThickness) {
        mDrawable.setLineThickness(lineThickness);
    }

    /**
     * Set the maximum distance when the connection line is still drawn between points
     *
     * @param lineDistance maximum distance for connection lines
     */
    public void setLineDistance(@FloatRange(from = 0) final float lineDistance) {
        mDrawable.setLineDistance(lineDistance);
    }

    /**
     * Set number of points to draw
     *
     * @param newNum the number of points
     * @throws IllegalArgumentException if number of points is negative
     */
    public void setNumDots(@IntRange(from = 0) final int newNum) {
        mDrawable.setNumDots(newNum);
    }

    /**
     * Set the dot color
     *
     * @param dotColor dot color to use
     */
    public void setDotColor(@ColorInt final int dotColor) {
        mDrawable.setDotColor(dotColor);
    }

    /**
     * Set the line color. Note that the color alpha is ignored and will be calculated depending on
     * distance between points
     *
     * @param lineColor line color to use
     */
    public void setLineColor(@ColorInt final int lineColor) {
        mDrawable.setLineColor(lineColor);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawable.setBounds(0, 0, w, h);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        mDrawable.draw(canvas);

        if (mDrawable.isRunning() && isEnabled()) {
            mDrawable.run();
            postInvalidateDelayed(mDrawable.getFrameDelay());
        } else {
            mDrawable.resetLastFrameTime();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDrawable.start();
        postInvalidateDelayed(mDrawable.getFrameDelay());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDrawable.stop();
    }
}
