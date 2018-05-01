package com.doctoror.particlesdrawable;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

final class ParticlesRendererView implements IParticlesView {

    private final float[] frame = new float[4];
    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(frame.length * 4);

    private GL10 gl;

    ParticlesRendererView() {
        byteBuffer.order(ByteOrder.nativeOrder());
    }

    public void setGl(@Nullable final GL10 gl) {
        this.gl = gl;
    }

    @Override
    public void drawLine(float startX, float startY, float stopX, float stopY, float strokeWidth, int color) {
        if (gl != null) {
            setColor(gl, color);

            frame[0] = startX;
            frame[1] = startY;
            frame[2] = stopX;
            frame[3] = stopY;

            final FloatBuffer buffer = byteBuffer.asFloatBuffer();
            buffer.put(frame);
            buffer.flip();
            buffer.position(0);

            gl.glVertexPointer( 2, GL10.GL_FLOAT, 0, byteBuffer);
            gl.glDrawArrays(GL10.GL_LINES, 0, 2);
        }
    }

    @Override
    public void fillCircle(float cx, float cy, float radius, int color) {
        if (gl != null) {
            setColor(gl, color);

            frame[0] = cx;
            frame[1] = cy;

            final FloatBuffer buffer = byteBuffer.asFloatBuffer();
            buffer.put(frame);
            buffer.flip();
            buffer.position(0);

            gl.glPointSize(radius * 2f);

            gl.glVertexPointer( 2, GL10.GL_FLOAT, 0, byteBuffer);
            gl.glDrawArrays(GL10.GL_POINTS, 0, 1);
        }
    }

    private void setColor(@NonNull final GL10 gl, @ColorInt final int color) {
        gl.glColor4f(
                Color.red(color) / 255f,
                Color.green(color) / 255f,
                Color.blue(color) / 255f,
                Color.alpha(color) / 255f);
    }
}
