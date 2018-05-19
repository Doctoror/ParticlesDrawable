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

import java.io.IOException;

/**
 * The Particles Drawable
 */
@Keep
public class ParticlesDrawable extends Drawable
        implements Animatable, SceneRenderer, SceneScheduler, ParticlesScene {

    private final CanvasSceneRenderer sceneRenderer = new CanvasSceneRenderer();
    private final SceneController sceneController = new SceneController(this, this);

    @Override
    public void inflate(@NonNull final Resources r,
            @NonNull final XmlPullParser parser,
            @NonNull final AttributeSet attrs,
            @Nullable final Resources.Theme theme) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);
        sceneController.inflate(r, attrs);
    }

    @NonNull
    @Keep
    public Paint getPaint() {
        return sceneRenderer.getPaint();
    }

    @Override
    public void setBounds(final int left, final int top, final int right, final int bottom) {
        super.setBounds(left, top, right, bottom);
        sceneController.setBounds(left, top, right, bottom);
    }

    @Override
    public void draw(@NonNull final Canvas canvas) {
        sceneRenderer.setCanvas(canvas);
        sceneController.draw();
        sceneRenderer.setCanvas(null);
    }

    @Override
    public void drawLine(final float startX, final float startY, final float stopX,
            final float stopY, final float strokeWidth, @ColorInt final int color) {
        sceneRenderer.drawLine(startX, startY, stopX, stopY, strokeWidth, color);
    }

    @Override
    public void fillCircle(final float cx, final float cy, final float radius,
            @ColorInt final int color) {
        sceneRenderer.fillCircle(cx, cy, radius, color);
    }

    @Override
    public void scheduleNextFrame(final long delay) {
        scheduleSelf(sceneController, SystemClock.uptimeMillis() + delay);
    }

    @Override
    public void unscheduleNextFrame() {
        unscheduleSelf(sceneController);
    }

    @Override
    public void invalidate() {
        invalidateSelf();
    }

    @Override
    public void setAlpha(final int alpha) {
        sceneController.setAlpha(alpha);
    }

    @Override
    public int getAlpha() {
        return sceneController.getAlpha();
    }

    @Override
    public void setColorFilter(final ColorFilter colorFilter) {
        sceneRenderer.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void start() {
        sceneController.start();
    }

    @Override
    public void stop() {
        sceneController.stop();
    }

    @Override
    public boolean isRunning() {
        return sceneController.isRunning();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextFrame() {
        sceneController.nextFrame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrame() {
        sceneController.makeBrandNewFrame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrameWithPointsOffscreen() {
        sceneController.makeBrandNewFrameWithPointsOffscreen();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFrameDelay(@IntRange(from = 0) final int delay) {
        sceneController.setFrameDelay(delay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFrameDelay() {
        return sceneController.getFrameDelay();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStepMultiplier(@FloatRange(from = 0) final float stepMultiplier) {
        sceneController.setStepMultiplier(stepMultiplier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getStepMultiplier() {
        return sceneController.getStepMultiplier();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDotRadiusRange(@FloatRange(from = 0.5f) final float minRadius,
            @FloatRange(from = 0.5f) final float maxRadius) {
        sceneController.setDotRadiusRange(minRadius, maxRadius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getMinDotRadius() {
        return sceneController.getMinDotRadius();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getMaxDotRadius() {
        return sceneController.getMaxDotRadius();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineThickness(@FloatRange(from = 1) final float lineThickness) {
        sceneController.setLineThickness(lineThickness);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLineThickness() {
        return sceneController.getLineThickness();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineDistance(@FloatRange(from = 0) final float lineDistance) {
        sceneController.setLineDistance(lineDistance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLineDistance() {
        return sceneController.getLineDistance();
    }

    /**
     * {@inheritDoc}
     */
    public void setNumDots(@IntRange(from = 0) final int newNum) {
        sceneController.setNumDots(newNum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumDots() {
        return sceneController.getNumDots();
    }

    /**
     * {@inheritDoc}
     */
    public void setDotColor(@ColorInt final int dotColor) {
        sceneController.setDotColor(dotColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDotColor() {
        return sceneController.getDotColor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineColor(@ColorInt final int lineColor) {
        sceneController.setLineColor(lineColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLineColor() {
        return sceneController.getLineColor();
    }
}
