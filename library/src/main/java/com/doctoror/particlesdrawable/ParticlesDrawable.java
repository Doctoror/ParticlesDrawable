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

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.doctoror.particlesdrawable.contract.SceneConfiguration;
import com.doctoror.particlesdrawable.contract.SceneController;
import com.doctoror.particlesdrawable.contract.SceneRenderer;
import com.doctoror.particlesdrawable.contract.SceneScheduler;
import com.doctoror.particlesdrawable.engine.Engine;
import com.doctoror.particlesdrawable.engine.SceneConfigurator;
import com.doctoror.particlesdrawable.model.Scene;
import com.doctoror.particlesdrawable.renderer.CanvasSceneRenderer;
import com.doctoror.particlesdrawable.renderer.DefaultSceneRenderer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * The Particles Drawable
 */
@Keep
public class ParticlesDrawable extends Drawable implements
        Animatable,
        SceneConfiguration,
        SceneController,
        SceneScheduler {

    // Fields below cannot be final because it would not allow replacing them with mocks for testing

    /**
     * @noinspection FieldMayBeFinal
     */
    private CanvasSceneRenderer canvasRenderer = new CanvasSceneRenderer();

    /**
     * @noinspection FieldMayBeFinal
     */
    private Scene scene = new Scene();

    /**
     * @noinspection FieldMayBeFinal
     */
    private SceneConfigurator sceneConfigurator = new SceneConfigurator();

    /**
     * @noinspection FieldMayBeFinal
     */
    private SceneRenderer renderer = new DefaultSceneRenderer(canvasRenderer);

    /**
     * @noinspection FieldMayBeFinal
     */
    private Engine engine = new Engine(scene, this, renderer);

    @Override
    public void inflate(
            @NonNull final Resources r,
            @NonNull final XmlPullParser parser,
            @NonNull final AttributeSet attrs,
            @Nullable final Resources.Theme theme) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);
        sceneConfigurator.configureSceneFromAttributes(scene, r, attrs);
    }

    @Override
    public void setBounds(final int left, final int top, final int right, final int bottom) {
        super.setBounds(left, top, right, bottom);
        engine.setDimensions(right - left, bottom - top);
    }

    @Override
    public void draw(@NonNull final Canvas canvas) {
        canvasRenderer.setCanvas(canvas);
        engine.draw();
        canvasRenderer.setCanvas(null);
        engine.run();
    }

    @Override
    public void scheduleNextFrame(final long delay) {
        if (delay == 0L) {
            requestRender();
        } else {
            scheduleSelf(invalidateSelfRunnable, SystemClock.uptimeMillis() + delay);
        }
    }

    @Override
    public void unscheduleNextFrame() {
        unscheduleSelf(invalidateSelfRunnable);
    }

    @Override
    public void requestRender() {
        invalidateSelf();
    }

    @Override
    public void setAlpha(final int alpha) {
        engine.setAlpha(alpha);
    }

    @Override
    public int getAlpha() {
        return engine.getAlpha();
    }

    @Override
    public void setColorFilter(@Nullable final ColorFilter colorFilter) {
        canvasRenderer.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void start() {
        engine.start();
    }

    @Override
    public void stop() {
        engine.stop();
    }

    @Override
    public boolean isRunning() {
        return engine.isRunning();
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
    public void makeFreshFrame() {
        engine.makeFreshFrame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeFreshFrameWithParticlesOffscreen() {
        engine.makeFreshFrameWithParticlesOffscreen();
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
    public void setSpeedFactor(@FloatRange(from = 0) final float speedFactor) {
        scene.setSpeedFactor(speedFactor);
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
    @Override
    public void setParticleRadiusRange(
            @FloatRange(from = 0.5f) final float minRadius,
            @FloatRange(from = 0.5f) final float maxRadius) {
        scene.setParticleRadiusRange(minRadius, maxRadius);
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
    @Override
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
    @Override
    public void setLineLength(@FloatRange(from = 0) final float lineLength) {
        scene.setLineLength(lineLength);
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
    public void setParticleColor(@ColorInt final int color) {
        scene.setParticleColor(color);
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
    @Override
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

    private final Runnable invalidateSelfRunnable = new Runnable() {
        @Override
        public void run() {
            invalidateSelf();
        }
    };
}
