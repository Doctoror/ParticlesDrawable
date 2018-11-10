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

import com.doctoror.particlesdrawable.contract.SceneConfiguration;
import com.doctoror.particlesdrawable.contract.SceneController;
import com.doctoror.particlesdrawable.contract.SceneScheduler;
import com.doctoror.particlesdrawable.engine.Engine;
import com.doctoror.particlesdrawable.engine.SceneConfigurator;
import com.doctoror.particlesdrawable.model.Scene;
import com.doctoror.particlesdrawable.opengl.chooser.EGLConfigChooserCallback;
import com.doctoror.particlesdrawable.opengl.chooser.FailsafeEGLConfigChooserFactory;
import com.doctoror.particlesdrawable.opengl.renderer.GlSceneRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

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

    final Scene scene;
    private final SceneConfigurator sceneConfigurator;
    final GlSceneRenderer renderer;
    final Engine engine;

    private volatile boolean backgroundColorDirty;
    private volatile boolean backgroundTextureDirty;

    @ColorInt
    private volatile int backgroundColor = Color.DKGRAY;

    private volatile Bitmap backgroundTexture;

    public GlParticlesView(@NonNull final Context context) {
        this(context, null);
    }

    public GlParticlesView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, DEFAULT_SAMPLES, null);
    }

    public GlParticlesView(
            @NonNull final Context context,
            @Nullable final AttributeSet attrs,
            final int samples,
            @Nullable final EGLConfigChooserCallback eglConfigChooserCallback) {
        super(context, attrs);
        this.scene = new Scene();
        this.sceneConfigurator = new SceneConfigurator();
        this.renderer = new GlSceneRenderer();
        this.engine = new Engine(scene, this, renderer);
        init(context, attrs, samples, eglConfigChooserCallback);
    }

    @VisibleForTesting
    GlParticlesView(
            @NonNull final Context context,
            @Nullable final AttributeSet attrs,
            @NonNull final Engine engine,
            @NonNull final Scene scene,
            @NonNull final SceneConfigurator sceneConfigurator,
            @NonNull final GlSceneRenderer sceneRenderer,
            final int samples,
            @Nullable final EGLConfigChooserCallback eglConfigChooserCallback) {
        super(context, attrs);
        this.engine = engine;
        this.scene = scene;
        this.sceneConfigurator = sceneConfigurator;
        this.renderer = sceneRenderer;
        init(context, attrs, samples, eglConfigChooserCallback);
    }

    private void init(
            @NonNull final Context context,
            @Nullable final AttributeSet attrs,
            int samples,
            @Nullable final EGLConfigChooserCallback configChooserCallback) {
        if (attrs != null) {
            sceneConfigurator.configureSceneFromAttributes(scene, context.getResources(), attrs);

            final TypedArray glAttrs = context
                    .obtainStyledAttributes(attrs, R.styleable.GlParticlesView);
            try {
                samples = glAttrs.getInt(R.styleable.GlParticlesView_multisampling, samples);
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
        setEGLConfigChooser(FailsafeEGLConfigChooserFactory
                .newFailsafeEGLConfigChooser(samples, configChooserCallback));
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
        queueEvent(new Runnable() {
            @Override
            public void run() {
                engine.nextFrame();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeFreshFrame() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                engine.makeFreshFrame();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeFreshFrameWithParticlesOffscreen() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                engine.makeFreshFrameWithParticlesOffscreen();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFrameDelay(@IntRange(from = 0) final int delay) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                scene.setFrameDelay(delay);
            }
        });
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
    public void setSpeedFactor(@FloatRange(from = 0) final float speedFactor) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                scene.setSpeedFactor(speedFactor);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getSpeedFactor() {
        return scene.getSpeedFactor();
    }

    /**
     * {@inheritDoc}
     */
    public void setParticleRadiusRange(
            @FloatRange(from = 0.5f) final float minRadius,
            @FloatRange(from = 0.5f) final float maxRadius) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                scene.setParticleRadiusRange(minRadius, maxRadius);
                renderer.markParticleTextureDirty();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getParticleRadiusMin() {
        return scene.getParticleRadiusMin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getParticleRadiusMax() {
        return scene.getParticleRadiusMax();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineThickness(@FloatRange(from = 1) final float lineThickness) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                scene.setLineThickness(lineThickness);
            }
        });
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
    public void setLineLength(@FloatRange(from = 0) final float lineLength) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                scene.setLineLength(lineLength);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLineLength() {
        return scene.getLineLength();
    }

    /**
     * {@inheritDoc}
     */
    public void setDensity(@IntRange(from = 0) final int newNum) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                scene.setDensity(newNum);
            }
        });
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
    public void setParticleColor(@ColorInt final int color) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                scene.setParticleColor(color);
                renderer.markParticleTextureDirty();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getParticleColor() {
        return scene.getParticleColor();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineColor(@ColorInt final int lineColor) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                scene.setLineColor(lineColor);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLineColor() {
        return scene.getLineColor();
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
        engine.start();
    }

    /**
     * Explicilty stop animating. This will stop animating and no animations will start
     * automatically until you call {@link #start()}.
     */
    @Override
    public void stop() {
        engine.stop();
        onPause();
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.recycle();
            }
        });
    }

    @Override
    public boolean isRunning() {
        return engine.isRunning();
    }

    @Override
    public void onSurfaceCreated(@NonNull final GL10 gl, @NonNull final EGLConfig config) {
        renderer.recycle();
        renderer.setupGl();
        backgroundColorDirty = true;
        backgroundTextureDirty = true;
    }

    @Override
    public void onSurfaceChanged(@NonNull final GL10 gl, final int width, final int height) {
        engine.setDimensions(width, height);
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
        engine.draw();
        engine.run();
    }

    private final Runnable requestRenderRunnable = new Runnable() {
        @Override
        public void run() {
            requestRender();
        }
    };
}
