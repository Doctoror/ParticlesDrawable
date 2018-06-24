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
package com.doctoror.particlesdrawable.renderer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.doctoror.particlesdrawable.ParticlesScene;
import com.doctoror.particlesdrawable.contract.SceneRenderer;

public final class GlSceneRenderer implements SceneRenderer {

    private final GlSceneRendererBackground background = new GlSceneRendererBackground();
    private final GlSceneRendererParticles particles = new GlSceneRendererParticles();
    private final GlSceneRendererLines lines = new GlSceneRendererLines();

    private final float[] mvpMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private final int[] textureHandle = new int[2];

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

    public void setupGl() {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_DST_ALPHA);
        GLES20.glGenTextures(2, textureHandle, 0);

        markParticleTextureDirty();
        background.init(textureHandle[0]);
        lines.init();
        particles.init(textureHandle[1]);
    }

    public void setDimensions(final int width, final int height) {
        Matrix.orthoM(projectionMatrix, 0, 0f, width, 0f, height, 1, -1);

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 0f, 0f, 0f, -1f, 0f, 1f, 0f);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        background.setDimensions(width, height);
    }

    public void recycle() {
        GLES20.glDeleteTextures(2, textureHandle, 0);
    }

    @Override
    public void drawScene(
            @NonNull final ParticlesScene scene) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        background.drawScene(mvpMatrix);
        lines.drawScene(scene, mvpMatrix);
        particles.drawScene(scene, mvpMatrix);
    }
}
