package com.doctoror.particlesdrawable.renderer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES11;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.doctoror.particlesdrawable.ParticlesScene;
import com.doctoror.particlesdrawable.contract.SceneRenderer;

import javax.microedition.khronos.opengles.GL11;

public final class GlSceneRenderer implements SceneRenderer {

    private final GlSceneRendererBackground background = new GlSceneRendererBackground();
    private final GlSceneRendererParticles particles = new GlSceneRendererParticles();
    private final GlSceneRendererLines lines = new GlSceneRendererLines();

    public void markParticleTextureDirty() {
        particles.markTextureDirty();
    }

    public void setClearColor(@ColorInt final int color) {
        GLES11.glClearColor(
                Color.red(color) / 255f,
                Color.green(color) / 255f,
                Color.blue(color) / 255f, 0f);
    }

    public void setBackgroundTexture(@Nullable final Bitmap texture) {
        background.setTexture(texture);
    }

    public void setupGl() {
        GLES11.glMatrixMode(GL11.GL_PROJECTION);
        GLES11.glLoadIdentity();

        GLES11.glEnable(GL11.GL_LINE_SMOOTH);
        GLES11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        GLES11.glEnable(GL11.GL_BLEND);
        GLES11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GLES11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

        markParticleTextureDirty();
    }

    public void setDimensions(final int width, final int height) {
        GLES11.glViewport(0, 0, width, height);
        GLES11.glOrthof(0, width, 0, height, 1, -1);

        background.setDimensions(width, height);
    }

    public void recycle() {
        particles.recycle();
        background.recycle();
    }

    @Override
    public void drawScene(
            @NonNull final ParticlesScene scene) {
        GLES11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        background.drawBackground();
        lines.drawScene(scene);
        particles.drawScene(scene);
    }
}
