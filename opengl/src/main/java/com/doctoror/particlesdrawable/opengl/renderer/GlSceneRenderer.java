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
package com.doctoror.particlesdrawable.opengl.renderer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.doctoror.particlesdrawable.KeepAsApi;
import com.doctoror.particlesdrawable.contract.SceneRenderer;
import com.doctoror.particlesdrawable.model.Scene;
import com.doctoror.particlesdrawable.opengl.util.GLErrorChecker;

import java.util.Arrays;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@KeepAsApi
public class GlSceneRenderer implements SceneRenderer {

    private final GlSceneRendererBackground background = new GlSceneRendererBackground();
    private final GlSceneRendererParticles particles = new GlSceneRendererParticles();
    private final GlSceneRendererLines lines = new GlSceneRendererLines();

    private final float[] mvpSourceMatrix = new float[16];
    private final float[] mvpTranslatedBackgroundMatrix = new float[16];
    private final float[] mvpTranslatedForegroundMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private final int[] textureHandle = new int[2];

    private float backgroundTranslationX;
    private float foregroundTranslationX;

    public void setBackgroundTranslationX(final float backgroundTranslationX) {
        this.backgroundTranslationX = backgroundTranslationX;
        Matrix.translateM(mvpTranslatedBackgroundMatrix, 0, mvpSourceMatrix, 0, backgroundTranslationX, 0, 0);
    }

    public void setForegroundTranslationX(final float foregroundTranslationX) {
        this.foregroundTranslationX = foregroundTranslationX;
        Matrix.translateM(mvpTranslatedForegroundMatrix, 0, mvpSourceMatrix, 0, foregroundTranslationX, 0, 0);
    }

    public void markParticleTextureDirty() {
        particles.markTextureDirty();
    }

    public void setClearColor(@ColorInt final int color) {
        GLES20.glClearColor(
                Color.red(color) / 255f,
                Color.green(color) / 255f,
                Color.blue(color) / 255f, 0f);
    }

    public void setBackgroundTexture(@Nullable final Bitmap texture) {
        background.setTexture(texture);
    }

    /**
     * Overrides background dimensions with the specified ones.
     * This change will be reset by next call to {@link #setDimensions(int, int)}.
     */
    public void overrideBackgroundDimensions(final int width, final int height) {
        background.setDimensions(width, height);
    }

    public void setupGl() {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glGenTextures(2, textureHandle, 0);

        markParticleTextureDirty();
        background.init(textureHandle[0]);
        lines.init();
        particles.init(textureHandle[1]);
    }

    public void setDimensions(final int width, final int height) {
        GLES20.glViewport(0, 0, width, height);

        Arrays.fill(projectionMatrix, 0);
        Arrays.fill(mvpSourceMatrix, 0);

        Matrix.orthoM(projectionMatrix, 0, 0f, width, 0f, height, 1, -1);

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 0f, 0f, 0f, -1f, 0f, 1f, 0f);
        Matrix.multiplyMM(mvpSourceMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        System.arraycopy(mvpSourceMatrix, 0, mvpTranslatedBackgroundMatrix, 0, mvpSourceMatrix.length);
        System.arraycopy(mvpSourceMatrix, 0, mvpTranslatedForegroundMatrix, 0, mvpSourceMatrix.length);

        Matrix.translateM(mvpTranslatedBackgroundMatrix, 0, mvpSourceMatrix, 0, backgroundTranslationX, 0, 0);
        Matrix.translateM(mvpTranslatedForegroundMatrix, 0, mvpSourceMatrix, 0, foregroundTranslationX, 0, 0);

        background.setDimensions(width, height);
    }

    public void recycle() {
        GLES20.glDeleteTextures(2, textureHandle, 0);
    }

    @Override
    public void drawScene(
            @NonNull final Scene scene) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        background.drawScene(mvpTranslatedBackgroundMatrix);
        lines.drawScene(scene, mvpTranslatedForegroundMatrix);
        particles.drawScene(scene, mvpTranslatedForegroundMatrix);
    }
}
