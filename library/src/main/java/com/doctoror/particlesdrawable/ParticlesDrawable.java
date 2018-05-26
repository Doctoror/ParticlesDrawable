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
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.doctoror.particlesdrawable.contract.SceneConfiguration;
import com.doctoror.particlesdrawable.contract.SceneController;
import com.doctoror.particlesdrawable.contract.SceneRenderer;
import com.doctoror.particlesdrawable.contract.SceneScheduler;
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

    private final CanvasSceneRenderer canvasRenderer = new CanvasSceneRenderer();
    private final ParticlesScene scene = new ParticlesScene();
    private final SceneRenderer renderer = new DefaultSceneRenderer(canvasRenderer);
    private final ScenePresenter presenter = new ScenePresenter(scene, renderer, this);

    @Override
    public void inflate(@NonNull final Resources r,
                        @NonNull final XmlPullParser parser,
                        @NonNull final AttributeSet attrs,
                        @Nullable final Resources.Theme theme) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);
        presenter.inflate(r, attrs);
    }

    @NonNull
    @Keep
    public Paint getPaint() {
        return canvasRenderer.getPaint();
    }

    @Override
    public void setBounds(final int left, final int top, final int right, final int bottom) {
        super.setBounds(left, top, right, bottom);
        presenter.setBounds(left, top, right, bottom);
    }

    @Override
    public void draw(@NonNull final Canvas canvas) {
        canvasRenderer.setCanvas(canvas);
        presenter.draw();
        canvasRenderer.setCanvas(null);
    }

    @Override
    public void scheduleNextFrame(final long delay) {
        scheduleSelf(presenter, SystemClock.uptimeMillis() + delay);
    }

    @Override
    public void unscheduleNextFrame() {
        unscheduleSelf(presenter);
    }

    @Override
    public void invalidate() {
        invalidateSelf();
    }

    @Override
    public void setAlpha(final int alpha) {
        presenter.setAlpha(alpha);
    }

    @Override
    public int getAlpha() {
        return presenter.getAlpha();
    }

    @Override
    public void setColorFilter(final ColorFilter colorFilter) {
        canvasRenderer.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void start() {
        presenter.start();
    }

    @Override
    public void stop() {
        presenter.stop();
    }

    @Override
    public boolean isRunning() {
        return presenter.isRunning();
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
    @Override
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
}
