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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.doctoror.particlesdrawable.contract.SceneConfiguration;
import com.doctoror.particlesdrawable.contract.SceneController;
import com.doctoror.particlesdrawable.contract.SceneScheduler;
import com.doctoror.particlesdrawable.renderer.GlSceneRenderer;
import com.doctoror.particlesdrawable.util.TextureUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Particles View that draws on {@link GLSurfaceView}.
 * <p/>
 * Due to it's limitations, use it only if it gives you measured performance gain. Otherwise it is recommented
 * to use {@link ParticlesView}.
 * <p/>
 * Limitations:
 * <ul>
 * <li>does not draw after onPause/onResume on some deivces. The same was reported here
 * <a href=https://stackoverflow.com/questions/14686815/blank-screen-after-onpause-onresume>Blank screen after
 * onPause-onResume</a>;</li>
 * <li>no background transparency;</li>
 * <li>textures are resized to POT, you can disable this with
 * {@link #setAutoScaleBackgroundToSmallerPot(boolean)} but it might be incompatible with some drivers;</li>
 * <li>line widths more than 1.0 are incompatible with most of the drivers;</li>
 * </ul>
 */
public class GlParticlesView extends GLSurfaceView implements
        SceneController,
        SceneConfiguration,
        SceneScheduler,
        GLSurfaceView.Renderer {

    private final ParticlesScene scene = new ParticlesScene();
    private final GlSceneRenderer renderer = new GlSceneRenderer();
    private final ScenePresenter presenter = new ScenePresenter(scene, renderer, this);

    private volatile boolean mBackgroundColorDirty;
    private volatile boolean mBackgroundTextureDirty;

    @ColorInt
    private int mBackgroundColor = Color.DKGRAY;

    private Bitmap mBackgroundTexture;

    private boolean autoScaleBackgroundToSmallerPot = true;

    public GlParticlesView(Context context) {
        super(context);
        init(context, null);
    }

    public GlParticlesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        setEGLContextClientVersion(2);
        setEGLConfigChooser(new MultisampleConfigChooser(2));
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        if (attrs != null) {
            @SuppressLint("CustomViewStyleable") final TypedArray a = context
                    .obtainStyledAttributes(attrs, R.styleable.ParticlesView);
            try {
                presenter.handleAttrs(a);
            } finally {
                a.recycle();
            }
        }

        final TypedArray array = context.getTheme()
                .obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        try {
            mBackgroundColor = array.getColor(0, Color.DKGRAY);
        } catch (Exception e) {
            Log.w("GlParticlesView", "Failed to obtain windowBackground", e);
        } finally {
            array.recycle();
        }

        renderer.markParticleTextureDirty();
    }

    /**
     * Set whether NPOT backgrounds should be auto scaled to the nearest smaller POT.
     * <p>
     * Default is true.
     *
     * @param autoScaleBackgroundToSmallerPot whether to auto scaled to POT
     */
    public void setAutoScaleBackgroundToSmallerPot(final boolean autoScaleBackgroundToSmallerPot) {
        this.autoScaleBackgroundToSmallerPot = autoScaleBackgroundToSmallerPot;
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
        mBackgroundColor = color;
        mBackgroundColorDirty = true;
    }

    /**
     * Applies background. Supported Drawables are {@link BitmapDrawable}, and {@link ColorDrawable} since API
     * Level 11.
     * <p/>
     * The background will stretch to fill the entire screen. If you need transformations, like center crop,
     * you should do it yourself before passing here. When {@link #setAutoScaleBackgroundToSmallerPot(boolean)}
     * is set to true (which is true by default), any NPOT images will be scaled to smaller POT.
     * <p/>
     * Setting a {@link ColorDrawable} will apply a background color and remove any previously set
     * {@link BitmapDrawable}.
     * <p/>
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
     * Applies background. Supported Drawables are {@link BitmapDrawable}, and {@link ColorDrawable} since API
     * Level 11.
     * <p/>
     * The background will stretch to fill the entire screen. If you need transformations, like center crop,
     * you should do it yourself before passing here. When {@link #setAutoScaleBackgroundToSmallerPot(boolean)}
     * is set to true (which is true by default), any NPOT images will be scaled to smaller POT.
     * <p/>
     * Setting a {@link ColorDrawable} will apply a background color and remove any previously set
     * {@link BitmapDrawable}.
     * <p/>
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

    private void processAndSetBackgroundTexture(@Nullable Bitmap texture) {
        if (texture != null && autoScaleBackgroundToSmallerPot) {
            texture = TextureUtils.scaleToSmallerPot(texture);
        }
        mBackgroundTexture = texture;
        mBackgroundTextureDirty = true;
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
    @Keep
    public void start() {
        onResume();
        presenter.start();
    }

    /**
     * Explicilty stop animating. This will stop animating and no animations will start
     * automatically until you call {@link #start()}.
     */
    @Keep
    public void stop() {
        presenter.stop();
        onPause();
        recycle();
    }

    private void recycle() {
        renderer.recycle();
    }

    @Override
    public void onSurfaceCreated(@NonNull final GL10 gl, @NonNull final EGLConfig config) {
        recycle();
        renderer.setupGl();
        mBackgroundColorDirty = true;
        mBackgroundTextureDirty = true;
    }

    @Override
    public void onSurfaceChanged(@NonNull final GL10 gl, final int width, final int height) {
        renderer.setDimensions(width, height);
        mBackgroundColorDirty = true;
        mBackgroundTextureDirty = true;
    }

    @Override
    public void onDrawFrame(@NonNull final GL10 gl) {
        if (mBackgroundColorDirty) {
            renderer.setClearColor(mBackgroundColor);
            mBackgroundColorDirty = false;
        }
        if (mBackgroundTextureDirty) {
            renderer.setBackgroundTexture(mBackgroundTexture);
            mBackgroundTextureDirty = false;
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
