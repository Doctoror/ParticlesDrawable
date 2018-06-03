package com.doctoror.particlesdrawable.renderer;

import android.graphics.Color;
import android.opengl.GLES11;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.doctoror.particlesdrawable.ParticlesScene;
import com.doctoror.particlesdrawable.contract.SceneRenderer;
import com.doctoror.particlesdrawable.util.DistanceResolver;
import com.doctoror.particlesdrawable.util.LineColorResolver;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL11;

public final class GlSceneRenderer implements SceneRenderer {

    private static final int BYTES_PER_SHORT = 2;
    private static final int COORDINATES_PER_VERTEX = 2;
    private static final int COLOR_BYTES_PER_VERTEX = 4;
    private static final int VERTICES_PER_LINE = 2;

    private final GlSceneRendererParticles particles = new GlSceneRendererParticles();

    private ByteBuffer lineColorBuffer;
    private ShortBuffer lineCoordinatesBuffer;

    private int lineCount;

    public void markTextureDirty() {
        particles.markTextureDirty();
    }

    public void setClearColor(@ColorInt final int color) {
        GLES11.glClearColor(
                Color.red(color) / 255f,
                Color.green(color) / 255f,
                Color.blue(color) / 255f, 0f);
    }

    public void setupGl() {
        GLES11.glMatrixMode(GL11.GL_PROJECTION);
        GLES11.glLoadIdentity();

        GLES11.glEnable(GL11.GL_LINE_SMOOTH);
        GLES11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        GLES11.glEnable(GL11.GL_BLEND);
        GLES11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GLES11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

        markTextureDirty();
    }

    public void setDimensions(final int width, final int height) {
        GLES11.glViewport(0, 0, width, height);
        GLES11.glOrthof(0, width, 0, height, 1, -1);
    }

    public void recycle() {
        particles.recycle();
    }

    private void initLineBuffers(final int vertexCount) {
        final int segmentsCount = segmentsCount(vertexCount);
        initLineCoordinates(segmentsCount);
        initLineColorBuffer(segmentsCount);
        lineCount = 0;
    }

    private int segmentsCount(final int vertices) {
        return (vertices * (vertices - 1)) / 2;
    }

    private void initLineCoordinates(final int segmentsCount) {
        final int shortcapacity = segmentsCount * VERTICES_PER_LINE * COORDINATES_PER_VERTEX;
        if (lineCoordinatesBuffer == null || lineCoordinatesBuffer.capacity() != shortcapacity) {
            final ByteBuffer coordinatesByteBuffer = ByteBuffer.allocateDirect(
                    shortcapacity * BYTES_PER_SHORT);
            coordinatesByteBuffer.order(ByteOrder.nativeOrder());
            lineCoordinatesBuffer = coordinatesByteBuffer.asShortBuffer();
        }
    }

    private void initLineColorBuffer(final int lineCount) {
        final int targetCapacity = lineCount * VERTICES_PER_LINE * COLOR_BYTES_PER_VERTEX;
        if (lineColorBuffer == null || lineColorBuffer.capacity() != targetCapacity) {
            lineColorBuffer = ByteBuffer.allocateDirect(targetCapacity);
            lineColorBuffer.order(ByteOrder.nativeOrder());
        }
    }

    @Override
    public void drawScene(
            @NonNull final ParticlesScene scene) {
        GLES11.glLineWidth(scene.getLineThickness());

        initLineBuffers(scene.getNumDots());
        resolveLines(scene);

        GLES11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        drawLines();
        particles.drawScene(scene);
    }

    private void resolveLines(@NonNull final ParticlesScene scene) {
        lineColorBuffer.clear();
        lineCoordinatesBuffer.clear();

        final int particlesCount = scene.getNumDots();
        if (particlesCount != 0) {
            for (int i = 0; i < particlesCount; i++) {

                final float x1 = scene.getParticleX(i);
                final float y1 = scene.getParticleY(i);

                // Draw connection lines for eligible points
                for (int j = i + 1; j < particlesCount; j++) {

                    final float x2 = scene.getParticleX(j);
                    final float y2 = scene.getParticleY(j);

                    final float distance = DistanceResolver.distance(x1, y1, x2, y2);
                    if (distance < scene.getLineDistance()) {
                        final int lineColor = LineColorResolver.resolveLineColorWithAlpha(
                                scene.getAlpha(),
                                scene.getLineColor(),
                                scene.getLineDistance(),
                                distance);

                        resolveLine(
                                x1,
                                y1,
                                x2,
                                y2,
                                lineColor);
                    }
                }
            }
        }
    }

    private void resolveLine(
            final float startX,
            final float startY,
            final float stopX,
            final float stopY,
            @ColorInt final int color) {
        lineCoordinatesBuffer.put((short) startX);
        lineCoordinatesBuffer.put((short) startY);
        lineCoordinatesBuffer.put((short) stopX);
        lineCoordinatesBuffer.put((short) stopY);

        lineColorBuffer.put((byte) Color.red(color));
        lineColorBuffer.put((byte) Color.green(color));
        lineColorBuffer.put((byte) Color.blue(color));
        lineColorBuffer.put((byte) Color.alpha(color));

        lineColorBuffer.put((byte) Color.red(color));
        lineColorBuffer.put((byte) Color.green(color));
        lineColorBuffer.put((byte) Color.blue(color));
        lineColorBuffer.put((byte) Color.alpha(color));

        lineCount++;
    }

    private void drawLines() {
        lineCoordinatesBuffer.position(0);
        lineColorBuffer.position(0);

        GLES11.glEnableClientState(GL11.GL_COLOR_ARRAY);

        GLES11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 0, lineColorBuffer);
        GLES11.glVertexPointer(2, GL11.GL_SHORT, 0, lineCoordinatesBuffer);
        GLES11.glDrawArrays(GL11.GL_LINES, 0, lineCount * 2);

        GLES11.glDisableClientState(GL11.GL_COLOR_ARRAY);
    }
}
