package com.doctoror.particlesdrawable;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.doctoror.particlesdrawable.contract.SceneRenderer;
import com.doctoror.particlesdrawable.util.DistanceResolver;
import com.doctoror.particlesdrawable.util.LineColorResolver;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

final class GlSceneRenderer implements SceneRenderer {

    private static final int BYTES_PER_FLOAT = 4;
    private static final int COORDINATES_PER_VERTEX = 2;
    private static final int COLOR_BYTES_PER_VERTEX = 4;
    private static final int VERTICES_PER_LINE = 2;

    private FloatBuffer lineCoordinatesBuffer;
    private ByteBuffer lineColorBuffer;

    private int lineCount;

    private GL10 gl;

    public void setClearColor(
            @NonNull final GL10 gl,
            @ColorInt final int color) {
        gl.glClearColor(
                Color.red(color) / 255f,
                Color.green(color) / 255f,
                Color.blue(color) / 255f, 0f);
    }

    public void setupGl(
            @NonNull final GL10 gl,
            final int width,
            final int height) {
        setupViewport(gl, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glEnable(GL10.GL_LINE_SMOOTH);
        gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glOrthof(0, width, 0, height, 1, -1);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    }

    public void setupViewport(@NonNull final GL10 gl, final int width, final int height) {
        gl.glViewport(0, 0, width, height);
    }

    public void setGl(@Nullable final GL10 gl) {
        this.gl = gl;
    }

    private void initLineBuffers(final int vertexCount) {
        final int segmentsCount = segmentsCount(vertexCount);
        initLineCoordinates(segmentsCount);
        initLineColorBuffer(segmentsCount);
        lineCount = 0;
    }

    private void initLineCoordinates(final int segmentsCount) {
        final int floatCapacity = segmentsCount * VERTICES_PER_LINE * COORDINATES_PER_VERTEX;
        if (lineCoordinatesBuffer == null || lineCoordinatesBuffer.capacity() != floatCapacity) {
            final ByteBuffer coordinatesByteBuffer = ByteBuffer.allocateDirect(
                    floatCapacity * BYTES_PER_FLOAT);
            coordinatesByteBuffer.order(ByteOrder.nativeOrder());
            lineCoordinatesBuffer = coordinatesByteBuffer.asFloatBuffer();
        } else {
            lineCoordinatesBuffer.position(0);
        }
    }

    private void initLineColorBuffer(final int lineCount) {
        final int targetCapacity = lineCount * VERTICES_PER_LINE * COLOR_BYTES_PER_VERTEX;
        if (lineColorBuffer == null || lineColorBuffer.capacity() != targetCapacity) {
            lineColorBuffer = ByteBuffer.allocateDirect(targetCapacity);
            lineColorBuffer.order(ByteOrder.nativeOrder());
        } else {
            lineColorBuffer.position(0);
        }
    }

    private int segmentsCount(final int vertices) {
        return (vertices * (vertices - 1)) / 2;
    }

    @Override
    public void drawScene(
            @NonNull final ParticlesSceneProperties scene) {
        gl.glPointSize(scene.getMaxDotRadius() * 2f);
        gl.glLineWidth(scene.getLineThickness());

        resolveLines(scene);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        drawLines();
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        drawParticles(
                scene.getCoordinates(),
                scene.getDotColor(),
                scene.getParticlesCount()
        );
    }

    private void resolveLines(@NonNull final ParticlesSceneProperties scene) {
        final int particlesCount = scene.getParticlesCount();
        if (particlesCount != 0) {
            initLineBuffers(scene.getParticlesCount());
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
        if (gl != null) {
            lineCoordinatesBuffer.put(startX);
            lineCoordinatesBuffer.put(startY);
            lineCoordinatesBuffer.put(stopX);
            lineCoordinatesBuffer.put(stopY);

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
    }

    private void drawLines() {
        lineCoordinatesBuffer.position(0);
        lineColorBuffer.position(0);

        gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, lineColorBuffer);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, lineCoordinatesBuffer);
        gl.glDrawArrays(GL10.GL_LINES, 0, lineCount * 2);

        lineCoordinatesBuffer.position(0);
        lineColorBuffer.position(0);
    }

    private void drawParticles(
            @NonNull final FloatBuffer coordinates,
            @ColorInt final int color,
            final int count) {
        setColor(gl, color);

        coordinates.position(0);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, coordinates);
        gl.glDrawArrays(GL10.GL_POINTS, 0, count);

        coordinates.position(0);
    }

    private void setColor(@NonNull final GL10 gl, @ColorInt final int color) {
        gl.glColor4f(
                Color.red(color) / 255f,
                Color.green(color) / 255f,
                Color.blue(color) / 255f,
                Color.alpha(color) / 255f);
    }
}
