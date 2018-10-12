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
package com.doctoror.particlesdrawable.opengl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;

import com.doctoror.particlesdrawable.ParticlesScene;
import com.doctoror.particlesdrawable.ScenePresenter;
import com.doctoror.particlesdrawable.contract.SceneConfiguration;
import com.doctoror.particlesdrawable.contract.SceneController;
import com.doctoror.particlesdrawable.contract.SceneScheduler;
import com.doctoror.particlesdrawable.opengl.renderer.GlSceneRenderer;
import com.doctoror.particlesdrawable.opengl.util.MultisampleConfigChooser;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Particles View that draws on {@link GLSurfaceView}.
 */
@Keep
public class GlParticlesView extends GLSurfaceView implements
        Animatable,
        SceneController,
        SceneConfiguration,
        SceneScheduler,
        GLSurfaceView.Renderer {

    private static final int DEFAULT_SAMPLES = 4;

    private final ParticlesScene scene = new ParticlesScene();
    private final GlSceneRenderer renderer = new GlSceneRenderer();
    private final ScenePresenter presenter = new ScenePresenter(scene, this, renderer);

    private volatile boolean backgroundColorDirty;
    private volatile boolean backgroundTextureDirty;

    @ColorInt
    private volatile int backgroundColor = Color.DKGRAY;

    private volatile Bitmap backgroundTexture;

    public GlParticlesView(@NonNull final Context context) {
        super(context);
        init(context, null);
    }

    public GlParticlesView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        int samples = DEFAULT_SAMPLES;
        if (attrs != null) {
            @SuppressLint("CustomViewStyleable") final TypedArray a = context
                    .obtainStyledAttributes(attrs, R.styleable.ParticlesView);
            try {
                presenter.handleAttrs(a);
            } finally {
                a.recycle();
            }

            final TypedArray glAttrs = context
                    .obtainStyledAttributes(attrs, R.styleable.GlParticlesView);
            try {
                samples = glAttrs.getInt(R.styleable.GlParticlesView_multisampling, DEFAULT_SAMPLES);
            } finally {
                glAttrs.recycle();
            }
        }

        final TypedArray array = context.getTheme()
                .obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        try {
            backgroundColor = array.getColor(0, Color.DKGRAY);
        } catch (Exception e) {
            Log.w("GlParticlesView", "Failed to obtain windowBackground", e);
        } finally {
            array.recycle();
        }

        setEGLContextClientVersion(2);
        if (samples != 0) {
            setEGLConfigChooser(new MultisampleConfigChooser(samples));
        }
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        renderer.markParticleTextureDirty();
    }

    /**
     * Sets the background color for this View.
     * Default is windowBackground.
     * <p>
     * Will not affect textures set in {@link #setBackground(Drawable)}.
     *
     * @param color the background of this View.
     */
    @Override
    public void setBackgroundColor(@ColorInt final int color) {
        backgroundColor = color;
        backgroundColorDirty = true;
    }

    /**
     * Applies background. Supported Drawables are {@link BitmapDrawable}, and {@link ColorDrawable}
     * since API Level 11.
     * <p>
     * The background will stretch to fill the entire screen. If you need transformations, like
     * center crop, you should do it yourself before passing here.
     * <p>
     * Setting a {@link ColorDrawable} will apply a background color and remove any previously set
     * {@link BitmapDrawable}.
     * <p>
     * If you want to change the background color without affecting the {@link Bitmap} texture, use
     * {@link #setBackgroundColor(int)}
     *
     * @param background the background to apply
     * @throws IllegalArgumentException if the background is not a {@link BitmapDrawable} or
     *                                  {@link ColorDrawable} if on API level 11 or higher.
     */
    @Override
    public void setBackgroundDrawable(@Nullable final Drawable background) {
        applyBackground(background);
    }

    /**
     * Applies background. Supported Drawables are {@link BitmapDrawable}, and {@link ColorDrawable}
     * since API Level 11.
     * <p>
     * The background will stretch to fill the entire screen. If you need transformations, like
     * center crop, you should do it yourself before passing here.
     * <p>
     * Setting a {@link ColorDrawable} will apply a background color and remove any previously set
     * {@link BitmapDrawable}.
     * <p>
     * If you want to change the background color without affecting the {@link Bitmap} texture, use
     * {@link #setBackgroundColor(int)}
     *
     * @param background the background to apply
     * @throws IllegalArgumentException if the background is not a {@link BitmapDrawable} or
     *                                  {@link ColorDrawable} if on API level 11 or higher.
     */
    @Override
    public void setBackground(@Nullable final Drawable background) {
        applyBackground(background);
    }

    private void applyBackground(@Nullable final Drawable background) {
        if (background == null) {
            processAndSetBackgroundTexture(null);
        } else if (background instanceof BitmapDrawable) {
            processAndSetBackgroundTexture(((BitmapDrawable) background).getBitmap());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                && background instanceof ColorDrawable) {
            processAndSetBackgroundTexture(null);
            setBackgroundColor(((ColorDrawable) background).getColor());
        } else {
            throw new IllegalArgumentException(
                    "Only BitmapDrawable (sdk >= 9) or ColorDrawable (sdk >= 11) are supported");
        }
    }

    private void processAndSetBackgroundTexture(@Nullable final Bitmap texture) {
        backgroundTexture = texture;
        backgroundTextureDirty = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextFrame() {
        presenter.nextFrame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrame() {
        presenter.makeBrandNewFrame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrameWithPointsOffscreen() {
        presenter.makeBrandNewFrameWithPointsOffscreen();
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
        renderer.markParticleTextureDirty();
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
    public void setNumDots(@IntRange(from = 0) final int newNum) {
        scene.setNumDots(newNum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumDots() {
        return scene.getNumDots();
    }

    /**
     * {@inheritDoc}
     */
    public void setDotColor(@ColorInt final int dotColor) {
        scene.setDotColor(dotColor);
        renderer.markParticleTextureDirty();
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
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        presenter.setBounds(0, 0, w, h);
    }

    @Override
    public void scheduleNextFrame(final long delay) {
        if (delay == 0) {
            requestRender();
        } else {
            removeCallbacks(requestRenderRunnable);
            postDelayed(requestRenderRunnable, delay);
        }
    }

    @Override
    public void unscheduleNextFrame() {

    }

    /**
     * Start animating. This will clear the explicit control flag if set by {@link #stop()}.
     * Note that if this View's visibility is not {@link #VISIBLE} or it's not attached to window,
     * this will not start animating until the state changes to meet the requirements above.
     */
    @Override
    public void start() {
        onResume();
        presenter.start();
    }

    /**
     * Explicilty stop animating. This will stop animating and no animations will start
     * automatically until you call {@link #start()}.
     */
    @Override
    public void stop() {
        presenter.stop();
        onPause();
        recycle();
    }

    @Override
    public boolean isRunning() {
        return presenter.isRunning();
    }

    private void recycle() {
        renderer.recycle();
    }

    @Override
    public void onSurfaceCreated(@NonNull final GL10 gl, @NonNull final EGLConfig config) {
        recycle();
        renderer.setupGl();
        backgroundColorDirty = true;
        backgroundTextureDirty = true;
    }

    @Override
    public void onSurfaceChanged(@NonNull final GL10 gl, final int width, final int height) {
        renderer.setDimensions(width, height);
        backgroundColorDirty = true;
        backgroundTextureDirty = true;
    }

    @Override
    public void onDrawFrame(@NonNull final GL10 gl) {
        if (backgroundColorDirty) {
            renderer.setClearColor(backgroundColor);
            backgroundColorDirty = false;
        }
        if (backgroundTextureDirty) {
            renderer.setBackgroundTexture(backgroundTexture);
            backgroundTextureDirty = false;
        }
        presenter.draw();
        presenter.run();
    }

    private final Runnable requestRenderRunnable = new Runnable() {
        @Override
        public void run() {
            requestRender();
        }
    };
}
