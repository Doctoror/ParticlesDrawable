package com.doctoror.particlesdrawable;

import android.graphics.Color;
import android.support.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

final class ParticlesViewGl implements IParticlesView {

    private static final int BYTES_PER_SHORT = 2;
    private static final int COORDINATES_PER_VERTEX = 2;
    private static final int COLOR_BYTES_PER_VERTEX = 4;
    private static final int VERTICES_PER_LINE = 2;

    private int lineCount;
    private int dotCount;

    private ShortBuffer lineCoordinatesBuffer;
    private ShortBuffer dotCoordinatesBuffer;

    private ByteBuffer lineColorBuffer;
    private ByteBuffer dotColorBuffer;

    private GL10 gl;

    public void setGl(@Nullable final GL10 gl) {
        this.gl = gl;
    }

    public void beginTransaction(final int vertexCount) {
        final int segmentsCount = segmentsCount(vertexCount);
        initLineCoordinates(segmentsCount);
        initDotCoordinates(vertexCount);

        initLineColorBuffer(segmentsCount);
        initDotColorBuffer(vertexCount);

        lineCount = 0;
        dotCount = 0;
    }

    private void initLineCoordinates(final int segmentsCount) {
        final int shortCapacity = segmentsCount * VERTICES_PER_LINE * COORDINATES_PER_VERTEX;
        if (lineCoordinatesBuffer == null || lineCoordinatesBuffer.capacity() != shortCapacity) {
            final ByteBuffer coordinatesByteBuffer = ByteBuffer.allocateDirect(
                    shortCapacity * BYTES_PER_SHORT);
            coordinatesByteBuffer.order(ByteOrder.nativeOrder());
            lineCoordinatesBuffer = coordinatesByteBuffer.asShortBuffer();
        } else {
            lineCoordinatesBuffer.position(0);
        }
    }

    private void initDotCoordinates(final int dotCount) {
        final int shortCapacity = dotCount * COORDINATES_PER_VERTEX;
        if (dotCoordinatesBuffer == null || dotCoordinatesBuffer.capacity() != shortCapacity) {
            final ByteBuffer coordinatesByteBuffer = ByteBuffer.allocateDirect(
                    shortCapacity * BYTES_PER_SHORT);
            coordinatesByteBuffer.order(ByteOrder.nativeOrder());
            dotCoordinatesBuffer = coordinatesByteBuffer.asShortBuffer();
        } else {
            dotCoordinatesBuffer.position(0);
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

    private void initDotColorBuffer(final int dotCount) {
        final int targetCapacity = dotCount * COLOR_BYTES_PER_VERTEX;
        if (dotColorBuffer == null || dotColorBuffer.capacity() != targetCapacity) {
            dotColorBuffer = ByteBuffer.allocateDirect(targetCapacity);
            dotColorBuffer.order(ByteOrder.nativeOrder());
        } else {
            dotColorBuffer.position(0);
        }
    }

    private int segmentsCount(final int vertices) {
        return (vertices * (vertices - 1)) / 2;
    }

    @Override
    public void drawLine(float startX, float startY, float stopX, float stopY, float strokeWidth, int color) {
        if (gl != null) {
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
    }

    @Override
    public void fillCircle(float cx, float cy, float radius, int color) {
        if (gl != null) {
            dotCoordinatesBuffer.put((short) cx);
            dotCoordinatesBuffer.put((short) cy);

            dotColorBuffer.put((byte) Color.red(color));
            dotColorBuffer.put((byte) Color.green(color));
            dotColorBuffer.put((byte) Color.blue(color));
            dotColorBuffer.put((byte) Color.alpha(color));

            dotCount++;

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
        gl.glVertexPointer(2, GL10.GL_SHORT, 0, lineCoordinatesBuffer);
        gl.glDrawArrays(GL10.GL_LINES, 0, lineCount * 2);

        lineCoordinatesBuffer.position(0);
        lineColorBuffer.position(0);
    }

    private void drawDots() {
        dotCoordinatesBuffer.position(0);
        dotColorBuffer.position(0);

        gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, dotColorBuffer);
        gl.glVertexPointer(2, GL10.GL_SHORT, 0, dotCoordinatesBuffer);
        gl.glDrawArrays(GL10.GL_POINTS, 0, dotCount);

        dotCoordinatesBuffer.position(0);
        dotColorBuffer.position(0);
    }
}
