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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * The Particles Drawable
 */
public class ParticlesDrawable extends Drawable implements Animatable, Runnable {

    /**
     * Path calculation padding.
     *
     * @see #applyFreshPointOffScreen(ParticleDot)
     */
    private static final float PCC = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 18f, Resources.getSystem().getDisplayMetrics());

    private static final float STEP_PER_MS = 0.05f;

    private static final int DEFAULT_DOT_NUMBER = 60;

    private static final float DEFAULT_MAX_DOT_RADIUS = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 3f, Resources.getSystem().getDisplayMetrics());

    private static final float DEFAULT_MIN_DOT_RADIUS = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 1f, Resources.getSystem().getDisplayMetrics());

    private static final float DEFAULT_LINE_THICKNESS = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 1, Resources.getSystem().getDisplayMetrics());

    @ColorInt
    private static final int DEFAULT_DOT_COLOR = Color.WHITE;

    @ColorInt
    private static final int DEFAULT_LINE_COLOR = Color.WHITE;

    private static final float DEFAULT_LINE_DISTANCE = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 86, Resources.getSystem().getDisplayMetrics());

    private static final float DEFAULT_STEP_MULTIPLIER = 1f;

    private static final int DEFAULT_DELAY = 10;

    private final List<ParticleDot> mPoints = new ArrayList<>(DEFAULT_DOT_NUMBER);

    private final Random mRandom = new Random();

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

    private boolean mPointsInited;

    private float mMinDotRadius = DEFAULT_MIN_DOT_RADIUS;
    private float mMaxDotRadius = DEFAULT_MAX_DOT_RADIUS;

    private float mLineThickness = DEFAULT_LINE_THICKNESS;

    private float mLineDistance = DEFAULT_LINE_DISTANCE;

    private int mNumDots = DEFAULT_DOT_NUMBER;

    @ColorInt
    private int mDotColor = DEFAULT_DOT_COLOR;

    @ColorInt
    private int mLineColor = DEFAULT_LINE_COLOR;

    private int mDelay = DEFAULT_DELAY;

    private float mStepMultiplier = DEFAULT_STEP_MULTIPLIER;

    private long mLastFrameTime;

    private boolean mAnimating;

    // The alpha value of this Drawable
    private int mAlpha = 255;

    @Override
    public void inflate(@NonNull final Resources r,
            @NonNull final XmlPullParser parser,
            @NonNull final AttributeSet attrs,
            @Nullable final Resources.Theme theme) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);
        final TypedArray a = r.obtainAttributes(attrs, R.styleable.ParticlesDrawable);
        try {
            handleAttrs(a);
        } finally {
            a.recycle();
        }
    }

    void handleAttrs(@NonNull final TypedArray a) {
        final int count = a.getIndexCount();
        float minDotRadius = ParticlesDrawable.DEFAULT_MIN_DOT_RADIUS;
        float maxDotRadius = ParticlesDrawable.DEFAULT_MAX_DOT_RADIUS;
        for (int i = 0; i < count; i++) {
            final int attr = a.getIndex(i);
            if (attr == R.styleable.ParticlesDrawable_minDotRadius) {
                minDotRadius = a.getDimension(attr, ParticlesDrawable.DEFAULT_MIN_DOT_RADIUS);

            } else if (attr == R.styleable.ParticlesDrawable_maxDotRadius) {
                maxDotRadius = a.getDimension(attr, ParticlesDrawable.DEFAULT_MAX_DOT_RADIUS);

            } else if (attr == R.styleable.ParticlesDrawable_lineThickness) {
                setLineThickness(
                        a.getDimension(attr, ParticlesDrawable.DEFAULT_LINE_THICKNESS));

            } else if (attr == R.styleable.ParticlesDrawable_lineDistance) {
                setLineDistance(a.getDimension(attr, ParticlesDrawable.DEFAULT_LINE_DISTANCE));

            } else if (attr == R.styleable.ParticlesDrawable_numDots) {
                setNumDots(a.getInteger(attr, ParticlesDrawable.DEFAULT_DOT_NUMBER));

            } else if (attr == R.styleable.ParticlesDrawable_dotColor) {
                setDotColor(a.getColor(attr, ParticlesDrawable.DEFAULT_DOT_COLOR));

            } else if (attr == R.styleable.ParticlesDrawable_lineColor) {
                setLineColor(a.getColor(attr, ParticlesDrawable.DEFAULT_LINE_COLOR));

            } else if (attr == R.styleable.ParticlesDrawable_frameDelayMillis) {
                setFrameDelay(a.getInteger(attr, ParticlesDrawable.DEFAULT_DELAY));

            } else if (attr == R.styleable.ParticlesDrawable_stepMultiplier) {
                setStepMultiplier(a.getFloat(attr, ParticlesDrawable.DEFAULT_STEP_MULTIPLIER));

            }
        }
        setDotRadiusRange(minDotRadius, maxDotRadius);
    }

    @NonNull
    public Paint getPaint() {
        return mPaint;
    }

    void resetLastFrameTime() {
        mLastFrameTime = 0;
    }

    @Override
    public void draw(@NonNull final Canvas canvas) {
        if (mNumDots > 0) {
            final int pointsSize = mPoints.size();
            for (int i = 0; i < pointsSize; i++) {
                final ParticleDot p1 = mPoints.get(i);
                // Draw connection lines for eligible points
                for (int c = 0; c < pointsSize; c++) {
                    if (c != i) {
                        final ParticleDot p2 = mPoints.get(c);
                        final float distance = distance(p1.x, p1.y, p2.x, p2.y);
                        if (distance < mLineDistance) {
                            drawLine(canvas, p1, p2, distance);
                        }
                    }
                }
            }

            // The dots are drawn above the lines
            // As an optimization, we can exclude point radius when drawing a line and then move point
            // drawing to the loop above
            for (int i = 0; i < pointsSize; i++) {
                final ParticleDot p1 = mPoints.get(i);
                drawDot(canvas, p1);
            }
        }
    }

    private void gotoNextFrameAndSchedule() {
        nextFrame();
        scheduleSelf(this, SystemClock.uptimeMillis() + mDelay);
    }

    @Override
    public void setAlpha(final int alpha) {
        mAlpha = alpha;
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setColorFilter(final ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void start() {
        if (!mAnimating) {
            mAnimating = true;
            gotoNextFrameAndSchedule();
        }
    }

    @Override
    public void stop() {
        if (mAnimating) {
            mAnimating = false;
            unscheduleSelf(this);
        }
    }

    @Override
    public boolean isRunning() {
        return mAnimating;
    }

    @Override
    public void run() {
        if (mAnimating) {
            gotoNextFrameAndSchedule();
        } else {
            mLastFrameTime = 0;
        }
    }

    private int getWidth() {
        return getBounds().width();
    }

    private int getHeight() {
        return getBounds().height();
    }

    /**
     * Resets and makes new random frame. This is useful for re-generating new fancy static
     * backgrounds when not using animations.
     */
    public void makeBrandNewFrame() {
        final int numDots = mNumDots;
        setNumDots(0);
        setNumDots(numDots);
        if (getWidth() != 0 && getHeight() != 0) {
            initPoints();
        }
    }

    /**
     * Set a delay per frame in milliseconds.
     *
     * @param delay delay between frames
     * @throws IllegalArgumentException if delay is a negative number
     */
    public void setFrameDelay(@IntRange(from = 0) final int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("delay must not be nagative");
        }
        mDelay = delay;
    }

    int getFrameDelay() {
        return mDelay;
    }

    /**
     * Sets step multiplier. Use this to control speed.
     *
     * @param stepMultiplier step multiplier
     */
    public void setStepMultiplier(@FloatRange(from = 0) final float stepMultiplier) {
        if (stepMultiplier < 0) {
            throw new IllegalArgumentException("step multiplier must not be nagative");
        }
        mStepMultiplier = stepMultiplier;
    }

    /**
     * Set dot radius range
     *
     * @param minRadius smallest dot radius
     * @param maxRadius largest dot radius
     */
    public void setDotRadiusRange(@FloatRange(from = 0.5f) final float minRadius,
            @FloatRange(from = 0.5f) final float maxRadius) {
        if (minRadius < 0.5f || maxRadius < 0.5f) {
            throw new IllegalArgumentException("Dot radius must not be lass than 0.5");
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
     * Set a line thickness
     *
     * @param lineThickness line thickness
     */
    public void setLineThickness(@FloatRange(from = 1) final float lineThickness) {
        if (lineThickness < 1) {
            throw new IllegalArgumentException("Line thickness must not be less than 1");
        }
        mLineThickness = lineThickness;
    }

    /**
     * Set the maximum distance when the connection line is still drawn between points
     *
     * @param lineDistance maximum distance for connection lines
     */
    public void setLineDistance(@FloatRange(from = 0) final float lineDistance) {
        if (lineDistance < 0) {
            throw new IllegalArgumentException("line distance must not be negative");
        }
        mLineDistance = lineDistance;
    }

    /**
     * Set number of points to draw
     *
     * @param newNum the number of points
     * @throws IllegalArgumentException if number of points is negative
     */
    public void setNumDots(@IntRange(from = 0) final int newNum) {
        if (newNum < 0) {
            throw new IllegalArgumentException("numPoints must not be negative");
        }

        if (newNum != mNumDots) {
            if (mPointsInited) {
                if (newNum > mNumDots) {
                    for (int i = mNumDots; i < newNum; i++) {
                        mPoints.add(makeNewPoint(false));
                    }
                } else {
                    for (int i = 0; i < mNumDots - newNum; i++) {
                        mPoints.remove(0);
                    }
                }
            }
            mNumDots = newNum;
        }
    }

    /**
     * Set the dot color
     *
     * @param dotColor dot color to use
     */
    public void setDotColor(@ColorInt final int dotColor) {
        mDotColor = dotColor;
    }

    /**
     * Set the line color. Note that the color alpha is ignored and will be calculated depending on
     * distance between points
     *
     * @param lineColor line color to use
     */
    public void setLineColor(@ColorInt final int lineColor) {
        mLineColor = lineColor;
    }

    @Override
    public void setBounds(final int left, final int top, final int right, final int bottom) {
        super.setBounds(left, top, right, bottom);
        if (right - left > 0 && bottom - top > 0) {
            if (!mPointsInited) {
                mPointsInited = true;
                initPoints();
            }
        }
    }

    private void initPoints() {
        if (getWidth() == 0 || getHeight() == 0) {
            throw new IllegalStateException("Cannot init points if width or height is 0");
        }
        mPoints.clear();
        for (int i = 0; i < mNumDots; i++) {
            mPoints.add(makeNewPoint(i % 2 == 0));
        }
    }

    @NonNull
    private ParticleDot makeNewPoint(final boolean onScreen) {
        if (getWidth() == 0 || getHeight() == 0) {
            throw new IllegalStateException("Cannot make new point if width or height is 0");
        }

        final ParticleDot point = new ParticleDot();
        if (onScreen) {
            applyFreshPointOnScreen(point);
        } else {
            applyFreshPointOffScreen(point);
        }
        return point;
    }

    /**
     * Calculates values for the next frame
     */
    private void nextFrame() {
        final float step = mLastFrameTime == 0 ? 1f
                : (SystemClock.uptimeMillis() - mLastFrameTime) * STEP_PER_MS;
        final int pointsSize = mPoints.size();
        for (int i = 0; i < pointsSize; i++) {
            final ParticleDot p = mPoints.get(i);
            p.x += step * mStepMultiplier * p.stepMultiplier * p.dCos;
            p.y += step * mStepMultiplier * p.stepMultiplier * p.dSin;

            if (pointOutOfBounds(p.x, p.y)) {
                applyFreshPointOffScreen(p);
            }
        }
        mLastFrameTime = SystemClock.uptimeMillis();
        invalidateSelf();
    }

    /**
     * Set new point coordinates somewhere on screen and apply new direction
     *
     * @param p {@link ParticleDot} to apply new values to
     */
    private void applyFreshPointOnScreen(@NonNull final ParticleDot p) {
        final int w = getWidth();
        final int h = getHeight();
        if (w == 0 || h == 0) {
            throw new IllegalStateException("Cannot apply points if width or height is 0");
        }

        final double direction = Math.toRadians(mRandom.nextInt(360));
        p.dCos = (float) Math.cos(direction);
        p.dSin = (float) Math.sin(direction);
        p.x = (float) mRandom.nextInt(w);
        p.y = (float) mRandom.nextInt(h);
        p.stepMultiplier = newRandomIndividualDotStepMultiplier();
        p.radius = newRandomIndividualDotRadius();
    }

    /**
     * Generates new step multiplier for individual dot.
     * The value is in [0.5:1.5] range
     *
     * @return new step multiplier for individual dot
     */
    private float newRandomIndividualDotStepMultiplier() {
        return 1f + 0.1f * (mRandom.nextInt(11) - 5);
    }

    /**
     * Generates new individual dot radius based on min and max radius setting
     *
     * @return new dot radius
     */
    private float newRandomIndividualDotRadius() {
        return mMinDotRadius == mMaxDotRadius ? mMinDotRadius : mMinDotRadius
                + (mRandom.nextInt((int) ((mMaxDotRadius - mMinDotRadius) * 100f))) / 100f;
    }

    /**
     * Set new point coordinates somewhere off screen and apply new direction towards the screen
     *
     * @param p {@link ParticleDot} to apply new values to
     */
    private void applyFreshPointOffScreen(@NonNull final ParticleDot p) {
        final int w = getWidth();
        final int h = getHeight();
        if (w == 0 || h == 0) {
            throw new IllegalStateException("Cannot apply points if width or height is 0");
        }

        p.x = (float) mRandom.nextInt(w);
        p.y = (float) mRandom.nextInt(h);

        // The offset to make when creating point of out bounds
        final float offset = mMinDotRadius + mLineDistance;

        // Point angle range
        final float startAngle;
        float endAngle;

        // Make random offset and calulate angles so that the direction of travel will always be
        // towards our View

        switch (mRandom.nextInt(4)) {
            case 0:
                // offset to left
                p.x = -offset;
                startAngle = angleDeg(PCC, PCC, p.x, p.y);
                endAngle = angleDeg(PCC, h - PCC, p.x, p.y);
                break;

            case 1:
                // offset to top
                p.y = -offset;
                startAngle = angleDeg(w - PCC, PCC, p.x, p.y);
                endAngle = angleDeg(PCC, PCC, p.x, p.y);
                break;

            case 2:
                // offset to right
                p.x = w + offset;
                startAngle = angleDeg(w - PCC, h - PCC, p.x, p.y);
                endAngle = angleDeg(w - PCC, PCC, p.x, p.y);
                break;

            case 3:
                // offset to bottom
                p.y = h + offset;
                startAngle = angleDeg(PCC, h - PCC, p.x, p.y);
                endAngle = angleDeg(w - PCC, h - PCC, p.x, p.y);
                break;

            default:
                throw new IllegalArgumentException("Supplied value out of range");
        }

        if (endAngle < startAngle) {
            endAngle += 360;
        }

        // Get random angle from angle range
        final float randomAngleInRange = startAngle + (mRandom
                .nextInt((int) Math.abs(endAngle - startAngle)));
        final double direction = Math.toRadians(randomAngleInRange);
        p.dCos = (float) Math.cos(direction);
        p.dSin = (float) Math.sin(direction);
        p.stepMultiplier = newRandomIndividualDotStepMultiplier();
        p.radius = newRandomIndividualDotRadius();
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
        final float offset = mMinDotRadius + mLineDistance;
        return x + offset < 0 || x - offset > getWidth()
                || y + offset < 0 || y - offset > getHeight();
    }

    /**
     * Draw a point on the {@link Canvas}
     *
     * @param canvas the {@link Canvas} to draw on
     * @param p      the {@link ParticleDot} to draw
     */
    private void drawDot(@NonNull final Canvas canvas,
            @NonNull final ParticleDot p) {
        final int alpha = Color.alpha(mDotColor) * mAlpha / 255;
        mPaint.setColor((mDotColor & 0x00FFFFFF) | (alpha << 24));
        canvas.drawCircle(p.x, p.y, p.radius, mPaint);
    }

    /**
     * Draw a line between two {@link ParticleDot}s on {@link Canvas}
     *
     * @param canvas   the {@link Canvas} to draw on
     * @param p1       the neighbour {@link ParticleDot}
     * @param p2       the neighbour {@link ParticleDot}
     * @param distance the distance between p1 and p2
     */
    private void drawLine(@NonNull final Canvas canvas,
            @NonNull final ParticleDot p1,
            @NonNull final ParticleDot p2,
            final float distance) {
        final float alphaPercent = 1f - distance / mLineDistance;
        int alpha = (int) (255f * alphaPercent);
        alpha = alpha * mAlpha / 255;

        // Set line color alpha
        mPaint.setStrokeWidth(mLineThickness);
        mPaint.setColor((mLineColor & 0x00FFFFFF) | (alpha << 24));
        // TODO exclude radius for better performance?
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mPaint);
    }


    /**
     * Calculates the distance between two points
     *
     * @return distance between two points
     */
    private static float distance(final float ax, final float ay,
            final float bx, final float by) {
        return (float) Math.sqrt(
                (ax - bx) * (ax - bx) +
                        (ay - by) * (ay - by)
        );
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
     * Represents a dot by holding x and y coordinates, travel direction and step multiplier
     */
    private static final class ParticleDot {

        /**
         * Direction cosine
         */
        float dCos;

        /**
         * Direction sine
         */
        float dSin;

        /**
         * Current X
         */
        float x;

        /**
         * Current Y
         */
        float y;

        /**
         * Step multiplier for this dot
         */
        float stepMultiplier;

        /**
         * Radius multiplier for this dot
         */
        float radius;
    }
}
