package com.doctoror.particlesdrawable.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES11;
import android.opengl.GLUtils;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.doctoror.particlesdrawable.ParticlesScene;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL11;

final class GlSceneRendererParticles {

    private static final int BYTES_PER_SHORT = 2;
    private static final int COORDINATES_PER_VERTEX = 2;
    private static final int VERTICES_PER_PARTICLE = 6;

    private final int[] textureHandle = new int[1];

    private ShortBuffer particlesTrianglesCoordinates;
    private ByteBuffer particlesTexturesCoordinates;

    private volatile boolean textureDirty;

    void markTextureDirty() {
        textureDirty = true;
    }

    private void initBuffers(final int vertexCount) {
        initParticleTrianglesBuffer(vertexCount);
        initParticleTexturesBuffer(vertexCount);
    }

    void recycle() {
        GLES11.glDeleteTextures(1, textureHandle, 0);
    }

    private void initParticleTrianglesBuffer(final int vertexCount) {
        final int floatcapacity = vertexCount * COORDINATES_PER_VERTEX * VERTICES_PER_PARTICLE;
        if (particlesTrianglesCoordinates == null
                || particlesTrianglesCoordinates.capacity() != floatcapacity) {
            final ByteBuffer triangleShit = ByteBuffer.allocateDirect(floatcapacity * BYTES_PER_SHORT);
            triangleShit.order(ByteOrder.nativeOrder());
            particlesTrianglesCoordinates = triangleShit.asShortBuffer();
        }
    }

    private void initParticleTexturesBuffer(final int vertexCount) {
        final int capacity = vertexCount * COORDINATES_PER_VERTEX * VERTICES_PER_PARTICLE;
        if (particlesTexturesCoordinates == null
                || particlesTexturesCoordinates.capacity() != capacity) {
            particlesTexturesCoordinates = ByteBuffer.allocateDirect(capacity);
            particlesTexturesCoordinates.order(ByteOrder.nativeOrder());

            for (int i = 0; i < capacity; i += VERTICES_PER_PARTICLE) {
                particlesTexturesCoordinates.put((byte) 0);
                particlesTexturesCoordinates.put((byte) 1);
                particlesTexturesCoordinates.put((byte) 0);
                particlesTexturesCoordinates.put((byte) 0);
                particlesTexturesCoordinates.put((byte) 1);
                particlesTexturesCoordinates.put((byte) 0);
            }
        }
    }

    private void reloadTextureIfDirty(
            @ColorInt final int color,
            final float maxParticleRadius) {
        if (textureDirty) {
            generateAndLoadTexture(color, maxParticleRadius);
        }
    }

    private void generateAndLoadTexture(
            @ColorInt final int color,
            final float maxParticleRadius) {
        final Bitmap texture = generateParticleTexture(color, maxParticleRadius);
        loadTexture(texture);
        texture.recycle();
    }

    @NonNull
    private Bitmap generateParticleTexture(
            @ColorInt final int color,
            final float maxPointRadius) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setColor(color);

        final int size = (int) (maxPointRadius * 2f);
        final Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(maxPointRadius, maxPointRadius, maxPointRadius, paint);

        return bitmap;
    }

    private void loadTexture(@NonNull final Bitmap texture) {
        GLES11.glDeleteTextures(1, textureHandle, 0);

        GLES11.glGenTextures(1, textureHandle, 0);
        GLES11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle[0]);
        GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, texture, 0);

        GLES11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GLES11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GLES11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP_TO_EDGE);
        GLES11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP_TO_EDGE);

        GLES11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);
        textureDirty = false;
    }

    public void drawScene(@NonNull final ParticlesScene scene) {
        initBuffers(scene.getNumDots());
        reloadTextureIfDirty(scene.getDotColor(), scene.getMaxDotRadius());
        resolveParticleTriangles(scene);
        drawParticles(scene.getNumDots());
    }

    private void resolveParticleTriangles(@NonNull final ParticlesScene scene) {
        final FloatBuffer coordinates = scene.getCoordinates();
        coordinates.position(0);

        final FloatBuffer radiuses = scene.getRadiuses();
        radiuses.position(0);

        particlesTrianglesCoordinates.clear();

        final int count = scene.getNumDots();
        for (int i = 0; i < count; i++) {
            final float particleRadius = radiuses.get();

            final float coordX = coordinates.get() - particleRadius;
            final float coordY = coordinates.get() - particleRadius;

            final float particleSize = particleRadius * 2f;
            particlesTrianglesCoordinates.put((short) coordX);
            particlesTrianglesCoordinates.put((short) coordY);
            particlesTrianglesCoordinates.put((short) (coordX + particleSize));
            particlesTrianglesCoordinates.put((short) coordY);
            particlesTrianglesCoordinates.put((short) (coordX + particleSize));
            particlesTrianglesCoordinates.put((short) (coordY + particleSize));

            particlesTrianglesCoordinates.put((short) coordX);
            particlesTrianglesCoordinates.put((short) coordY);
            particlesTrianglesCoordinates.put((short) coordX);
            particlesTrianglesCoordinates.put((short) (coordY + particleSize));
            particlesTrianglesCoordinates.put((short) (coordX + particleSize));
            particlesTrianglesCoordinates.put((short) (coordY + particleSize));
        }
    }

    private void drawParticles(final int count) {
        GLES11.glEnable(GL11.GL_TEXTURE_2D);
        GLES11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        particlesTexturesCoordinates.position(0);
        particlesTrianglesCoordinates.position(0);

        GLES11.glTexCoordPointer(2, GL11.GL_BYTE, 0, particlesTexturesCoordinates);
        GLES11.glVertexPointer(2, GL11.GL_SHORT, 0, particlesTrianglesCoordinates);
        GLES11.glDrawArrays(GL11.GL_TRIANGLES, 0, count * VERTICES_PER_PARTICLE);

        GLES11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GLES11.glDisable(GL11.GL_TEXTURE_2D);
    }
}
