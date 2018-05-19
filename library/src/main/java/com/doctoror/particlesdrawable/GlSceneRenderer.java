package com.doctoror.particlesdrawable;

import android.graphics.Color;
import android.support.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

final class GlSceneRenderer implements SceneRenderer {

    private static final int BYTES_PER_FLOAT = 4;
    private static final int COORDINATES_PER_VERTEX = 2;
    private static final int COLOR_BYTES_PER_VERTEX = 4;
    private static final int VERTICES_PER_LINE = 2;

    private int lineCount;
    private int particlesCount;

    private FloatBuffer lineCoordinatesBuffer;
    private FloatBuffer particleCoordinatesBuffer;

    private ByteBuffer lineColorBuffer;
    private ByteBuffer particleColorBuffer;

    private GL10 gl;

    public void setGl(@Nullable final GL10 gl) {
        this.gl = gl;
    }

    public void beginTransaction(final int vertexCount) {
        final int segmentsCount = segmentsCount(vertexCount);
        initLineCoordinates(segmentsCount);
        initParticleCoordinates(vertexCount);

        initLineColorBuffer(segmentsCount);
        initParticleColorBuffer(vertexCount);

        lineCount = 0;
        particlesCount = 0;
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

    private void initParticleCoordinates(final int dotCount) {
        final int floatCapacity = dotCount * COORDINATES_PER_VERTEX;
        if (particleCoordinatesBuffer == null || particleCoordinatesBuffer.capacity() != floatCapacity) {
            final ByteBuffer coordinatesByteBuffer = ByteBuffer.allocateDirect(
                    floatCapacity * BYTES_PER_FLOAT);
            coordinatesByteBuffer.order(ByteOrder.nativeOrder());
            particleCoordinatesBuffer = coordinatesByteBuffer.asFloatBuffer();
        } else {
            particleCoordinatesBuffer.position(0);
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

    private void initParticleColorBuffer(final int dotCount) {
        final int targetCapacity = dotCount * COLOR_BYTES_PER_VERTEX;
        if (particleColorBuffer == null || particleColorBuffer.capacity() != targetCapacity) {
            particleColorBuffer = ByteBuffer.allocateDirect(targetCapacity);
            particleColorBuffer.order(ByteOrder.nativeOrder());
        } else {
            particleColorBuffer.position(0);
        }
    }

    private int segmentsCount(final int vertices) {
        return (vertices * (vertices - 1)) / 2;
    }

    @Override
    public void drawLine(float startX, float startY, float stopX, float stopY, float strokeWidth, int color) {
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

    @Override
    public void fillCircle(float cx, float cy, float radius, int color) {
        if (gl != null) {
            particleCoordinatesBuffer.put(cx);
            particleCoordinatesBuffer.put(cy);

            particleColorBuffer.put((byte) Color.red(color));
            particleColorBuffer.put((byte) Color.green(color));
            particleColorBuffer.put((byte) Color.blue(color));
            particleColorBuffer.put((byte) Color.alpha(color));

            particlesCount++;

            gl.glPointSize(radius * 2f);
        }
    }

    public void commit() {
        drawLines();
        drawDots();
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

    private void drawDots() {
        particleCoordinatesBuffer.position(0);
        particleColorBuffer.position(0);

        gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, particleColorBuffer);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, particleCoordinatesBuffer);
        gl.glDrawArrays(GL10.GL_POINTS, 0, particlesCount);

        particleCoordinatesBuffer.position(0);
        particleColorBuffer.position(0);
    }
}
