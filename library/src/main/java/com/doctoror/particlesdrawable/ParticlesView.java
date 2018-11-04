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
import android.graphics.Paint;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

import com.doctoror.particlesdrawable.contract.SceneConfiguration;
import com.doctoror.particlesdrawable.contract.SceneController;
import com.doctoror.particlesdrawable.contract.SceneRenderer;
import com.doctoror.particlesdrawable.contract.SceneScheduler;
import com.doctoror.particlesdrawable.engine.Engine;
import com.doctoror.particlesdrawable.model.Scene;
import com.doctoror.particlesdrawable.renderer.CanvasSceneRenderer;
import com.doctoror.particlesdrawable.renderer.DefaultSceneRenderer;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.annotation.VisibleForTesting;

/**
 * The Particles View.
 * <p>
 * Automatically starts on {@link #onAttachedToWindow()} or when visibility is set to
 * {@link #VISIBLE}. Automatically stops on {@link #onDetachedFromWindow()} or when visbility set
 * to {@link #INVISIBLE} or {@link #GONE}.
 * <p>
 * You may also use {@link #start()} and {@link #stop()} on your behalf. Note when you call {@link
 * #stop()} explicitly, the animation will not automatically restart when you trigger visibility or
 * when this View gets attached to window.
 * <p>
 * The View does not use Lifecycle api and thus cannot tell whether your hosting Activity or
 * Fragment is started or stopped. It can only tell when it's being destroyed
 * ({@link #onDetachedFromWindow()} will be called) so this is where it stops animations
 * automatically. Thus, It is recommended to call {@link #stop()} when the hosting component gets
 * onStop() call and call {@link #start()} when the hosting component gets onStart() call.
 */
@Keep
public class ParticlesView extends View implements
        Animatable,
        SceneConfiguration,
        SceneController,
        SceneScheduler {

    private final CanvasSceneRenderer canvasSceneRenderer = new CanvasSceneRenderer();
    private final Scene scene = new Scene();
    private final SceneRenderer renderer = new DefaultSceneRenderer(canvasSceneRenderer);
    private final Engine engine = new Engine(scene, this, renderer);

    /**
     * Whether explicitly stopped by user. This means it will not start automatically on visibility
     * change or when attached to window.
     */
    private boolean mExplicitlyStopped;

    private boolean mAttachedToWindow;
    private boolean mEmulateOnAttachToWindow;

    public ParticlesView(@NonNull final Context context) {
        super(context);
        init(context, null);
    }

    public ParticlesView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ParticlesView(
            @NonNull final Context context,
            @Nullable final AttributeSet attrs,
            @AttrRes final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ParticlesView(
            @NonNull final Context context,
            @Nullable final AttributeSet attrs,
            @AttrRes final int defStyleAttr,
            @StyleRes final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_HARDWARE, canvasSceneRenderer.getPaint());
        }
        if (attrs != null) {
            final TypedArray a = context
                    .obtainStyledAttributes(attrs, R.styleable.ParticlesView);
            try {
                engine.handleAttrs(a);
            } finally {
                a.recycle();
            }
        }
    }

    @NonNull
    @Keep
    public Paint getPaint() {
        return canvasSceneRenderer.getPaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextFrame() {
        engine.nextFrame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrame() {
        engine.makeBrandNewFrame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrameWithParticlesOffscreen() {
        engine.makeBrandNewFrameWithParticlesOffscreen();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFrameDelay(@IntRange(from = 0) final int delay) {
        scene.setFrameDelay(delay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFrameDelay() {
        return scene.getFrameDelay();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStepMultiplier(@FloatRange(from = 0) final float stepMultiplier) {
        scene.setStepMultiplier(stepMultiplier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getStepMultiplier() {
        return scene.getStepMultiplier();
    }

    /**
     * {@inheritDoc}
     */
    public void setDotRadiusRange(@FloatRange(from = 0.5f) final float minRadius,
                                  @FloatRange(from = 0.5f) final float maxRadius) {
        scene.setDotRadiusRange(minRadius, maxRadius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getMinDotRadius() {
        return scene.getMinDotRadius();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getMaxDotRadius() {
        return scene.getMaxDotRadius();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineThickness(@FloatRange(from = 1) final float lineThickness) {
        scene.setLineThickness(lineThickness);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLineThickness() {
        return scene.getLineThickness();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineDistance(@FloatRange(from = 0) final float lineDistance) {
        scene.setLineDistance(lineDistance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLineDistance() {
        return scene.getLineDistance();
    }

    /**
     * {@inheritDoc}
     */
    public void setDensity(@IntRange(from = 0) final int newNum) {
        scene.setDensity(newNum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDensity() {
        return scene.getDensity();
    }

    /**
     * {@inheritDoc}
     */
    public void setDotColor(@ColorInt final int dotColor) {
        scene.setDotColor(dotColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDotColor() {
        return scene.getDotColor();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineColor(@ColorInt final int lineColor) {
        scene.setLineColor(lineColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLineColor() {
        return scene.getLineColor();
    }

    @Override
    public void requestRender() {
        invalidate();
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        engine.setDimensions(w, h);
    }

    @Override
    protected void onDraw(@NonNull final Canvas canvas) {
        super.onDraw(canvas);
        canvasSceneRenderer.setCanvas(canvas);
        engine.draw();
        engine.run();
        canvasSceneRenderer.setCanvas(null);
    }

    @Override
    public void scheduleNextFrame(final long delay) {
        postInvalidateDelayed(delay);
    }

    @Override
    public void unscheduleNextFrame() {

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
     * Start animating. This will clear the explicit control flag if set by {@link #stop()}.
     * Note that if this View's visibility is not {@link #VISIBLE} or it's not attached to window,
     * this will not start animating until the state changes to meet the requirements above.
     */
    @Override
    public void start() {
        mExplicitlyStopped = false;
        startInternal();
    }

    /**
     * Explicilty stop animating. This will stop animating and no animations will start
     * automatically until you call {@link #start()}.
     */
    @Override
    public void stop() {
        mExplicitlyStopped = true;
        stopInternal();
    }

    @Override
    public boolean isRunning() {
        return engine.isRunning();
    }

    @VisibleForTesting
    void startInternal() {
        if (!mExplicitlyStopped && isVisibleWithAllParents(this) && isAttachedToWindowCompat()) {
            engine.start();
        }
    }

    @VisibleForTesting
    void stopInternal() {
        engine.stop();
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
