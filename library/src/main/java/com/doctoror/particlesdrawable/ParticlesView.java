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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

/**
 * The Particles View.
 *
 * Automatically starts on {@link #onAttachedToWindow()} or when visibility is set to
 * {@link #VISIBLE}. Automatically stops on {@link #onDetachedFromWindow()} or when visbility set
 * to {@link #INVISIBLE} or {@link #GONE}.
 *
 * You may also use {@link #start()} and {@link #stop()} on your behalf. Note when you call {@link
 * #stop()} explicitly, the animation will not automatically restart when you trigger visibility or
 * when this View gets attached to window.
 *
 * The View cannot tell whether your hosting {@link android.app.Activity} or
 * {@link android.app.Fragment} is started or stopped. It can only tell when it's being destroyed
 * ({@link #onDetachedFromWindow()} will be called) so this is where it stops animations
 * automatically. Thus, It is recommended to call {@link #stop()} when the hosting component gets
 * onStop() call and call {@link #start()} when the hosting component gets onStart() call.
 */
public class ParticlesView extends View {

    // We don't use this Drawable as background so that the user might be able to set it's own
    private final ParticlesDrawable mDrawable = new ParticlesDrawable();

    /**
     * Whether explicitly stopped by user. This means it will not start automatically on visibility
     * change or when attached to window.
     */
    @VisibleForTesting
    boolean mExplicitlyStopped;

    private boolean mAttachedToWindow;
    private boolean mEmulateOnAttachToWindow;

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
            final TypedArray a = context
                    .obtainStyledAttributes(attrs, R.styleable.ParticlesDrawable);
            try {
                mDrawable.handleAttrs(a);
            } finally {
                a.recycle();
            }
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
    protected void onVisibilityChanged(@NonNull final View changedView, final int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != View.VISIBLE) {
            stopInternal();
        } else {
            startInternal();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        startInternal();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachedToWindow = false;
        stopInternal();
    }

    /**
     * Resets and makes new random frame. This is useful for re-generating new fancy static
     * backgrounds when not using animations.
     */
    public void makeBrandNewFrame() {
        mDrawable.makeBrandNewFrame();
    }

    /**
     * Start animating. This will clear the explicit control flag if set by {@link #stop()}.
     * Note that if this View's visibility is not {@link #VISIBLE} or it's not attached to window,
     * this will not start animating until the state changes to meet the requirements above.
     */
    public void start() {
        mExplicitlyStopped = false;
        startInternal();
    }

    /**
     * Explicilty stop animating. This will stop animating and no animations will start
     * automatically until you call {@link #start()}.
     */
    public void stop() {
        mExplicitlyStopped = true;
        stopInternal();
    }

    @VisibleForTesting
    void startInternal() {
        if (!mExplicitlyStopped && isVisibleWithAllParents(this) && isAttachedToWindowCompat()) {
            mDrawable.start();
            postInvalidateDelayed(mDrawable.getFrameDelay());
        }
    }

    @VisibleForTesting
    void stopInternal() {
        mDrawable.stop();
    }

    @VisibleForTesting
    boolean isRunning() {
        return mDrawable.isRunning();
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    void setEmulateOnAttachToWindow(final boolean emulateOnAttachToWindow) {
        mEmulateOnAttachToWindow = emulateOnAttachToWindow;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean isAttachedToWindowCompat() {
        if (mEmulateOnAttachToWindow) {
            return mAttachedToWindow;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return isAttachedToWindow();
        }
        return mAttachedToWindow;
    }

    private boolean isVisibleWithAllParents(@NonNull final View view) {
        if (view.getVisibility() != VISIBLE) {
            return false;
        }

        final ViewParent parent = view.getParent();
        if (parent instanceof View) {
            return isVisibleWithAllParents((View) parent);
        }

        return true;
    }
}
